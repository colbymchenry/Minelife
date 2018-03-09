package com.minelife.gun.bullets;

import com.minelife.MLBlocks;
import com.minelife.gun.item.ammos.ItemAmmo;
import com.minelife.gun.item.guns.ItemGun;
import com.minelife.gun.server.BulletHitEvent;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.relauncher.ReflectionHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Bullet {

    private static final List<Block> blackListedBlocks = new ArrayList<>(Arrays.asList(Blocks.tallgrass, Blocks.water,
            Blocks.flowing_water, Blocks.double_plant, Blocks.red_flower, Blocks.yellow_flower, Blocks.stone));

    public static int range = 0;

    // start is different than origin, start never changes
    public double startX, startY, startZ;
    public double originX, originY, originZ;
    public double targetX, targetY, targetZ;
    public double lookX, lookY, lookZ;
    public double bulletSpeed;
    public World world;
    public EntityLivingBase shooter;
    public ItemAmmo.AmmoType ammoType;
    public List<EntityLivingBase> nearbyTargets;

    @SuppressWarnings("ALL")
    public Bullet(World world, EntityLivingBase shooter, double originX, double originY, double originZ, double targetX, double targetY, double targetZ, double lookX, double lookY, double lookZ, ItemAmmo.AmmoType ammoType, double bulletSpeed) {
        this.world = world;
        this.shooter = shooter;
        this.originX = originX;
        this.originY = originY;
        this.originZ = originZ;
        this.startX = originX;
        this.startY = originY;
        this.startZ = originZ;
        this.targetX = targetX;
        this.targetY = targetY;
        this.targetZ = targetZ;
        this.lookX = lookX;
        this.lookY = lookY;
        this.lookZ = lookZ;
        this.ammoType = ammoType;
        this.bulletSpeed = bulletSpeed;

        if (!world.isRemote) {
            range = MinecraftServer.getServer().getConfigurationManager().getEntityViewDistance();

            nearbyTargets = world.getEntitiesWithinAABB(EntityLivingBase.class, AxisAlignedBB.getBoundingBox(startX - range, startY - range, startZ - range,
                    startX + range, startY + range, startZ + range));
        }
    }

    private Bullet() {
    }

    public void toBytes(ByteBuf buf) {
        buf.writeDouble(originX);
        buf.writeDouble(originY);
        buf.writeDouble(originZ);
        buf.writeDouble(targetX);
        buf.writeDouble(targetY);
        buf.writeDouble(targetZ);
        buf.writeDouble(lookX);
        buf.writeDouble(lookY);
        buf.writeDouble(lookZ);
        buf.writeDouble(startX);
        buf.writeDouble(startY);
        buf.writeDouble(startZ);
        buf.writeInt(range);
        buf.writeBoolean(shooter != null);
        if (shooter != null) buf.writeInt(shooter.getEntityId());
        buf.writeInt(ammoType.ordinal());
        buf.writeDouble(bulletSpeed);
    }

    @SuppressWarnings("ALL")
    @SideOnly(Side.CLIENT)
    public static Bullet fromBytes(ByteBuf buf) {
        Bullet bullet = new Bullet();
        bullet.originX = buf.readDouble();
        bullet.originY = buf.readDouble();
        bullet.originZ = buf.readDouble();
        bullet.targetX = buf.readDouble();
        bullet.targetY = buf.readDouble();
        bullet.targetZ = buf.readDouble();
        bullet.lookX = buf.readDouble();
        bullet.lookY = buf.readDouble();
        bullet.lookZ = buf.readDouble();
        bullet.startX = buf.readDouble();
        bullet.startY = buf.readDouble();
        bullet.startZ = buf.readDouble();
        Bullet.range = buf.readInt();
        if (buf.readBoolean())
            bullet.shooter = (EntityLivingBase) Minecraft.getMinecraft().theWorld.getEntityByID(buf.readInt());
        bullet.ammoType = ItemAmmo.AmmoType.values()[buf.readInt()];
        bullet.world = Minecraft.getMinecraft().theWorld;

        bullet.nearbyTargets = bullet.world.getEntitiesWithinAABB(EntityLivingBase.class, AxisAlignedBB.getBoundingBox(bullet.startX - range, bullet.startY - range, bullet.startZ - range,
                bullet.startX + range, bullet.startY + range, bullet.startZ + range));

        bullet.bulletSpeed = buf.readDouble();
        return bullet;
    }

    public boolean tick() throws InvocationTargetException, IllegalAccessException {
        int x = MathHelper.floor_double(targetX);
        int y = MathHelper.floor_double(targetY);
        int z = MathHelper.floor_double(targetZ);

        Block block = world.getBlock(x, y, z);

        boolean hitBlock = block != null && block != Blocks.air && block != MLBlocks.turret && block != MLBlocks.turret.topTurret && !blackListedBlocks.contains(block);

        if (hitBlock) {
//            if (!bullet.world.isRemote) block.setBlockBoundsBasedOnState(bullet.world, x, y, z);
//            AxisAlignedBB axisalignedbb = block.getCollisionBoundingBoxFromPool(bullet.world, x, y, z);
            AxisAlignedBB collisionBox = block.getCollisionBoundingBoxFromPool(world, x, y, z);

            // check if it hits inside the correct collision bounds for the specific block
            if (collisionBox != null && x >= collisionBox.minX && x <= collisionBox.maxX && y >= collisionBox.minY && y <= collisionBox.maxY
                    && z >= collisionBox.minZ && z <= collisionBox.maxZ) {

                // return true here for client because they need not go further
                if (world.isRemote) return true;
                else {
                    // spawn block break particles
                    for (int i = 0; i < 4; i++)
                        world.spawnParticle("blockcrack_" + Block.getIdFromBlock(block) + "_14", originX, originY, originZ, 0, 0, 0);
                }

                BulletHitEvent hitEvent = new BulletHitEvent(shooter, targetX, targetY, targetZ, shooter != null ? shooter.getHeldItem() : null);
                MinecraftForge.EVENT_BUS.post(hitEvent);

                if (hitEvent.isCanceled()) return true;

                if (ammoType == ItemAmmo.AmmoType.EXPLOSIVE)
                    world.createExplosion(shooter, targetX, targetY, targetZ, 4.0F, false);
                else if (ammoType == ItemAmmo.AmmoType.INCENDIARY && world.getBlock(x, y + 1, z) == Blocks.air)
                    world.setBlock(x, y + 1, z, Blocks.fire);

                return true;
            }
        }

        // Find an entity to hit
        for (EntityLivingBase e : nearbyTargets) {
            // if there was no collision
            if (e != shooter && sweptAABB(e)) {
                // return true if client because they need not go further
                if (world.isRemote) return true;

                BulletHitEvent hitEvent = new BulletHitEvent(shooter, e, shooter == null ? null : shooter.getHeldItem());
                MinecraftForge.EVENT_BUS.post(hitEvent);

                if (hitEvent.isCanceled()) return true;

                if (e instanceof EntityPlayerMP && ((EntityPlayerMP) e).theItemInWorldManager.isCreative()) return true;

                int damage = shooter == null ? 10 : ((ItemGun) shooter.getHeldItem().getItem()).getDamage();
                Method damageEntity = ReflectionHelper.findMethod(EntityLivingBase.class, e, new String[]{"func_70665_d", "damageEntity"}, DamageSource.class, float.class);
                damageEntity.invoke(e, DamageSource.causeMobDamage(shooter), damage);

                if (ammoType == ItemAmmo.AmmoType.EXPLOSIVE) {
                    world.createExplosion(shooter, targetX, targetY, targetZ, 4.0F, false);
                    return true;
                } else if (ammoType == ItemAmmo.AmmoType.INCENDIARY) {
                    e.setFire(6);
                    return true;
                }
            }
        }

        originX += lookX * bulletSpeed;
        originY += lookY * bulletSpeed;
        originZ += lookZ * bulletSpeed;
        targetX += lookX * bulletSpeed;
        targetY += lookY * bulletSpeed;
        targetZ += lookZ * bulletSpeed;

        double d3 = startX - originX;
        double d4 = startY - originY;
        double d5 = startZ - originZ;
        double distance = Math.sqrt(d3 * d3 + d4 * d4 + d5 * d5);

        return distance > range;
    }

    private boolean sweptAABB(EntityLivingBase e) {
        double xInvEntry, yInvEntry, zInvEntry;
        double xInvExit, yInvExit, zInvExit;

        // find the distance between the objects on the near and far sides for both x and y
        if (lookX > 0.0f) {
            xInvEntry = e.boundingBox.minX - targetX;
            xInvExit = e.boundingBox.maxX - originX;
        } else {
            xInvEntry = e.boundingBox.maxX - originX;
            xInvExit = e.boundingBox.minX - targetX;
        }

        if (lookY > 0.0f) {
            yInvEntry = e.boundingBox.minY - targetY;
            yInvExit = e.boundingBox.maxY - originY;
        } else {
            yInvEntry = e.boundingBox.maxY - originY;
            yInvExit = e.boundingBox.minY - targetY;
        }

        if (lookZ > 0.0f) {
            zInvEntry = e.boundingBox.minZ - targetZ;
            zInvExit = e.boundingBox.maxZ - originZ;
        } else {
            zInvEntry = e.boundingBox.maxZ - originZ;
            zInvExit = e.boundingBox.minZ - targetZ;
        }

        double xEntry, yEntry, zEntry;
        double xExit, yExit, zExit;

        if (lookX == 0.0f) {
            xEntry = Double.NEGATIVE_INFINITY;
            xExit = Double.POSITIVE_INFINITY;
        } else {
            xEntry = xInvEntry / (lookX * bulletSpeed);
            xExit = xInvExit / (lookX * bulletSpeed);
        }

        if (lookY == 0.0f) {
            yEntry = Double.NEGATIVE_INFINITY;
            yExit = Double.POSITIVE_INFINITY;
        } else {
            yEntry = yInvEntry / (lookY * bulletSpeed);
            yExit = yInvExit / (lookY * bulletSpeed);
        }

        if (lookZ == 0.0f) {
            zEntry = Double.NEGATIVE_INFINITY;
            zExit = Double.POSITIVE_INFINITY;
        } else {
            zEntry = zInvEntry / (lookZ * bulletSpeed);
            zExit = zInvExit / (lookZ * bulletSpeed);
        }

        // find the earliest/latest times of collision
        double entryTime = Math.max(xEntry, yEntry);
        double exitTime = Math.min(xExit, yExit);
        entryTime = Math.max(entryTime, zEntry);
        exitTime = Math.min(exitTime, zExit);

        // if there was no collision
        return entryTime <= exitTime && (xEntry >= 0.0f || yEntry >= 0.0f) && xEntry <= 1.0f && yEntry <= 1.0f;
    }

}
