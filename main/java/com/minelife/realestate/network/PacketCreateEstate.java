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
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.Vec3;

import java.sql.SQLException;
import java.util.Set;
import java.util.TreeSet;

public class PacketCreateEstate implements IMessage {

    private Set<EnumPermission> permissions;
    private Set<EnumPermission> permissionsAllowedToChange;
    private double purchasePrice, rentPrice;
    private int rentPeriodInDays;
    private boolean forRent;

    public PacketCreateEstate(Set<EnumPermission> permissions, Set<EnumPermission> permissionsAllowedToChange, double purchasePrice, double rentPrice, int rentPeriodInDays, boolean forRent)
    {
        this.permissions = permissions;
        this.permissionsAllowedToChange = permissionsAllowedToChange;
        this.purchasePrice = purchasePrice;
        this.rentPrice = rentPrice;
        this.rentPeriodInDays = rentPeriodInDays;
        this.forRent = forRent;
    }

    public PacketCreateEstate()
    {
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        permissions = new TreeSet<>();
        permissionsAllowedToChange = new TreeSet<>();
        int permsSize = buf.readInt();
        for(int i = 0; i < permsSize; i++) permissions.add(EnumPermission.values()[buf.readInt()]);
        int permsAllowedToChangeSize = buf.readInt();
        for(int i = 0; i < permsAllowedToChangeSize; i++) permissionsAllowedToChange.add(EnumPermission.values()[buf.readInt()]);
        purchasePrice = buf.readDouble();
        rentPrice = buf.readDouble();
        rentPeriodInDays = buf.readInt();
        forRent = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(permissions.size());
        permissions.forEach(p -> buf.writeInt(p.ordinal()));
        buf.writeInt(permissionsAllowedToChange.size());
        permissionsAllowedToChange.forEach(p -> buf.writeInt(p.ordinal()));
        buf.writeDouble(purchasePrice);
        buf.writeDouble(rentPrice);
        buf.writeInt(rentPeriodInDays);
        buf.writeBoolean(forRent);
    }

    public static class Handler implements IMessageHandler<PacketCreateEstate, IMessage> {

        @SideOnly(Side.SERVER)
        public IMessage onMessage(PacketCreateEstate message, MessageContext ctx)
        {
            EntityPlayerMP player = ctx.getServerHandler().playerEntity;
            Region region = Region.getRegionAt(player.worldObj, Vec3.createVectorHelper(player.posX, player.posY, player.posZ));

            if(region == null) {
                Minelife.NETWORK.sendTo(new PacketPopupMessage("Region not found at your location.", 0xC6C6C6), player);
                return null;
            }

            try {
                Minelife.SQLITE.query("INSERT INTO estates (region, owner) VALUES ('" + region.getUniqueID().toString() + "', '" + player.getUniqueID().toString() + "')");
                Estate estate = new Estate(region);
                estate.setOwner(player.getUniqueID());
                estate.setForRent(message.forRent);
                estate.setPermissions(message.permissions);
                estate.setPermissionsAllowedToChange(message.permissionsAllowedToChange);
                estate.setPurchasePrice(message.purchasePrice);
                estate.setRentPrice(message.rentPrice);
                estate.setRentPeriodInDays(message.rentPeriodInDays);
                Estate.estates.add(estate);
                player.closeScreen();
                player.addChatComponentMessage(new ChatComponentText("Estate created!"));
            } catch (Exception e) {
                e.printStackTrace();
                player.addChatComponentMessage(new ChatComponentText(Minelife.default_error_message));
            }
            return null;
        }
    }
}
