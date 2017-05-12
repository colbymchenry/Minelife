package com.minelife.util;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
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

    public static EntityLivingBase getTargetEntity(EntityPlayer player, int range) {

        List<Block> blackListedBlocks = new ArrayList<>(Arrays.asList(new Block[]{Blocks.air, Blocks.grass,
                Blocks.tallgrass, Blocks.water, Blocks.flowing_water, Blocks.double_plant, Blocks.red_flower, Blocks.yellow_flower}));

        AxisAlignedBB surrounding_check = AxisAlignedBB.getBoundingBox(player.posX - range, player.posY - range, player.posZ - range, player.posX + range, player.posY + range, player.posZ + range);
        List<EntityLivingBase> surrounding_entities = player.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, surrounding_check);
        Vec3 lookVec = player.getLookVec();
        Vec3 currentPosVec = Vec3.createVectorHelper(player.posX, player.posY + player.eyeHeight, player.posZ);
        Block block;

        for (int i = 0; i < range; i++) {
            currentPosVec = currentPosVec.addVector(lookVec.xCoord, lookVec.yCoord, lookVec.zCoord);

            if (i < 10)
                player.worldObj.spawnParticle("smoke", currentPosVec.xCoord, currentPosVec.yCoord - 0.2f, currentPosVec.zCoord, 0.0F, 0.0F, 0.0F);

            block = player.worldObj.getBlock((int) currentPosVec.xCoord, (int) currentPosVec.yCoord, (int) currentPosVec.zCoord);

            if (block != null && block != Blocks.air) {
                block.setBlockBoundsBasedOnState(player.worldObj, (int) currentPosVec.xCoord, (int) currentPosVec.yCoord, (int) currentPosVec.zCoord);
                AxisAlignedBB axisalignedbb = block.getCollisionBoundingBoxFromPool(player.worldObj, (int) currentPosVec.xCoord, (int) currentPosVec.yCoord, (int) currentPosVec.zCoord);

                if (axisalignedbb != null && axisalignedbb.isVecInside(Vec3.createVectorHelper(currentPosVec.xCoord, currentPosVec.yCoord, currentPosVec.zCoord))) {
                    if (blackListedBlocks.contains(block)) return null;
                }
            }

            for(EntityLivingBase e : surrounding_entities) {
                if(e != player && e.boundingBox.expand(0.3F, 0.3F, 0.3F).isVecInside(currentPosVec))
                    return e;
            }
        }



        return null;
    }

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

}
