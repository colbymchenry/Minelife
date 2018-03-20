package com.minelife.util;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.server.FMLServerHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class PlayerHelper {

    @SideOnly(Side.SERVER)
    public static EntityPlayerMP getPlayer(UUID playerUUID) {
        for (WorldServer worldServer : FMLServerHandler.instance().getServer().worlds) {
            if (worldServer.getPlayerEntityByUUID(playerUUID) != null)
                return (EntityPlayerMP) worldServer.getPlayerEntityByUUID(playerUUID);
        }

        return null;
    }

    @SideOnly(Side.SERVER)
    public static EntityPlayerMP getPlayer(String player) {
        for (WorldServer worldServer : FMLServerHandler.instance().getServer().worlds) {
            for (int i = 0; i < worldServer.playerEntities.size(); ++i) {
                EntityPlayerMP entityPlayer = (EntityPlayerMP) worldServer.playerEntities.get(i);

                if (player.equalsIgnoreCase(entityPlayer.getName())) {
                    return entityPlayer;
                }
            }
        }

        return null;
    }

    public static TargetResult getTarget(EntityPlayer player, int range) {
        TargetResult result = new TargetResult();

        List<Block> blackListedBlocks = new ArrayList<>(Arrays.asList(Blocks.TALLGRASS, Blocks.WATER,
                Blocks.FLOWING_WATER, Blocks.DOUBLE_PLANT, Blocks.RED_FLOWER, Blocks.YELLOW_FLOWER));

        AxisAlignedBB surrounding_check = new AxisAlignedBB(player.posX - range, player.posY - range, player.posZ - range, player.posX + range, player.posY + range, player.posZ + range);
        List<EntityLivingBase> surrounding_entities = player.getEntityWorld().getEntitiesWithinAABB(EntityLivingBase.class, surrounding_check);
        Vec3d lookVec = player.getLookVec();
        Vec3d currentPosVec = new Vec3d(player.posX, player.posY + player.eyeHeight, player.posZ);

        IBlockState block;
        BlockPos pos;

        for (int i = 0; i < range; i++) {
            currentPosVec = currentPosVec.add(lookVec);

            pos = new BlockPos(MathHelper.floor(currentPosVec.x), MathHelper.floor(currentPosVec.y), MathHelper.floor(currentPosVec.z));
            block = player.getEntityWorld().getBlockState(pos);

            if (block != null && block.getBlock() != Blocks.AIR) {
                AxisAlignedBB axisalignedbb = block.getBoundingBox(player.getEntityWorld(), pos);

                if (axisalignedbb != null && axisalignedbb.contains(currentPosVec)) {
                    if (blackListedBlocks.contains(block.getBlock())) return result;
                    else {
                        result.blockPos = pos;
                        result.block = block;
                        return result;
                    }
                }
            }

            for (EntityLivingBase e : surrounding_entities) {
                if (e != player && e.getEntityBoundingBox().expand(0.3F, 0.3F, 0.3F).contains(currentPosVec)) {
                    result.entity = e;
                    return result;
                }
            }
        }

        return result;
    }

    public static class TargetResult {
        protected IBlockState block;
        protected BlockPos blockPos;
        protected EntityLivingBase entity;

        public EntityLivingBase getEntity() {
            return entity;
        }

        public BlockPos getBlockPos() {
            return blockPos;
        }

        public IBlockState getBlock() {
            return block;
        }
    }

    @SideOnly(Side.SERVER)
    public static boolean isOp(EntityPlayerMP player) {
        return Arrays.asList(FMLServerHandler.instance().getServer().getPlayerList().getOppedPlayerNames()).contains(player.getName());
    }

}
