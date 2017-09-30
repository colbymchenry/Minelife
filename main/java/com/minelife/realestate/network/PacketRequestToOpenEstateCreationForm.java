package com.minelife.realestate.network;

import com.minelife.Minelife;
import com.minelife.realestate.EnumPermission;
import com.minelife.realestate.Estate;
import com.minelife.region.server.Region;
import com.minelife.util.client.PacketPopupMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.Vec3;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

public class PacketRequestToOpenEstateCreationForm implements IMessage {

    public PacketRequestToOpenEstateCreationForm()
    {
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {

    }

    @Override
    public void toBytes(ByteBuf buf)
    {

    }

    public static class Handler implements IMessageHandler<PacketRequestToOpenEstateCreationForm, IMessage> {

        @SideOnly(Side.SERVER)
        public IMessage onMessage(PacketRequestToOpenEstateCreationForm message, MessageContext ctx)
        {
            EntityPlayerMP player = ctx.getServerHandler().playerEntity;
            Region region = Region.getRegionAt(player.getEntityWorld(), Vec3.createVectorHelper(player.posX, player.posY, player.posZ));
            if (region == null) {
                player.addChatComponentMessage(new ChatComponentText("Could not find a region at your current location."));
                return null;
            }

            Estate estate = Estate.estates.stream().filter(e -> e.getRegion().equals(region)).findFirst().orElse(null);

            if(estate == null) {
                Region r = region.getParentRegion();
                while(estate == null) {
                    if(r == null) break;
                    for (Estate e : Estate.estates) {
                        if(e.getRegion().equals(r)) estate = e;
                    }
                    r = region.getParentRegion();
                }
            }

            if (estate != null) {
                try {
                    if(estate.getOwner().equals(player.getUniqueID()) || (estate.getRenter() != null && estate.getRenter().equals(player.getUniqueID()))) {
                        Minelife.NETWORK.sendTo(new PacketOpenEstateInfoGui(estate, estate.getPermissionsAllowedToChange(player.getUniqueID()), estate.getPermissionsAllowedToChangeEnabled(), estate.showPermsAllowedToChange(player.getUniqueID())), player);
                    } else {
                        Minelife.NETWORK.sendTo(new PacketPopupMessage("You are not allowed to view this estates info.", 0xC6C6C6), player);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    player.addChatComponentMessage(new ChatComponentText(Minelife.default_error_message));
                }
            } else {
                Set<EnumPermission> permissions = new TreeSet<>();
                permissions.addAll(Arrays.asList(EnumPermission.values()));
                Minelife.NETWORK.sendTo(new PacketOpenEstateCreationGui(permissions), player);
            }
            return null;
        }
    }
}
