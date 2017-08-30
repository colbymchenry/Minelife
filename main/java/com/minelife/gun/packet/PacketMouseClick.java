package com.minelife.gun.packet;

import com.google.common.collect.Maps;
import com.minelife.gun.item.guns.ItemGun;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;

import java.util.Map;

public class PacketMouseClick implements IMessage {


    public PacketMouseClick() {
    }


    @Override
    public void fromBytes(ByteBuf buf) {
    }

    @Override
    public void toBytes(ByteBuf buf) {
    }

    public static class Handler implements IMessageHandler<PacketMouseClick, IMessage> {

        @Override
        public IMessage onMessage(PacketMouseClick message, MessageContext ctx) {
            EntityPlayer player = ctx.getServerHandler().playerEntity;

            if(player.getHeldItem() != null && player.getHeldItem().getItem() instanceof ItemGun) {
                ItemGun gun =  ((ItemGun) player.getHeldItem().getItem());
                gun.shootBullet(player, player.getHeldItem());
            }

            return null;
        }
    }

}
