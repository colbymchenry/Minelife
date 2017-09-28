package com.minelife.realestate.network;

import com.minelife.Minelife;
import com.minelife.realestate.EnumPermission;
import com.minelife.realestate.Estate;
import com.minelife.region.server.Region;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
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
            if (estate != null) {
                try {
                    Minelife.NETWORK.sendTo(new PacketOpenEstateGui(estate.getPermissionsAllowedToChange()), player);
                } catch (SQLException e) {
                    e.printStackTrace();
                    player.addChatComponentMessage(new ChatComponentText(Minelife.default_error_message));
                }
            } else {
                Set<EnumPermission> permissions = new TreeSet<>();
                permissions.addAll(Arrays.asList(EnumPermission.values()));
                Minelife.NETWORK.sendTo(new PacketOpenEstateGui(permissions), player);
            }
            return null;
        }
    }
}
