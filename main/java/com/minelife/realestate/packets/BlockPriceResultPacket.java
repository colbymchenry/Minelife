package com.minelife.realestate.packets;

import com.minelife.realestate.Packet;
import com.minelife.realestate.client.renderer.SelectionRenderer;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;

public class BlockPriceResultPacket extends Packet {

    private long price;

    public BlockPriceResultPacket() { }

    public BlockPriceResultPacket(long price) {
        this.price = price;
    }

    @Override
    public Side sideOfHandling() {
        return Side.CLIENT;
    }

    @Override
    public void handle(MessageContext ctx) {
        SelectionRenderer.setPricePerBlock(this.price);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.price = buf.readLong();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(this.price);
    }

}