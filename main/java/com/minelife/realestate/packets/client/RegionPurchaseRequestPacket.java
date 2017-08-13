package com.minelife.realestate.packets.client;

import com.minelife.realestate.util.Util;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.AxisAlignedBB;

public class RegionPurchaseRequestPacket implements IMessage {

    AxisAlignedBB selection;
    long price;

    public RegionPurchaseRequestPacket(AxisAlignedBB selection, long price) {
        this.selection = selection;
        this.price = price;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.selection = Util.boundingBoxDeserialize(ByteBufUtils.readUTF8String(buf));
        this.price = buf.readLong();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, this.selection.toString());
        buf.writeLong(this.price);
    }

    public static class Handler implements IMessageHandler<RegionPurchaseRequestPacket, IMessage> {

        @Override
        public IMessage onMessage(RegionPurchaseRequestPacket packet, MessageContext ctx) {

            return null;
        }

    }

}