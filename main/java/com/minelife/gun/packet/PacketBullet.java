package com.minelife.gun.packet;

import com.minelife.Minelife;
import com.minelife.bullets.Bullet;
import com.minelife.bullets.BulletHandler;
import com.minelife.gun.item.guns.ItemGun;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import org.lwjgl.input.Mouse;

public class PacketBullet implements IMessage {

    private Bullet bullet;

    public PacketBullet() {
    }

    public PacketBullet(Bullet bullet) {
        this.bullet = bullet;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        bullet = Bullet.fromBytes(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        bullet.toBytes(buf);
    }

    public static class Handler implements IMessageHandler<PacketBullet, IMessage> {

        @SideOnly(Side.CLIENT)
        public IMessage onMessage(PacketBullet message, MessageContext ctx) {
            BulletHandler.bulletList.add(message.bullet);
            if(message.bullet.shooter.getEntityId() == Minecraft.getMinecraft().thePlayer.getEntityId()) {
                if(Minecraft.getMinecraft().thePlayer == null) return null;

                ItemStack heldItem = Minecraft.getMinecraft().thePlayer.getHeldItem();

                if(heldItem == null || !(heldItem.getItem() instanceof ItemGun)) return null;

                if (ItemGun.getCurrentClipHoldings(heldItem) < 1) return null;

                if(Minecraft.getMinecraft().currentScreen != null) return null;

                ItemGun gun = (ItemGun) heldItem.getItem();
                gun.getClientHandler().shootBullet();
            }
            return null;
        }
    }
}
