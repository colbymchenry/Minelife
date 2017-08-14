package com.minelife.realestate.packets.client;

import com.minelife.economy.ModEconomy;
import com.minelife.realestate.server.Estate;
import com.minelife.region.server.Region;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.AxisAlignedBB;

public class RegionPurchaseRequestPacket implements IMessage {

    private AxisAlignedBB selection;
    private long price;

    public RegionPurchaseRequestPacket() {
    }

    public RegionPurchaseRequestPacket(AxisAlignedBB selection, long price) {
        this.selection = selection;
        this.price = price;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.selection = AxisAlignedBB.getBoundingBox(buf.readDouble(), buf.readDouble(), buf.readDouble(), buf.readDouble(), buf.readDouble(), buf.readDouble());
        this.price = buf.readLong();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeDouble(this.selection.minX);
        buf.writeDouble(this.selection.minY);
        buf.writeDouble(this.selection.minZ);
        buf.writeDouble(this.selection.maxX);
        buf.writeDouble(this.selection.maxY);
        buf.writeDouble(this.selection.maxZ);
        buf.writeLong(this.price);
    }

    public static class Handler implements IMessageHandler<RegionPurchaseRequestPacket, IMessage> {

        @Override
        public IMessage onMessage(RegionPurchaseRequestPacket packet, MessageContext ctx) {

            // TODO: Create separate class to handle buying and selling of estates

            Region region = null;
            Estate estate = null;
            try {
                if (ModEconomy.getBalance(ctx.getServerHandler().playerEntity.getUniqueID(), true) >= packet.price) {
                    region = Region.create(ctx.getServerHandler().playerEntity.worldObj.getWorldInfo().getWorldName(), packet.selection);
                    estate = Estate.create(region, ctx.getServerHandler().playerEntity.getUniqueID(), "First Estate");

                    // TODO: Create Gui to Purchase Estate.

                    // TODO: Test
                    {
                        System.out.println(ctx.getServerHandler().playerEntity.getDisplayName() + " purchased an estate (uuid = " + estate.getUniqueID() + ", name = " + estate.getName() + ").");
                        Region.delete(region.getUniqueID());
                        Estate.delete(estate.getUniqueID());
                    }

                } else {
                    System.out.println("Not enough money to purchase estate.");
                }
            } catch (Exception e) {
                System.out.println("Couldn't instantiate new Estate.");
                e.printStackTrace();
                try {
                    if (region != null) Region.delete(region.getUniqueID());
                    if (estate != null) Estate.delete(estate.getUniqueID());
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }

            return null;

        }

    }

    // ---- New Packet System Style ---- //

//    private AxisAlignedBB selection;
//    private long price;
//
//    public RegionPurchaseRequestPacket() {
//    }
//
//    public RegionPurchaseRequestPacket(AxisAlignedBB selection, long price) {
//        this.selection = selection;
//        this.price = price;
//    }
//
//    @Override
//    public Side sideOfHandling() {
//        return Side.SERVER;
//    }
//
//    @Override
//    public void handle(MessageContext ctx) {
//
//        try {
//            if (ModEconomy.getBalance(ctx.getServerHandler().playerEntity.getUniqueID(), true) >= this.price) {
//                Region region = Region.create(ctx.getServerHandler().playerEntity.worldObj.getWorldInfo().getWorldName(), this.selection);
//                Estate estate = Estate.create(region, ctx.getServerHandler().playerEntity.getUniqueID());
//
//                // TODO: Test
//                System.out.println(ctx.getServerHandler().playerEntity.getDisplayName() + " purchased an estate (uuid = " + estate.getUniqueID() + ").");
//                Region.delete(region.getUniqueID());
//                Estate.delete(estate.getUniqueID());
//
//            } else {
//                System.out.println("Not enough money to purchase estate.");
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    @Override
//    public void fromBytes(ByteBuf buf) {
//        this.selection = AxisAlignedBB.getBoundingBox(buf.readDouble(), buf.readDouble(), buf.readDouble(), buf.readDouble(), buf.readDouble(), buf.readDouble());
//        this.price = buf.readLong();
//    }
//
//    @Override
//    public void toBytes(ByteBuf buf) {
//        buf.writeDouble(this.selection.minX);
//        buf.writeDouble(this.selection.minY);
//        buf.writeDouble(this.selection.minZ);
//        buf.writeDouble(this.selection.maxX);
//        buf.writeDouble(this.selection.maxY);
//        buf.writeDouble(this.selection.maxZ);
//        buf.writeLong(this.price);
//    }

}