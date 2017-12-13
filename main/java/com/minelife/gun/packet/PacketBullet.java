package com.minelife.gun.packet;

import com.minelife.Minelife;
import com.minelife.gun.bullets.Bullet;
import com.minelife.gun.bullets.BulletHandler;
import com.minelife.gun.item.guns.ItemGun;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;

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

            if (Minecraft.getMinecraft().thePlayer == null) return null;

            if (message.bullet.shooter != null && Minecraft.getMinecraft().thePlayer != null) {
                if (message.bullet.shooter.getEntityId() == Minecraft.getMinecraft().thePlayer.getEntityId()) {
                    if(Minecraft.getMinecraft().currentScreen != null) return null;

                    ItemStack heldItem = Minecraft.getMinecraft().thePlayer.getHeldItem();

                    if (heldItem == null || !(heldItem.getItem() instanceof ItemGun)) return null;

                    if (ItemGun.getCurrentClipHoldings(heldItem) < 1) return null;
                    ((ItemGun) heldItem.getItem()).getClientHandler().shootBullet();
                }

                Vec3 shooterVec = Vec3.createVectorHelper(message.bullet.shooter.posX, message.bullet.shooter.posY, message.bullet.shooter.posZ);
                Vec3 receiverVec = Vec3.createVectorHelper(Minecraft.getMinecraft().thePlayer.posX, Minecraft.getMinecraft().thePlayer.posY, Minecraft.getMinecraft().thePlayer.posZ);
                float volume = (float) Math.min(1, 1 / receiverVec.distanceTo(shooterVec));
                volume = volume > 1 ? 1 : volume;

                Minecraft.getMinecraft().thePlayer.playSound(Minelife.MOD_ID + ":guns." + ((ItemGun) message.bullet.shooter.getHeldItem().getItem()).getName() + ".shot", volume, 1F);
            }

            return null;
        }
    }
}
