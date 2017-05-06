package com.minelife.gun.packet;

import com.minelife.gun.BaseGun;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class PacketReload implements IMessage {

    public PacketReload() {
    }

    @Override
    public void fromBytes(ByteBuf buf) {
    }

    @Override
    public void toBytes(ByteBuf buf) {
    }

    public static class Handler implements IMessageHandler<PacketReload, IMessage> {

        @Override
        public IMessage onMessage(PacketReload message, MessageContext ctx) {
            EntityPlayer player = ctx.getServerHandler().playerEntity;
            ItemStack heldItem = player.getHeldItem();
            if(heldItem != null && heldItem.getItem() instanceof BaseGun) {
                BaseGun.reload(player, heldItem);
            }
            return null;
        }

    }

}
