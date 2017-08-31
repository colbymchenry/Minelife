package com.minelife.gun.bullets;

import com.google.common.collect.Lists;
import com.minelife.gun.item.ammos.ItemAmmo;
import com.minelife.gun.item.guns.ItemGun;
import com.minelife.gun.server.EntityShotEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.MinecraftForge;

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

    @SubscribeEvent
    public void onTick(TickEvent event) {
        tickCount++;

        // TODO: Add bullet speed
        if (tickCount < 2) return;

        Iterator iterator = bulletList.iterator();

        Bullet bullet;
        while (iterator.hasNext()) {
            bullet = (Bullet) iterator.next();

            bullet.origin = bullet.origin.addVector(bullet.lookVec.xCoord, bullet.lookVec.yCoord, bullet.lookVec.zCoord);
            bullet.target = bullet.target.addVector(bullet.lookVec.xCoord, bullet.lookVec.yCoord, bullet.lookVec.zCoord);

            if (!bullet.world.isRemote) {

                int x = MathHelper.floor_double(bullet.target.xCoord);
                int y = MathHelper.floor_double(bullet.target.yCoord);
                int z = MathHelper.floor_double(bullet.target.zCoord);

                Block block = bullet.world.getBlock(x, y, z);

                if (block != null && block != Blocks.air) {
                    block.setBlockBoundsBasedOnState(bullet.world, x, y, z);
                    AxisAlignedBB axisalignedbb = block.getCollisionBoundingBoxFromPool(bullet.world, x, y, z);

                    if (axisalignedbb != null && axisalignedbb.isVecInside(Vec3.createVectorHelper(bullet.target.xCoord, bullet.target.yCoord, bullet.target.zCoord))) {
                        if (!blackListedBlocks.contains(block)) {
                            if (bullet.ammoType == ItemAmmo.AmmoType.EXPLOSIVE) {
                                bullet.world.createExplosion(bullet.shooter, bullet.target.xCoord, bullet.target.yCoord, bullet.target.zCoord, 4.0F, false);
                            } else if (bullet.ammoType == ItemAmmo.AmmoType.INCENDIARY) {
                                if (bullet.world.getBlock((int) bullet.target.xCoord, (int) bullet.target.yCoord + 1, (int) bullet.target.zCoord) == Blocks.air) {
                                    bullet.world.setBlock((int) bullet.target.xCoord, (int) bullet.target.yCoord + 1, (int) bullet.target.zCoord, Blocks.fire);
                                }
                            }
                            iterator.remove();
                            continue;
                        }
                    }
                }

                float offset = 0.3f;

                AxisAlignedBB surrounding_check = AxisAlignedBB.getBoundingBox(bullet.target.xCoord - offset, bullet.target.yCoord - offset, bullet.target.zCoord - offset, bullet.target.xCoord + offset, bullet.target.yCoord + offset, bullet.target.zCoord + offset);
                for (EntityLivingBase e : (List<EntityLivingBase>) bullet.world.getEntitiesWithinAABB(EntityLivingBase.class, surrounding_check)) {
                    if (e != bullet.shooter && e.boundingBox.expand(0.3F, 0.3F, 0.3F).isVecInside(bullet.target)) {
                        EntityShotEvent shotEvent = new EntityShotEvent(bullet.shooter, e, bullet.shooter.getHeldItem());
                        MinecraftForge.EVENT_BUS.post(shotEvent);

                        if (shotEvent.isCanceled()) return;
                        int damage = ((ItemGun) bullet.shooter.getHeldItem().getItem()).getDamage();
                        e.attackEntityFrom(DamageSource.causeMobDamage(bullet.shooter), damage);

                        if (bullet.ammoType == ItemAmmo.AmmoType.EXPLOSIVE) {
                            bullet.world.createExplosion(bullet.shooter, bullet.target.xCoord, bullet.target.yCoord, bullet.target.zCoord, 4.0F, false);
                        } else if (bullet.ammoType == ItemAmmo.AmmoType.INCENDIARY) {
                            e.setFire(6);
                        }

                        iterator.remove();
                        continue;
                    }
                }
            }
        }


        tickCount = 0;
    }
}
