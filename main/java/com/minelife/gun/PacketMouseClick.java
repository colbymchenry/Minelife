package com.minelife.gun;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;

public class PacketMouseClick implements IMessage {

    private boolean rightMouse;

    public PacketMouseClick() {
    }

    public PacketMouseClick(boolean rightMouse) {
        this.rightMouse = rightMouse;
    }


    @Override
    public void fromBytes(ByteBuf buf) {
        this.rightMouse = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(this.rightMouse);
    }

    public static class Handler implements IMessageHandler<PacketMouseClick, IMessage> {

        @SideOnly(Side.SERVER)
        public IMessage onMessage(PacketMouseClick message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().playerEntity;

            if(player.getHeldItem() != null && player.getHeldItem().getItem() instanceof ItemGun) {
                ((ItemGun) player.getHeldItem().getItem()).shootBullet(player, player.getHeldItem());
            }

            return null;
        }
    }

}
