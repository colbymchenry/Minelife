package com.minelife.realestate.network;

import com.minelife.realestate.client.SelectionRenderer;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketSelection implements IMessage {

    private BlockPos min, max;

    public PacketSelection() {
    }

    public PacketSelection(BlockPos min, BlockPos max) {
        this.min = min;
        this.max = max;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        boolean minNull = buf.readBoolean();
        boolean maxNull = buf.readBoolean();
        if(!minNull) this.min = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
        if(!maxNull) this.max = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(this.min == null);
        buf.writeBoolean(this.max == null);
        if(this.min != null) {
            buf.writeInt(this.min.getX());
            buf.writeInt(this.min.getY());
            buf.writeInt(this.min.getZ());
        }
        if(this.max != null) {
            buf.writeInt(this.max.getX());
            buf.writeInt(this.max.getY());
            buf.writeInt(this.max.getZ());
        }
    }

    public static class Handler implements IMessageHandler<PacketSelection, IMessage> {

        @SideOnly(Side.CLIENT)
        public IMessage onMessage(PacketSelection message, MessageContext ctx) {
            SelectionRenderer.MIN = message.min;
            SelectionRenderer.MAX = message.max;
            return null;
        }

    }
}
