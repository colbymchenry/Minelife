package com.minelife.util;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.minelife.realestate.server.EstateListener;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class BreakHelper {

    private static Set<BreakProgress> BREAK_PROGRESS = Sets.newTreeSet();

    public static BreakProgress get(BlockPos pos, World world) {
        return BREAK_PROGRESS.stream().filter(breakProgress -> breakProgress.pos.getX() == pos.getX() &&
                breakProgress.pos.getY() == pos.getY() && breakProgress.pos.getZ() == pos.getZ() &&
                world.equals(breakProgress.world)).findFirst().orElse(null);
    }

    public static BreakProgress create(BlockPos pos, World world, int progress) {
        BreakProgress breakProgress;
        BREAK_PROGRESS.add(breakProgress = new BreakProgress(pos, world, progress));
        return breakProgress;
    }

    public static void remove(BreakProgress breakProgress) {
        breakProgress.clear();
        BREAK_PROGRESS.remove(breakProgress);
    }

    public static Map<BlockPos, Integer> getAffectedBlocksFromExplosion(World world, Vec3d pos, EntityLivingBase exploder, float power, int distance) {
        Map<BlockPos, Integer> map = Maps.newHashMap();
        Explosion explosion = new Explosion(world, exploder, pos.x, pos.y, pos.z, power, false, true);
        for (int j = 0; j < distance; ++j) {
            for (int k = 0; k < distance; ++k) {
                for (int l = 0; l < distance; ++l) {
                    if (j == 0 || j == distance - 1 || k == 0 || k == distance - 1 || l == 0 || l == distance - 1) {
                        double d0 = (double) ((float) j / 15.0F * 2.0F - 1.0F);
                        double d1 = (double) ((float) k / 15.0F * 2.0F - 1.0F);
                        double d2 = (double) ((float) l / 15.0F * 2.0F - 1.0F);
                        double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
                        d0 = d0 / d3;
                        d1 = d1 / d3;
                        d2 = d2 / d3;
                        float f = power * (0.7F + world.rand.nextFloat() * 0.6F);
                        double d4 = pos.x;
                        double d6 = pos.y;
                        double d8 = pos.z;

                        for (float f1 = 0.3F; f > 0.0F; f -= 0.22500001F) {
                            BlockPos blockpos = new BlockPos(d4, d6, d8);
                            IBlockState iblockstate = world.getBlockState(blockpos);
                            float f2 = exploder != null ? exploder.getExplosionResistance(explosion, world, blockpos, iblockstate) : iblockstate.getBlock().getExplosionResistance(world, blockpos, (Entity) null, explosion);

                            if (iblockstate.getMaterial() != Material.AIR) {
                                f -= (f2 + 0.3F) * 0.3F;
                                map.put(blockpos, (int) ((240.0F * power) / f2));
                            }

                            d4 += d0 * 0.30000001192092896D;
                            d6 += d1 * 0.30000001192092896D;
                            d8 += d2 * 0.30000001192092896D;
                        }
                    }
                }
            }
        }

        return map;
    }


    @SubscribeEvent
    public void breakEvent(BlockEvent.BreakEvent event) {
        BreakProgress breakProgress = get(event.getPos(), event.getWorld());
        if (breakProgress != null) remove(breakProgress);
    }

    @SubscribeEvent
    public void serverTick(TickEvent.ServerTickEvent event) {
        Iterator<BreakProgress> iterator = BREAK_PROGRESS.iterator();
        while (iterator.hasNext()) {
            BreakProgress breakProgress = iterator.next();

            if (System.currentTimeMillis() - breakProgress.intervalTime > 5000L) {
                int var1 = (int) ((float) breakProgress.progress / 240.0F * 10.0F);
                breakProgress.world.sendBlockBreakProgress(breakProgress.entID, breakProgress.pos, var1);
                breakProgress.intervalTime = System.currentTimeMillis();
            }

            if (System.currentTimeMillis() - breakProgress.startTime > 60000L) {
                breakProgress.world.sendBlockBreakProgress(breakProgress.entID, breakProgress.pos, -1);
                iterator.remove();
            }
        }
    }

    public static class BreakProgress implements Comparable<BreakProgress> {
        public BlockPos pos;
        private int progress, entID;
        private long startTime, intervalTime;
        public World world;

        public BreakProgress(BlockPos pos, World world, int progress) {
            this.pos = pos;
            this.world = world;
            this.progress = progress;
            this.entID = world.rand.nextInt() * -1;
            this.startTime = System.currentTimeMillis();
            this.intervalTime = System.currentTimeMillis();
        }

        public void sendProgress() {
            world.sendBlockBreakProgress(entID, pos, getCalculatedProgress());
        }

        public void clear() {
            world.sendBlockBreakProgress(entID, pos, -1);
        }

        public int getCalculatedProgress() {
            return (int) ((float) (progress < 240 ? progress : 240) / 240.0F * 10.0F);
        }

        public int getUncalculatedProgress() {
            return this.progress;
        }

        public void setProgress(int progress) {
            this.progress = progress;
            this.startTime = System.currentTimeMillis();
        }

        @Override
        public int compareTo(BreakProgress o) {
            return entID - o.entID;
        }
    }

}
