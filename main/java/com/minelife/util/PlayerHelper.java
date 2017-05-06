package com.minelife.util;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import net.minecraft.world.WorldServer;

import java.lang.reflect.Field;
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
        AxisAlignedBB surrounding_check = AxisAlignedBB.getBoundingBox(player.posX - range, player.posY - range, player.posZ - range, player.posX + range, player.posY + range, player.posZ + range);

        List<EntityLivingBase> surrounding_entities = player.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, surrounding_check);
        Vec3 lookVec = player.getLookVec();

        Vec3 currentPosVec = Vec3.createVectorHelper(player.posX, player.posY + player.eyeHeight, player.posZ);
        Block block;
        for (int i = 0; i < range; i++) {
            currentPosVec = currentPosVec.addVector(lookVec.xCoord, lookVec.yCoord, lookVec.zCoord);

            player.worldObj.spawnParticle("smoke", currentPosVec.xCoord, currentPosVec.yCoord, currentPosVec.zCoord, 0.0F, 0.0F, 0.0F);

            block = player.worldObj.getBlock((int) currentPosVec.xCoord, (int) currentPosVec.yCoord, (int) currentPosVec.zCoord);

            if (block != null && block.getMaterial() != Material.air) {
                block.setBlockBoundsBasedOnState(player.worldObj, (int) currentPosVec.xCoord, (int) currentPosVec.yCoord, (int) currentPosVec.zCoord);
                AxisAlignedBB axisalignedbb = block.getCollisionBoundingBoxFromPool(player.worldObj, (int) currentPosVec.xCoord, (int) currentPosVec.yCoord, (int) currentPosVec.zCoord);

                if (axisalignedbb != null && axisalignedbb.isVecInside(Vec3.createVectorHelper(currentPosVec.xCoord, currentPosVec.yCoord, currentPosVec.zCoord))) {
                    if (block != Blocks.air &&
                            block != Blocks.grass &&
                            block != Blocks.tallgrass &&
                            block != Blocks.water &&
                            block != Blocks.flowing_water &&
                            block != Blocks.double_plant &&
                            block != Blocks.red_flower &&
                            block != Blocks.yellow_flower) {
                        return null;
                    }
                }
            }

            for (EntityLivingBase entity1 : surrounding_entities) {
                if (entity1.canBeCollidedWith() && entity1 != player) {
                    if (entity1.boundingBox.isVecInside(Vec3.createVectorHelper(currentPosVec.xCoord, currentPosVec.yCoord, currentPosVec.zCoord))) {
                        return entity1;
                    }
                }
            }

        }

        return null;
    }

    @SideOnly(Side.CLIENT)
    public static final void zoom(double amount) {

        if(amount < 1) amount = 1;

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
