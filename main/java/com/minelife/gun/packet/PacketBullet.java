package com.minelife.gun.packet;

import com.minelife.Minelife;
import com.minelife.gun.bullets.Bullet;
import com.minelife.gun.bullets.BulletHandler;
import com.minelife.gun.client.guns.ItemGunClient;
import com.minelife.gun.item.guns.ItemGun;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;

import java.util.Random;

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
            message.bullet.world.spawnParticle("smoke", message.bullet.target.xCoord, message.bullet.target.yCoord, message.bullet.target.zCoord, 0, 0, 0);
            BulletHandler.bulletList.add(message.bullet);

            if (Minecraft.getMinecraft().thePlayer == null) return null;

            if (message.bullet.shooter != null && Minecraft.getMinecraft().thePlayer != null) {
                if (message.bullet.shooter.getEntityId() == Minecraft.getMinecraft().thePlayer.getEntityId()) {
                    if (Minecraft.getMinecraft().currentScreen != null) return null;

                    ItemStack heldItem = Minecraft.getMinecraft().thePlayer.getHeldItem();

                    if (heldItem == null || !(heldItem.getItem() instanceof ItemGun)) return null;

                    if (ItemGun.getCurrentClipHoldings(heldItem) < 1) return null;
                    ItemGunClient gunClient = ((ItemGun) heldItem.getItem()).getClientHandler();
                    ((ItemGun) heldItem.getItem()).getClientHandler().shootBullet();
                    if (!gunClient.shot) {
                        gunClient.ogYaw = Minecraft.getMinecraft().thePlayer.rotationYaw;
                        gunClient.ogPitch = Minecraft.getMinecraft().thePlayer.rotationPitch;
                        gunClient.shot = true;
                    }

                    Minecraft.getMinecraft().thePlayer.setAngles(
                            (float) MathHelper.getRandomDoubleInRange(gunClient.random, gunClient.yawSpread()[0],
                                    gunClient.yawSpread()[1]) * getLeftOrRight(gunClient.random),
                            (float) MathHelper.getRandomDoubleInRange(gunClient.random, gunClient.pitchSpread()[0],
                                    gunClient.pitchSpread()[1]));
                }

                Vec3 shooterVec = Vec3.createVectorHelper(message.bullet.shooter.posX, message.bullet.shooter.posY, message.bullet.shooter.posZ);
                Vec3 receiverVec = Vec3.createVectorHelper(Minecraft.getMinecraft().thePlayer.posX, Minecraft.getMinecraft().thePlayer.posY, Minecraft.getMinecraft().thePlayer.posZ);
                float volume = (float) Math.min(1, 1 / receiverVec.distanceTo(shooterVec));
                volume = volume > 1 ? 1 : volume;

                Minecraft.getMinecraft().thePlayer.playSound(Minelife.MOD_ID + ":guns." + ((ItemGun) message.bullet.shooter.getHeldItem().getItem()).getName() + ".shot", volume, 1F);
            } else if (message.bullet.shooter == null) {
                Vec3 shooterVec = Vec3.createVectorHelper(message.bullet.origin.xCoord, message.bullet.origin.yCoord, message.bullet.origin.zCoord);
                Vec3 receiverVec = Vec3.createVectorHelper(Minecraft.getMinecraft().thePlayer.posX, Minecraft.getMinecraft().thePlayer.posY, Minecraft.getMinecraft().thePlayer.posZ);
                float volume = (float) Math.min(1, 1 / receiverVec.distanceTo(shooterVec));
                volume = volume > 1 ? 1 : volume;

                Minecraft.getMinecraft().thePlayer.playSound(Minelife.MOD_ID + ":guns.turret_shot", volume, 1F);
            }

            return null;
        }

        public static int getLeftOrRight(Random random) {
            int[] i = new int[]{1, -1};
            return i[random.nextInt(2)];
        }
    }
}
