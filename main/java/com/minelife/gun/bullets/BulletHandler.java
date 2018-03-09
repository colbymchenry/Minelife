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
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
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

    private static int tickCount = 0;
    private static List<Bullet> toRemove = Lists.newArrayList();
    public static List<Bullet> bulletList = Lists.newArrayList();

    public static final BulletHandler instance = new BulletHandler();

    private BulletHandler() {

    }

    public static Bullet addBullet(EntityPlayer player, ItemAmmo.AmmoType ammoType, double bulletSpeed) {
        Bullet bullet = new Bullet(player.worldObj, player,
                player.posX, player.posY + player.eyeHeight, player.posZ,
                player.posX + player.getLookVec().xCoord, player.posY + player.eyeHeight + player.getLookVec().yCoord, player.posZ + player.getLookVec().zCoord,
                player.getLookVec().xCoord, player.getLookVec().yCoord, player.getLookVec().zCoord, ammoType, bulletSpeed);
        bulletList.add(bullet);
        return bullet;
    }

    public static Bullet addBullet(TileEntityTurret turret, ItemAmmo.AmmoType ammoType) {
        Vector v = turret.getLookVec();
        Bullet bullet = new Bullet(turret.getWorldObj(), null, turret.xCoord + 0.5, turret.yCoord + 1.5, turret.zCoord + 0.5,
                turret.xCoord + 0.5 + v.getX(), turret.yCoord + 1.5 + v.getY(), turret.zCoord + 0.5 + v.getZ(), v.getX(), v.getY(), v.getZ(), ammoType, 2.8);
        bulletList.add(bullet);
        return bullet;
    }

    // TODO: normal rounds causing total server hangup every 30-40 seconds
    @SideOnly(Side.SERVER)
    @SubscribeEvent
    public void onTick(TickEvent.ServerTickEvent event) throws IllegalAccessException, InvocationTargetException {

        toRemove.clear();

        ListIterator<Bullet> iterator = bulletList.listIterator();

        Bullet bullet;
        while (iterator.hasNext()) {
            bullet = iterator.next();
            if(bullet.tick()) toRemove.add(bullet);
        }

        bulletList.removeAll(toRemove);

    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) throws IllegalAccessException, InvocationTargetException {
        toRemove.clear();

        ListIterator<Bullet> iterator = bulletList.listIterator();

        Bullet bullet;
        while (iterator.hasNext()) {
            bullet = iterator.next();
            if(bullet.tick()) toRemove.add(bullet);
        }

        bulletList.removeAll(toRemove);
    }
}
