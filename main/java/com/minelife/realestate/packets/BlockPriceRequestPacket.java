package com.minelife.realestate.packets;

import com.minelife.Minelife;
import com.minelife.realestate.ModRealEstate;
import com.minelife.realestate.Packet;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;

public class BlockPriceRequestPacket extends Packet {

    public BlockPriceRequestPacket() { }

    @Override
    public Side sideOfHandling() {
        return Side.SERVER;
    }

    @Override
    public void handle(MessageContext ctx) {
        Minelife.NETWORK.sendTo(new BlockPriceResultPacket(ModRealEstate.config.getLong("price_per_block")), ctx.getServerHandler().playerEntity);
    }

    @Override
    public void fromBytes(ByteBuf buf) {

    }

    @Override
    public void toBytes(ByteBuf buf) {

    }

}