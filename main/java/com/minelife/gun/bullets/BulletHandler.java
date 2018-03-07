package com.minelife.gun.bullets;

import com.google.common.collect.Lists;
import com.minelife.MLBlocks;
import com.minelife.gun.item.ammos.ItemAmmo;
import com.minelife.gun.item.guns.ItemGun;
import com.minelife.gun.server.BulletHitEvent;
import com.minelife.gun.turrets.TileEntityTurret;
import com.minelife.util.PlayerHelper;
import com.minelife.util.Vector;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.MinecraftForge;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.List;

public class BulletHandler {

    private int tickCount = 0;

    private static final List<Block> blackListedBlocks = new ArrayList<>(Arrays.asList(Blocks.tallgrass, Blocks.water,
            Blocks.flowing_water, Blocks.double_plant, Blocks.red_flower, Blocks.yellow_flower));

    public static volatile List<Bullet> bulletList = Lists.newArrayList();

    public static Bullet addBullet(EntityPlayer player, ItemAmmo.AmmoType ammoType) {
        Vec3 lookVec = player.getLookVec();
        Vec3 origin = Vec3.createVectorHelper(player.posX, player.posY + player.eyeHeight, player.posZ);
        Vec3 target = origin.addVector(lookVec.xCoord, lookVec.yCoord, lookVec.zCoord);
        Bullet bullet = new Bullet(player.worldObj, player, origin, target, lookVec, ammoType);
        bulletList.add(bullet);
        return bullet;
    }

    public static Bullet addBullet(TileEntityTurret turret, ItemAmmo.AmmoType ammoType) {
        Vector v = turret.getLookVec();
        Vec3 lookVec = Vec3.createVectorHelper(v.getX(), v.getY(), v.getZ());
        Vec3 origin = Vec3.createVectorHelper(turret.xCoord + 0.5, turret.yCoord + 1.5, turret.zCoord + 0.5);
        Vec3 target = origin.addVector(lookVec.xCoord, lookVec.yCoord, lookVec.zCoord);
        Bullet bullet = new Bullet(turret.getWorldObj(), null, origin, target, lookVec, ammoType);
        bulletList.add(bullet);
        return bullet;
    }

    @SubscribeEvent
    public void onTick(TickEvent event) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        tickCount++;

        if (tickCount < 2) return;

        ListIterator<Bullet> iterator = bulletList.listIterator();

