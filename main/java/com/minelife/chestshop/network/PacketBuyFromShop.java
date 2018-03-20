package com.minelife.chestshop.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.server.FMLServerHandler;

public class PacketBuyFromShop implements IMessage {

    private BlockPos pos;
    private int amount;

    public PacketBuyFromShop() {
    }

    public PacketBuyFromShop(BlockPos pos, int amount) {
        this.pos = pos;
        this.amount = amount;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.pos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
        this.amount = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.pos.getX());
        buf.writeInt(this.pos.getY());
        buf.writeInt(this.pos.getZ());
        buf.writeInt(this.amount);
    }


    public static class Handler implements IMessageHandler<PacketBuyFromShop, IMessage> {

        // TODO
        @SideOnly(Side.SERVER)
        public IMessage onMessage(PacketBuyFromShop message, MessageContext ctx) {
            FMLServerHandler.instance().getServer().addScheduledTask(() -> {

            });
            return null;
        }

    }
}
