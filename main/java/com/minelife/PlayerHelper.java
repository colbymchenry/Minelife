package com.minelife;

import com.google.common.collect.Lists;
import com.minelife.gun.EntityShotEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFurnace;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;

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
        for(int i = 0; i < range; i++) {
            currentPosVec = currentPosVec.addVector(lookVec.xCoord, lookVec.yCoord, lookVec.zCoord);

            player.worldObj.spawnParticle("smoke", currentPosVec.xCoord, currentPosVec.yCoord, currentPosVec.zCoord, 0.0F, 0.0F, 0.0F);

            block = player.worldObj.getBlock((int) currentPosVec.xCoord, (int) currentPosVec.yCoord, (int) currentPosVec.zCoord);

            if(block != null &&
                    block != Blocks.air &&
                    block != Blocks.grass &&
                    block != Blocks.tallgrass &&
                    block != Blocks.water &&
                    block != Blocks.flowing_water &&
                    block != Blocks.double_plant) {
                return null;
            }

            for (EntityLivingBase entity1 : surrounding_entities) {
                if (entity1.canBeCollidedWith() && entity1 != player) {
                  if(currentPosVec.xCoord >= entity1.boundingBox.minX && currentPosVec.xCoord <= entity1.boundingBox.maxX
                          && currentPosVec.yCoord >= entity1.boundingBox.minY && currentPosVec.yCoord <= entity1.boundingBox.maxY
                          && currentPosVec.zCoord >= entity1.boundingBox.minZ && currentPosVec.zCoord <= entity1.boundingBox.maxZ) {
                      return entity1;
                  }
                }
            }

        }

//        for (EntityLivingBase entity : surrounding_entities) {
//            if (entity != player) {
//                double minX = (entity.boundingBox.minX - player.posX) / lookVec.xCoord;
//                double maxX = (entity.boundingBox.maxX - player.posX) / lookVec.xCoord;
//
//                double minY = (entity.boundingBox.minY - (player.posY + player.eyeHeight)) / lookVec.yCoord;
//                double maxY = (entity.boundingBox.maxY - (player.posY + player.eyeHeight)) / lookVec.yCoord;
//
//                double minZ = (entity.boundingBox.minZ - player.posZ) / lookVec.zCoord;
//                double maxZ = (entity.boundingBox.maxZ - player.posZ) / lookVec.zCoord;
//
//                if (lookVec.xCoord < 0) {
//                    double min_x = minX;
//                    minX = maxX;
//                    maxX = min_x;
//                }
//
//                if (lookVec.yCoord < 0) {
//                    double min_y = minY;
//                    minY = maxY;
//                    maxY = min_y;
//                }
//
//                if (lookVec.zCoord < 0) {
//                    double min_z = minZ;
//                    minZ = maxZ;
//                    maxZ = min_z;
//                }
//
//                double largestMinValue = minX;
//
//                if (minZ > minX && minZ > minY) {
//                    largestMinValue = minZ;
//                } else if (minY > minX && minY > minZ) {
//                    largestMinValue = minY;
//                }
//
//                double smallestMaxValue = maxX;
//
//                if (maxZ < maxX && maxZ < maxY) {
//                    smallestMaxValue = maxZ;
//                } else if (maxY < maxX && maxY < maxZ) {
//                    smallestMaxValue = maxY;
//                }
//
//                if (smallestMaxValue > largestMinValue) {
//                    return entity;
//                }
//            }
//        }

        return null;
    }

}
