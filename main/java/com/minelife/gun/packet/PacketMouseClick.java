package com.minelife.gun.packet;

import com.minelife.gun.item.guns.ItemGun;
import com.minelife.gun.server.ShootBulletEvent;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;

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

                ShootBulletEvent shotEvent = new ShootBulletEvent(player, player.getHeldItem());
                MinecraftForge.EVENT_BUS.post(shotEvent);

                if (shotEvent.isCanceled()) return null;

                ItemGun gun =  ((ItemGun) player.getHeldItem().getItem());
                gun.shootBullet(player, player.getHeldItem());
            }

            return null;
        }
    }

}
