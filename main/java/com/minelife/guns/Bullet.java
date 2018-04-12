package com.minelife.guns;

import com.google.common.collect.Lists;
import com.minelife.util.client.render.LineRenderer;
import com.minelife.util.client.render.Vector;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Bullet {

    public static volatile List<Bullet> BULLETS = Lists.newArrayList();

    private static final List<Block> blackListedBlocks = new ArrayList<>(Arrays.asList(Blocks.TALLGRASS, Blocks.WATER,
            Blocks.FLOWING_WATER, Blocks.DOUBLE_PLANT, Blocks.RED_FLOWER, Blocks.YELLOW_FLOWER, ModGuns.blockTurretBottom, ModGuns.blockTurretTop));

    public World world;
    public double posX, posY, posZ, prevPosX, prevPosY, prevPosZ, startX, startY, startZ, bulletSpeed, bulletDamage;
    public Vec3d lookVec;
    public int range = 112;
    public List<EntityLivingBase> nearbyTargets;
    public EntityLivingBase shooter;

    public Bullet(World world, double posX, double posY, double posZ, long pingDelay, Vec3d lookVec, double bulletSpeed, double bulletDamage, EntityLivingBase shooter) {
        this.world = world;
        this.prevPosX = posX;
        this.prevPosY = posY;
        this.prevPosZ = posZ;
        this.startX = posX;
        this.startY = posY;
        this.startZ = posZ;
        this.posX = posX + (lookVec.x * (pingDelay / 60));
        this.posY = posY + (lookVec.y * (pingDelay / 60));
        this.posZ = posZ + (lookVec.z * (pingDelay / 60));
        this.lookVec = lookVec;
        this.bulletDamage = bulletDamage;
        this.bulletSpeed = bulletSpeed;
        this.shooter = shooter;

        nearbyTargets = world.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(
                prevPosX - range, prevPosY - range, prevPosZ - range,
                prevPosX + range, prevPosY + range, prevPosZ + range));
    }

    public HitResult tick(float partialTicks, boolean simulate) {
        HitResult hitResult = collisionTest(simulate);
        if(hitResult != null) return hitResult;

        if(posX == startX && posY == startY && posZ == startZ) {
            prevPosX = posX - lookVec.x;
            prevPosY = posY - lookVec.y;
            prevPosZ = posZ - lookVec.z;
        } else {
            prevPosX += lookVec.x * (bulletSpeed * (partialTicks + 0.5));
            prevPosY += lookVec.y * (bulletSpeed * (partialTicks + 0.5));
            prevPosZ += lookVec.z * (bulletSpeed * (partialTicks + 0.5));
        }

        posX += lookVec.x * bulletSpeed;
        posY += lookVec.y * bulletSpeed;
        posZ += lookVec.z * bulletSpeed;

        if(world.isRemote && !simulate) {
            world.spawnParticle(EnumParticleTypes.FIREWORKS_SPARK, prevPosX, prevPosY, prevPosZ, lookVec.x, lookVec.y, lookVec.z);
            Vector topLeft = new Vector(prevPosX, prevPosY, prevPosZ);
            Vector bottomLeft = new Vector(prevPosX, prevPosY - 0.01, prevPosZ);
            Vector topRight = new Vector(posX, posY, posZ);
            LineRenderer.drawRect(Minecraft.getMinecraft(), topLeft, bottomLeft, null, topRight, partialTicks, Color.orange, true, true);
        }

        double d3 = posX - startX;
        double d4 = posY - startY;
        double d5 = posZ - startZ;
        double distance = Math.sqrt(d3 * d3 + d4 * d4 + d5 * d5);

        return new HitResult(distance > range);
    }

    private HitResult collisionTest(boolean simulate) {
        Vec3d lookNormalized = new Vec3d(lookVec.x, lookVec.y, lookVec.z).normalize();

        for (double distance = 0; distance < bulletSpeed; distance += bulletSpeed / 100) {
            Vec3d newVec = new Vec3d(lookNormalized.x * distance + posX,
                    lookNormalized.y * distance + posY, lookNormalized.z * distance + posZ);

            Block block = world.getBlockState(new BlockPos(Math.floor(newVec.x), Math.floor(newVec.y), Math.floor(newVec.z))).getBlock();
            boolean hitBlock = block != null && block != Blocks.AIR && !blackListedBlocks.contains(block);

            if(hitBlock) return new HitResult(world.getBlockState(new BlockPos(Math.floor(newVec.x), Math.floor(newVec.y), Math.floor(newVec.z))), null);

            for (EntityLivingBase e : nearbyTargets) {
                if (e != shooter && e.getEntityBoundingBox().contains(newVec)) {
                    if (world.isRemote) return new HitResult(null, e);

                    if (e instanceof EntityPlayerMP && ((EntityPlayerMP) e).isCreative()) return new HitResult(null, null);

                    if(!simulate) {
                        e.attackEntityFrom(DamageSource.GENERIC, (float) bulletDamage);
                        e.hurtResistantTime = 0;
                    }

                    return new HitResult(null, e);
                }
            }
        }

        return null;
    }

    public class HitResult {
        private IBlockState blockState;
        private EntityLivingBase entity;
        private boolean tooFar = false;

        public HitResult(IBlockState blockState, EntityLivingBase entity) {
            this.blockState = blockState;
            this.entity = entity;
        }

        public HitResult(boolean tooFar) {
            this.tooFar = tooFar;
        }

        public boolean isTooFar() {
            return tooFar;
        }

        public IBlockState getBlockState() {
            return blockState;
        }

        public EntityLivingBase getEntity() {
            return entity;
        }
    }

}
