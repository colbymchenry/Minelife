package com.minelife.realestate.network;

import com.minelife.notifications.Notification;
import com.minelife.notifications.NotificationType;
import com.minelife.realestate.Estate;
import com.minelife.realestate.EstateProperty;
import com.minelife.realestate.ModRealEstate;
import com.minelife.realestate.PlayerPermission;
import com.minelife.realestate.server.CommandEstate;
import com.minelife.realestate.server.SelectionListener;
import com.minelife.util.PlayerHelper;
import com.minelife.util.client.PacketPopup;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.server.FMLServerHandler;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class PacketUpdateEstate implements IMessage {

    private Estate estate;

    public PacketUpdateEstate() {
    }

    public PacketUpdateEstate(Estate estate) {
        this.estate = estate;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        UUID id = UUID.fromString(ByteBufUtils.readUTF8String(buf));
        NBTTagCompound tagCompound = ByteBufUtils.readTag(buf);
        this.estate = new Estate(id, tagCompound);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, estate.getUniqueID().toString());
        ByteBufUtils.writeTag(buf, estate.getTagCompound());
    }

    public static class Handler implements IMessageHandler<PacketUpdateEstate, IMessage> {

        @SideOnly(Side.SERVER)
        public IMessage onMessage(PacketUpdateEstate message, MessageContext ctx) {
            FMLServerHandler.instance().getServer().addScheduledTask(() -> {
                EntityPlayerMP player = ctx.getServerHandler().player;

                if (message.estate.getRentPrice() > 0 && message.estate.getRentPeriod() < 1) {
                    PacketPopup.sendPopup("Rent period must be greater than 1 if rent price is greater than 1.", player);
                    return;
                }

                if(message.estate.getRentPrice() < 1 && message.estate.getRentPeriod() < 1) {
                    if(message.estate.getRenterID() != null) {
                        Notification notification = new Notification(message.estate.getRenterID(), TextFormatting.DARK_RED + "You have been evicted!", NotificationType.EDGED, 5, 0xFFFFFF);
                        if(PlayerHelper.getPlayer(message.estate.getRenterID()) != null) {
                            notification.sendTo(PlayerHelper.getPlayer(message.estate.getRenterID()), true, true, false);
                        } else {
                            try {
                                notification.save();
                            } catch (SQLException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    message.estate.setRenterID(null);
                }

                if (message.estate.getIntro().trim().isEmpty()) message.estate.setIntro(null);
                if (message.estate.getOutro().trim().isEmpty()) message.estate.setOutro(null);

                // check properties
                Set<EstateProperty> properties = message.estate.getProperties();
                for (EstateProperty property : EstateProperty.values()) {
                    if(!CommandEstate.getEstateProperties(player.getUniqueID()).contains(property))
                        properties.remove(property);
                }
                message.estate.setProperties(properties);

                Estate loadedEstate = ModRealEstate.getEstate(message.estate.getUniqueID());

                Set<PlayerPermission> playerPermissions = loadedEstate.getPlayerPermissions(player.getUniqueID());
                // check permissions
                Set<PlayerPermission> globalPermissions = message.estate.getGlobalPermissions();
                Set<PlayerPermission> renterPermissions = message.estate.getRenterPermissions();
                for (PlayerPermission playerPermission : PlayerPermission.values()) {
                    if(!CommandEstate.getPlayerPermissions(player.getUniqueID()).contains(playerPermission)) {
                        globalPermissions.remove(playerPermission);
                        renterPermissions.remove(playerPermission);
                    }
                }

                Iterator<PlayerPermission> globalIter = globalPermissions.iterator();
                while(globalIter.hasNext()) if(!playerPermissions.contains(globalIter.next())) globalIter.remove();

                Iterator<PlayerPermission> renterIter = renterPermissions.iterator();
                while(renterIter.hasNext()) if(!playerPermissions.contains(renterIter.next())) renterIter.remove();

                message.estate.setGlobalPermissions(globalPermissions);
                message.estate.setRenterPermissions(renterPermissions);

                if(!Objects.equals(loadedEstate.getRenterID(), player.getUniqueID()) &&
                        !Objects.equals(loadedEstate.getOwnerID(), player.getUniqueID())) {
                    PacketPopup.sendPopup(TextFormatting.DARK_RED + "You are not allowed to modify this estate.", player);
                    return;
                }

                try {
                    message.estate.save();
                } catch (SQLException e) {
                    e.printStackTrace();
                    PacketPopup.sendPopup("Error writing to database.", player);
                    return;
                }

                ModRealEstate.getLoadedEstates().remove(loadedEstate);
                ModRealEstate.getLoadedEstates().add(message.estate);
                player.closeScreen();
                player.sendMessage(new TextComponentString(TextFormatting.LIGHT_PURPLE + "[RealEstate]" + TextFormatting.GOLD + " Estate updated!"));
            });
            return null;
        }
    }

}
