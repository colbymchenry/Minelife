package com.minelife.locks;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

public class PacketInteract implements IMessage {

    private int x, y, z, face;

    public PacketInteract() {
    }

    public PacketInteract(int x, int y, int z, int face) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.face = face;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        x = buf.readInt();
        y = buf.readInt();
        z = buf.readInt();
        face = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
        buf.writeInt(face);
    }

    public static class Handler implements IMessageHandler<PacketInteract, IMessage> {

        @SideOnly(Side.SERVER)
        public IMessage onMessage(PacketInteract message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().playerEntity;
            PlayerInteractEvent interactEvent = new PlayerInteractEvent(player, PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK, message.x, message.y, message.z, message.face, player.getEntityWorld());
            MinecraftForge.EVENT_BUS.post(interactEvent);
            return null;
        }
    }

}
