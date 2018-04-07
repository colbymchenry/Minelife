package com.minelife.guns.packet;

import com.minelife.guns.Bullet;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketBullet implements IMessage {

    private Bullet bullet;
    private double posX, posY, posZ, speed, damage;
    private Vec3d lookVec;
    private int entityID;

    public PacketBullet() {
    }

    public PacketBullet(Bullet bullet) {
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
        buf.writeBoolean(bullet.shooter != null);
        if (bullet.shooter != null)
            buf.writeInt(bullet.shooter.getEntityId());
    }

    public static class Handler implements IMessageHandler<PacketBullet, IMessage> {

        @SideOnly(Side.CLIENT)
        public IMessage onMessage(PacketBullet message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                if (message.entityID != Minecraft.getMinecraft().player.getEntityId()) {
                    Bullet.BULLETS.add(new Bullet(Minecraft.getMinecraft().world, message.posX, message.posY, message.posZ, 0,
                            message.lookVec, message.speed, message.damage, Minecraft.getMinecraft().player));
                }
            });
            return null;
        }
    }

}
