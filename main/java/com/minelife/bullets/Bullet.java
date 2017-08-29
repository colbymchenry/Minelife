package com.minelife.bullets;

import com.minelife.gun.item.ammos.ItemAmmo;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class Bullet {

    public Vec3 origin, target, lookVec;
    public World world;
    public EntityLivingBase shooter;
    public ItemAmmo.AmmoType ammoType;

    public Bullet(World world, EntityLivingBase shooter, Vec3 origin, Vec3 target, Vec3 lookVec, ItemAmmo.AmmoType ammoType) {
        this.world = world;
        this.shooter = shooter;
        this.origin = origin;
        this.target = target;
        this.lookVec = lookVec;
        this.ammoType = ammoType;
    }

    private Bullet() {}

    public void toBytes(ByteBuf buf) {
        buf.writeDouble(origin.xCoord);
        buf.writeDouble(origin.yCoord);
        buf.writeDouble(origin.zCoord);
        buf.writeDouble(target.xCoord);
        buf.writeDouble(target.yCoord);
        buf.writeDouble(target.zCoord);
        buf.writeDouble(lookVec.xCoord);
        buf.writeDouble(lookVec.yCoord);
        buf.writeDouble(lookVec.zCoord);
        buf.writeInt(shooter.getEntityId());
        ByteBufUtils.writeUTF8String(buf, ammoType.name());
    }

    @SideOnly(Side.CLIENT)
    public static Bullet fromBytes(ByteBuf buf) {
        Bullet bullet = new Bullet();
        bullet.origin = Vec3.createVectorHelper(buf.readDouble(), buf.readDouble(), buf.readDouble());
        bullet.target = Vec3.createVectorHelper(buf.readDouble(), buf.readDouble(), buf.readDouble());
        bullet.lookVec = Vec3.createVectorHelper(buf.readDouble(), buf.readDouble(), buf.readDouble());
        bullet.shooter = (EntityLivingBase) Minecraft.getMinecraft().theWorld.getEntityByID(buf.readInt());
        bullet.ammoType = ItemAmmo.AmmoType.valueOf(ByteBufUtils.readUTF8String(buf));
        bullet.world = Minecraft.getMinecraft().theWorld;
        return bullet;
    }

}
