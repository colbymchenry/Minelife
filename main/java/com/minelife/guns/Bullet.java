package com.minelife.guns;

import com.google.common.collect.Lists;
import com.minelife.Minelife;
import com.minelife.util.PacketPlaySound;
import com.minelife.util.client.render.LineRenderer;
import com.minelife.util.client.render.Vector;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
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
    public double posX, posY, posZ, prevPosX, prevPosY, prevPosZ, startX, startY, startZ, bulletSpeed, bulletDamage, pingDelay;
    private boolean initialTick = true;
    public Vec3d lookVec;
    public int range = 112;
    public List<EntityLivingBase> nearbyTargets;
    public EntityLivingBase shooter;

    // TODO: Add kill message for player who killed another player
    public Bullet(World world, double posX, double posY, double posZ, Vec3d lookVec, double bulletSpeed, double bulletDamage, double pingDelay, EntityLivingBase shooter) {
        System.out.println("SPAWN BULLET");
        this.world = world;
        this.startX = posX;
        this.startY = posY;
        this.startZ = posZ;
        this.pingDelay = pingDelay;
        this.prevPosX = posX;
        this.prevPosY = posY;
        this.prevPosZ = posZ;
        // TODO: May need to increase from 60 to max of 100, still doesn't feel PERFECT. May still have to take into account how long it takes for packet to reach server.
        this.posX = posX + ((lookVec.x * bulletSpeed) * (pingDelay / 60));
        this.posY = posY + ((lookVec.y * bulletSpeed) * (pingDelay / 60));
        this.posZ = posZ + ((lookVec.z * bulletSpeed) * (pingDelay / 60));
//        this.posX = posX + (lookVec.x);
//        this.posY = posY + (lookVec.y);
//        this.posZ = posZ + (lookVec.z);

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
        if (hitResult != null) return hitResult;

        if (world.isRemote && !simulate) {
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

        if (initialTick) {
            prevPosX = posX - lookVec.x * bulletSpeed;
            prevPosY = posY - lookVec.y * bulletSpeed;
            prevPosZ = posZ - lookVec.z * bulletSpeed;
            initialTick = false;
        } else {
            prevPosX += lookVec.x * (bulletSpeed * (partialTicks + 0.5));
            prevPosY += lookVec.y * (bulletSpeed * (partialTicks + 0.5));
            prevPosZ += lookVec.z * (bulletSpeed * (partialTicks + 0.5));
        }

        posX += lookVec.x * bulletSpeed;
        posY += lookVec.y * bulletSpeed;
        posZ += lookVec.z * bulletSpeed;

        return new HitResult(distance > range);
    }

    private HitResult collisionTest(boolean simulate) {
        Vec3d lookNormalized = new Vec3d(lookVec.x, lookVec.y, lookVec.z).normalize();
        Vec3d prevVec = new Vec3d(prevPosX, prevPosY, prevPosZ);
        Vec3d currentVec = new Vec3d(posX, posY, posZ);
        double distanceBetweenVec = prevVec.distanceTo(currentVec);

        for (double distance = 0; distance < distanceBetweenVec; distance += 1 / distanceBetweenVec) {
            Vec3d newVec = new Vec3d(posX - lookNormalized.x * distance, posY - lookNormalized.y * distance, posZ - lookNormalized.z * distance);

            BlockPos blockpos = new BlockPos(MathHelper.floor(newVec.x), MathHelper.floor(newVec.y), MathHelper.floor(newVec.z));
            IBlockState iblockstate = this.world.getBlockState(blockpos);
            Block block = iblockstate.getBlock();

            if (iblockstate.getMaterial() != Material.AIR) {
                AxisAlignedBB axisalignedbb = iblockstate.getCollisionBoundingBox(this.world, blockpos);

                if (axisalignedbb != Block.NULL_AABB && axisalignedbb.offset(blockpos).contains(newVec) && !blackListedBlocks.contains(block)) {
                    HitResult result = forwardProgression();
                    if (result != null && result.getEntity() == null) {
                        return new HitResult(iblockstate, null);
                    } else if (result != null && result.getEntity() != null) {
                        if (!simulate)
                            damageEntity(result.getEntity());
                        return new HitResult(null, result.getEntity());
                    }
                }
            }

            for (EntityLivingBase e : nearbyTargets) {
                if (e != shooter && e.getEntityBoundingBox().contains(newVec)) {
                    if (world.isRemote) return new HitResult(null, e);

                    HitResult result = forwardProgression();


                    if (result != null && result.getBlockState() == null) {

                        if (e instanceof EntityPlayerMP && ((EntityPlayerMP) e).isCreative()) {
                            return new HitResult(null, null);
                        }

                        if (!simulate)
                            damageEntity(e);
                        return new HitResult(null, e);
                    }
                }
            }
        }


        return null;
    }

    private void damageEntity(EntityLivingBase e) {
        e.attackEntityFrom(shooter != null && shooter instanceof EntityPlayerMP ? DamageSource.causePlayerDamage((EntityPlayer) shooter) : DamageSource.GENERIC, (float) bulletDamage);
        if (shooter != null && shooter instanceof EntityPlayerMP && e instanceof EntityPlayer) {
            Minelife.getNetwork().sendTo(new PacketPlaySound("minecraft:entity.arrow.hit_player", 1, 1), (EntityPlayerMP) shooter);
        }
        e.hurtResistantTime = 0;
    }

    public HitResult forwardProgression() {
        Vec3d lookNormalized = new Vec3d(lookVec.x, lookVec.y, lookVec.z).normalize();
        Vec3d prevVec = new Vec3d(prevPosX, prevPosY, prevPosZ);
        Vec3d currentVec = new Vec3d(posX, posY, posZ);
        double distanceBetweenVec = prevVec.distanceTo(currentVec);

        for (double distance = 0; distance < distanceBetweenVec; distance += 4 / distanceBetweenVec) {
            Vec3d newVec = new Vec3d(prevPosX + lookNormalized.x * distance, prevPosY + lookNormalized.y * distance, prevPosZ + lookNormalized.z * distance);

            BlockPos blockpos = new BlockPos(MathHelper.floor(newVec.x), MathHelper.floor(newVec.y), MathHelper.floor(newVec.z));
            IBlockState iblockstate = this.world.getBlockState(blockpos);
            Block block = iblockstate.getBlock();

            if (iblockstate.getMaterial() != Material.AIR) {
                AxisAlignedBB axisalignedbb = iblockstate.getCollisionBoundingBox(this.world, blockpos);

                if (axisalignedbb != Block.NULL_AABB && axisalignedbb.offset(blockpos).contains(newVec) && !blackListedBlocks.contains(block)) {
                    return new HitResult(iblockstate, null);
                }
            }

            for (EntityLivingBase e : nearbyTargets) {
                if (e != shooter && e.getEntityBoundingBox().contains(newVec)) {
                    if (world.isRemote) return new HitResult(null, e);

                    if (e instanceof EntityPlayerMP && ((EntityPlayerMP) e).isCreative())
                        return new HitResult(null, null);

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
