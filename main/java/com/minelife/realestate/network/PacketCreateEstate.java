package com.minelife.realestate.network;

import com.minelife.Minelife;
import com.minelife.economy.ModEconomy;
import com.minelife.realestate.Estate;
import com.minelife.realestate.EstateProperty;
import com.minelife.realestate.ModRealEstate;
import com.minelife.realestate.PlayerPermission;
import com.minelife.realestate.server.CommandEstate;
import com.minelife.realestate.server.SelectionListener;
import com.minelife.util.NumberConversions;
import com.minelife.util.StringHelper;
import com.minelife.util.client.PacketPopup;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
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
import java.util.Set;
import java.util.UUID;

public class PacketCreateEstate implements IMessage {

    private Estate estate;

    public PacketCreateEstate() {
    }

    public PacketCreateEstate(Estate estate) {
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
        ByteBufUtils.writeUTF8String(buf, this.estate.getUniqueID().toString());
        ByteBufUtils.writeTag(buf, this.estate.getTagCompound());
    }

    public static class Handler implements IMessageHandler<PacketCreateEstate, IMessage> {

        @SideOnly(Side.SERVER)
        public IMessage onMessage(PacketCreateEstate message, MessageContext ctx) {
            FMLServerHandler.instance().getServer().addScheduledTask(() -> {
                EntityPlayerMP player = ctx.getServerHandler().player;

                if (!CommandEstate.creationCheck(player, false, true)) return;

                BlockPos min = SelectionListener.getMinimum(player), max = SelectionListener.getMaximum(player);
                long width = Math.abs(max.getX() - min.getX());
                long length = Math.abs(max.getZ() - min.getZ());
                long height = Math.abs(max.getY() - min.getY());
                long area = width * length * height;
                long price = area * ModRealEstate.getConfig().getInt("price_per_block", 2);

                if(ModEconomy.getBalanceInventory(player) < price) {
                    PacketPopup.sendPopup(TextFormatting.RED + "Insufficient funds! Price: " + TextFormatting.DARK_RED + "$" + NumberConversions.format(price), player);
                    return;
                }

                if (message.estate.getRentPrice() > 0 && message.estate.getRentPeriod() < 1) {
                    PacketPopup.sendPopup("Rent period must be greater than 1 if rent price is greater than 1.", player);
                    return;
                }

                if(ModRealEstate.getEstate(message.estate.getUniqueID()) != null) {
                    PacketPopup.sendPopup("An estate with that unique ID already exists.", player);
                    return;
                }

                if (message.estate.getIntro().trim().isEmpty()) message.estate.setIntro(null);
                if (message.estate.getOutro().trim().isEmpty()) message.estate.setOutro(null);
                message.estate.setOwnerID(player.getUniqueID());
                message.estate.setMinimum(SelectionListener.getMinimum(player));
                message.estate.setMaximum(SelectionListener.getMaximum(player));
                message.estate.setWorld(player.getEntityWorld());

                // check properties
                Set<EstateProperty> properties = message.estate.getProperties();
                for (EstateProperty property : EstateProperty.values()) {
                    if(!CommandEstate.getEstateProperties(player.getUniqueID()).contains(property))
                        properties.remove(property);
                }
                message.estate.setProperties(properties);

                // check permissions
                Set<PlayerPermission> globalPermissions = message.estate.getGlobalPermissions();
                Set<PlayerPermission> renterPermissions = message.estate.getRenterPermissions();
                for (PlayerPermission playerPermission : PlayerPermission.values()) {
                    if(!CommandEstate.getPlayerPermissions(player.getUniqueID()).contains(playerPermission)) {
                        globalPermissions.remove(playerPermission);
                        renterPermissions.remove(playerPermission);
                    }
                }
                message.estate.setGlobalPermissions(globalPermissions);
                message.estate.setRenterPermissions(renterPermissions);
                message.estate.setIdentifier(StringHelper.randomAlphaNumeric(5));

                try {
                    message.estate.save();
                } catch (SQLException e) {
                    e.printStackTrace();
                    PacketPopup.sendPopup("Error writing to database.", player);
                    return;
                }


                ModEconomy.withdrawInventory(player, (int) price);
                ModRealEstate.getLoadedEstates().add(message.estate);
                player.closeScreen();
                player.sendMessage(new TextComponentString(TextFormatting.LIGHT_PURPLE + "[RealEstate]" + TextFormatting.GOLD + " Estate created!"));
                SelectionListener.removeSelection(player);
                Minelife.getNetwork().sendTo(new PacketSelection(null, null), player);
            });
            return null;
        }

    }

}
