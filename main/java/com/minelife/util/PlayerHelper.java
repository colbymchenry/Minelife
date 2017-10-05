package com.minelife.util;

import com.google.common.collect.Lists;
import com.minelife.Minelife;
import com.minelife.gun.client.ClientProxy;
import com.minelife.util.server.PacketUpdatePlayerInventory;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.WorldServer;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class PlayerHelper {

    @SideOnly(Side.SERVER)
    public static EntityPlayerMP getPlayer(UUID playerUUID) {
        for (WorldServer worldServer : MinecraftServer.getServer().worldServers) {
            if (worldServer.func_152378_a(playerUUID) != null)
                return (EntityPlayerMP) worldServer.func_152378_a(playerUUID);
        }

        return null;
    }

    @SideOnly(Side.SERVER)
    public static void updatePlayerInventory(EntityPlayerMP player) {
        Minelife.NETWORK.sendTo(new PacketUpdatePlayerInventory(player), player);
    }

    public static TargetResult getTrget(EntityPlayer player, int range, String effect) {
        TargetResult result = new TargetResult();

        List<Block> blackListedBlocks = new ArrayList<>(Arrays.asList(Blocks.tallgrass, Blocks.water,
                Blocks.flowing_water, Blocks.double_plant, Blocks.red_flower, Blocks.yellow_flower));

        AxisAlignedBB surrounding_check = AxisAlignedBB.getBoundingBox(player.posX - range, player.posY - range, player.posZ - range, player.posX + range, player.posY + range, player.posZ + range);
        List<EntityLivingBase> surrounding_entities = player.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, surrounding_check);
        Vec3 lookVec = player.getLookVec();
        Vec3 currentPosVec = Vec3.createVectorHelper(player.posX, player.posY + player.eyeHeight, player.posZ);

        Vec3 origin = null, target = null;
        Block block;

        for (int i = 0; i < range; i++) {

            if(i == 1 && player.worldObj.isRemote) {
                origin = currentPosVec;
            }

            currentPosVec = currentPosVec.addVector(lookVec.xCoord, lookVec.yCoord, lookVec.zCoord);

            if(i == 1 && player.worldObj.isRemote) {
               target = currentPosVec;
//                ClientProxy.renderBulletLine.shot(origin, target, lookVec);
            }

            if (i < 10 && effect != null && !effect.isEmpty())
                player.worldObj.spawnParticle(effect, currentPosVec.xCoord, currentPosVec.yCoord - 0.2f, currentPosVec.zCoord, 0.0F, 0.0F, 0.0F);

            // may have to move the yCoord down by 0.2f for everything to make it work right, we'll see

            int x = MathHelper.floor_double(currentPosVec.xCoord);
            int y = MathHelper.floor_double(currentPosVec.yCoord);
            int z = MathHelper.floor_double(currentPosVec.zCoord);
            block = player.worldObj.getBlock(x, y, z);

            if (block != null && block != Blocks.air) {
                block.setBlockBoundsBasedOnState(player.worldObj, x, y, z);
                AxisAlignedBB axisalignedbb = block.getCollisionBoundingBoxFromPool(player.worldObj, x, y, z);

                if (axisalignedbb != null && axisalignedbb.isVecInside(Vec3.createVectorHelper(currentPosVec.xCoord, currentPosVec.yCoord, currentPosVec.zCoord))) {
                    if (blackListedBlocks.contains(block)) return result;
                    else {
                        result.blockVector = new BlockVector(currentPosVec.xCoord, currentPosVec.yCoord, currentPosVec.zCoord);
                        result.block = block;
                        return result;
                    }
                }
            }

            for (EntityLivingBase e : surrounding_entities) {
                if (e != player && e.boundingBox.expand(0.3F, 0.3F, 0.3F).isVecInside(currentPosVec)) {
                    result.entity = e;
                    return result;
                }
            }
        }

        return result;
    }

//    public static TargetResult getTrget(EntityPlayer player, int range) {
//        return getTarget(player, range, null);
//    }

    @SideOnly(Side.CLIENT)
    public static final void zoom(double amount) {

        if (amount < 1) amount = 1;

        EntityRenderer entRenderer = Minecraft.getMinecraft().entityRenderer;
        try {
            Class<?> c = entRenderer.getClass();
            Field f = c.getDeclaredField("cameraZoom");
            f.setAccessible(true);
            // f.setAccessible(true); // solution
            f.setDouble(entRenderer, amount); // IllegalAccessException
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    public static class TargetResult {
        protected Block block;
        protected BlockVector blockVector;
        protected EntityLivingBase entity;

        public EntityLivingBase getEntity() {
            return entity;
        }

        public BlockVector getBlockVector() {
            return blockVector;
        }

        public Block getBlock() {
            return block;
        }
    }

    @SideOnly(Side.SERVER)
    public static boolean isOp(EntityPlayerMP player) {
        return MinecraftServer.getServer().getConfigurationManager().func_152596_g(player.getGameProfile());
    }


}
