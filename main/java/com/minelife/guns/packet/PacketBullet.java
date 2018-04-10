package com.minelife.guns.packet;

import com.minelife.guns.Bullet;
import com.minelife.guns.ModGuns;
import com.minelife.guns.client.RenderGun;
import com.minelife.guns.item.EnumGun;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

public class PacketBullet implements IMessage {

    private Bullet bullet;
    private double posX, posY, posZ, speed, damage;
    private Vec3d lookVec;
    private int entityID;
    private EnumGun gunType;

    public PacketBullet() {
    }

    public PacketBullet(EnumGun gunType, Bullet bullet) {
        this.gunType = gunType;
        this.bullet = bullet;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        posX = buf.readDouble();
        posY = buf.readDouble();
        posZ = buf.readDouble();
        lookVec = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
        speed = buf.readDouble();
        damage = buf.readDouble();
        gunType = EnumGun.values()[buf.readInt()];
        if (buf.readBoolean()) entityID = buf.readInt();
        else entityID = -1;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeDouble(bullet.posX);
        buf.writeDouble(bullet.posY);
        buf.writeDouble(bullet.posZ);
        buf.writeDouble(bullet.lookVec.x);
        buf.writeDouble(bullet.lookVec.y);
        buf.writeDouble(bullet.lookVec.z);
        buf.writeDouble(bullet.bulletSpeed);
        buf.writeDouble(bullet.bulletDamage);
        buf.writeInt(gunType.ordinal());
        buf.writeBoolean(bullet.shooter != null);
        if (bullet.shooter != null)
            buf.writeInt(bullet.shooter.getEntityId());
    }

    public static class Handler implements IMessageHandler<PacketBullet, IMessage> {

        @SideOnly(Side.CLIENT)
        public IMessage onMessage(PacketBullet message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                EntityPlayer player = Minecraft.getMinecraft().player;
                if (message.entityID != player.getEntityId()) {
                    Bullet.BULLETS.add(new Bullet(Minecraft.getMinecraft().world, message.posX, message.posY, message.posZ, 0,
                            message.lookVec, message.speed, message.damage, (EntityLivingBase) player.getEntityWorld().getEntityByID(message.entityID)));

                    /**
                     * Volume calculations for bullets
                     */
                    Vec3d shooterVec = new Vec3d(message.posX, message.posY, message.posZ);
                    Vec3d receiverVec = new Vec3d(player.posX, player.posY, player.posZ);
                    float volume = (float) Math.min(1, 3 / receiverVec.distanceTo(shooterVec));
                    volume = volume > 1 ? 1 : volume;

                    player.getEntityWorld().playSound(player.posX, player.posY, player.posZ, new SoundEvent(message.gunType.soundShot), SoundCategory.NEUTRAL, volume, 1, false);
                }
            });
            return null;
        }
    }

}
