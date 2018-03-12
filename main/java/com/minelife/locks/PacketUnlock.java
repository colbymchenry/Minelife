package com.minelife.locks;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;

public class PacketUnlock implements IMessage {

    private int blockX, blockY, blockZ;

    public PacketUnlock() {
    }

    public PacketUnlock(int blockX, int blockY, int blockZ) {
        this.blockX = blockX;
        this.blockY = blockY;
        this.blockZ = blockZ;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        blockX = buf.readInt();
        blockY = buf.readInt();
        blockZ = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(blockX);
        buf.writeInt(blockY);
        buf.writeInt(blockZ);
    }

    public static class Handler implements IMessageHandler<PacketUnlock, IMessage> {

        @SideOnly(Side.CLIENT)
        public IMessage onMessage(PacketUnlock message, MessageContext ctx) {
            World world = Minecraft.getMinecraft().theWorld;
            if(world.getTileEntity(message.blockX, message.blockY, message.blockZ) == null) return null;

            TileEntityLock tileLock = (TileEntityLock) world.getTileEntity(message.blockX, message.blockY, message.blockZ);

            world.getBlock(tileLock.protectX, tileLock.protectY, tileLock.protectZ).
                    onBlockActivated(world, tileLock.protectX, tileLock.protectY, tileLock.protectZ,
                    Minecraft.getMinecraft().thePlayer, 3, 0.5f, 0.5f, 0.5f);
            return null;
        }
    }
}