        Bullet bullet;
        while (iterator.hasNext()) {
            bullet = iterator.next();

            boolean calledToQuit = false;


            int x = MathHelper.floor_double(bullet.target.xCoord);
            int y = MathHelper.floor_double(bullet.target.yCoord);
            int z = MathHelper.floor_double(bullet.target.zCoord);

            Block block = bullet.world.getBlock(x, y, z);

            /**
             * Check if we hit a block
             */
            if (block != null && block != Blocks.air && block != MLBlocks.turret && block != MLBlocks.turret.topTurret) {
                if (!bullet.world.isRemote) block.setBlockBoundsBasedOnState(bullet.world, x, y, z);
                AxisAlignedBB axisalignedbb = block.getCollisionBoundingBoxFromPool(bullet.world, x, y, z);

                if (axisalignedbb != null && axisalignedbb.isVecInside(bullet.target)) {
                    if (!blackListedBlocks.contains(block)) {


                        bullet.world.spawnParticle("blockcrack_" + Block.getIdFromBlock(block) + "_14", bullet.origin.xCoord, bullet.origin.yCoord, bullet.origin.zCoord, 0, 0, 0);
                        bullet.world.spawnParticle("blockcrack_" + Block.getIdFromBlock(block) + "_14", bullet.origin.xCoord, bullet.origin.yCoord, bullet.origin.zCoord, 0, 0, 0);
                        bullet.world.spawnParticle("blockcrack_" + Block.getIdFromBlock(block) + "_14", bullet.origin.xCoord, bullet.origin.yCoord, bullet.origin.zCoord, 0, 0, 0);
                        bullet.world.spawnParticle("blockcrack_" + Block.getIdFromBlock(block) + "_14", bullet.origin.xCoord, bullet.origin.yCoord, bullet.origin.zCoord, 0, 0, 0);

                        if (!bullet.world.isRemote) {
                            BulletHitEvent shotEvent = new BulletHitEvent(bullet.shooter, bullet.target.xCoord, bullet.target.yCoord, bullet.target.zCoord, bullet.shooter != null ? bullet.shooter.getHeldItem() : null);
                            MinecraftForge.EVENT_BUS.post(shotEvent);

                            if (shotEvent.isCanceled()) return;

                            if (bullet.ammoType == ItemAmmo.AmmoType.EXPLOSIVE) {
                                bullet.world.createExplosion(bullet.shooter, bullet.target.xCoord, bullet.target.yCoord, bullet.target.zCoord, 4.0F, false);
                            } else if (bullet.ammoType == ItemAmmo.AmmoType.INCENDIARY) {
                                if (bullet.world.getBlock((int) bullet.target.xCoord, (int) bullet.target.yCoord + 1, (int) bullet.target.zCoord) == Blocks.air) {
                                    bullet.world.setBlock((int) bullet.target.xCoord, (int) bullet.target.yCoord + 1, (int) bullet.target.zCoord, Blocks.fire);
                                }
                            }
                        }
                        iterator.remove();
                        calledToQuit = true;
                        continue;
                    }
                }
            }

            /**
             * Check if we hit an entity
             */

            float offset = 0.3f;

            AxisAlignedBB surrounding_check = AxisAlignedBB.getBoundingBox(bullet.target.xCoord - offset, bullet.target.yCoord - offset, bullet.target.zCoord - offset, bullet.target.xCoord + offset, bullet.target.yCoord + offset, bullet.target.zCoord + offset);
            for (EntityLivingBase e : (List<EntityLivingBase>) bullet.world.getEntitiesWithinAABB(EntityLivingBase.class, surrounding_check)) {
                if (e != bullet.shooter && e.boundingBox.expand(0.3F, 0.3F, 0.3F).isVecInside(bullet.target)) {

                    if(!bullet.world.isRemote) {
                        BulletHitEvent shotEvent = new BulletHitEvent(bullet.shooter, e, bullet.shooter == null ? null : bullet.shooter.getHeldItem());
                        MinecraftForge.EVENT_BUS.post(shotEvent);

                        if (shotEvent.isCanceled()) return;

                        if(e instanceof EntityPlayerMP && ((EntityPlayerMP) e).theItemInWorldManager.isCreative()) return;

                        int damage = bullet.shooter == null ? 10 : ((ItemGun) bullet.shooter.getHeldItem().getItem()).getDamage();
//                    e.attackEntityFrom(DamageSource.causeMobDamage(bullet.shooter), damage);
                        Method damageEntity = ReflectionHelper.findMethod(EntityLivingBase.class, e, new String[]{"func_70665_d", "damageEntity"}, DamageSource.class, float.class);
                        damageEntity.invoke(e, DamageSource.causeMobDamage(bullet.shooter), damage);

                        if (bullet.ammoType == ItemAmmo.AmmoType.EXPLOSIVE) {
                            bullet.world.createExplosion(bullet.shooter, bullet.target.xCoord, bullet.target.yCoord, bullet.target.zCoord, 4.0F, false);
                        } else if (bullet.ammoType == ItemAmmo.AmmoType.INCENDIARY) {
                            e.setFire(6);
                        }
                    }

                    if (!calledToQuit) {
                        iterator.remove();
                        calledToQuit = true;
                    }
                    continue;
                }
            }

            bullet.origin = bullet.origin.addVector(bullet.lookVec.xCoord, bullet.lookVec.yCoord, bullet.lookVec.zCoord);
            bullet.target = bullet.target.addVector(bullet.lookVec.xCoord, bullet.lookVec.yCoord, bullet.lookVec.zCoord);


            /**
             * Check if the bullet is far enough away to remove
             */
            if (bullet.target.distanceTo(Vec3.createVectorHelper(bullet.origin.xCoord, bullet.origin.yCoord, bullet.origin.zCoord)) > 500) {
                if (!calledToQuit) {
                    iterator.remove();
                    calledToQuit = true;
                }
            }
        }

        tickCount = 0;
    }
}
