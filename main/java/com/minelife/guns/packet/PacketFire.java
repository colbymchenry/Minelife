package com.minelife.guns.packet;

import com.minelife.Minelife;
import com.minelife.guns.Bullet;
import com.minelife.guns.ModGuns;
import com.minelife.guns.item.EnumGunType;
import com.minelife.guns.item.ItemGun;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.server.FMLServerHandler;

public class PacketFire implements IMessage {

    private long timeStamp;
    private Vec3d lookVector;

    public PacketFire() {
    }

    public PacketFire(Vec3d lookVector) {
        this.lookVector = lookVector;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        timeStamp = buf.readLong();
        lookVector = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(System.currentTimeMillis());
        buf.writeDouble(lookVector.x);
        buf.writeDouble(lookVector.y);
        buf.writeDouble(lookVector.z);
    }

    public static class Handler implements IMessageHandler<PacketFire, IMessage> {

        @SideOnly(Side.SERVER)
        public IMessage onMessage(PacketFire message, MessageContext ctx) {
            FMLServerHandler.instance().getServer().addScheduledTask(() -> {
                EntityPlayerMP player = ctx.getServerHandler().player;

                if (player.getHeldItemMainhand().getItem() != ModGuns.itemGun) return;

                EnumGunType gunType = EnumGunType.values()[player.getHeldItemMainhand().getMetadata()];

                long pingDelay = System.currentTimeMillis() - message.timeStamp;

                pingDelay = pingDelay > 200 ? 60 : pingDelay;

                if(ItemGun.getClipCount(player.getHeldItemMainhand()) <= 0) return;

                Bullet bullet = new Bullet(player.getEntityWorld(), player.posX, player.posY + player.getEyeHeight(), player.posZ, pingDelay,
                        message.lookVector, gunType.bulletSpeed, gunType.damage, player);

                Bullet.BULLETS.add(bullet);

                ItemGun.decreaseAmmo(player.getHeldItemMainhand());

                Minelife.getNetwork().sendToAllAround(new PacketBullet(bullet),
                        new NetworkRegistry.TargetPoint(player.world.provider.getDimension(), player.posX, player.posY, player.posZ, 112));
            });
            return null;
        }

    }

}
