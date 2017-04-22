package com.minelife;

import com.google.common.collect.Lists;
import com.minelife.gun.EntityShotEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
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

        List<Vec3> blocks = Lists.newArrayList();

        for (int x = (int) surrounding_check.minX; x < surrounding_check.maxX; x++) {
            for (int y = (int) surrounding_check.minY; y < surrounding_check.maxY; y++) {
                for (int z = (int) surrounding_check.minZ; z < surrounding_check.maxZ; z++) {
                    if(player.worldObj.getBlock(x, y, z) != null && player.worldObj.getBlock(x, y, z) != Blocks.air && player.worldObj.getBlock(x, y, z) != Blocks.water) blocks.add(Vec3.createVectorHelper(x, y, z));
                }
            }
        }

        List<EntityLivingBase> surrounding_entities = player.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, surrounding_check);
        Vec3 lookVec = player.getLookVec();

        // TODO: Fix Y value issue when looking up in the air it's not getting the block right or some shit.
        for(Vec3 blockVec : blocks) {
            double minX = (blockVec.xCoord - player.posX) / lookVec.xCoord;
            double maxX = (blockVec.xCoord + 1 - player.posX) / lookVec.xCoord;

            double minY = (blockVec.yCoord - (player.posY + player.eyeHeight)) / lookVec.yCoord;
            double maxY = (blockVec.yCoord + 1 - (player.posY + player.eyeHeight)) / lookVec.yCoord;

            double minZ = (blockVec.zCoord - player.posZ) / lookVec.zCoord;
            double maxZ = (blockVec.zCoord + 1 - player.posZ) / lookVec.zCoord;

            if (lookVec.xCoord < 0) {
                double min_x = minX;
                minX = maxX;
                maxX = min_x;
            }

            if (lookVec.yCoord < 0) {
                double min_y = minY;
                minY = maxY;
                maxY = min_y;
            }

            if (lookVec.zCoord < 0) {
                double min_z = minZ;
                minZ = maxZ;
                maxZ = min_z;
            }

            double largestMinValue = minX;

            if (minZ > minX && minZ > minY) {
                largestMinValue = minZ;
            } else if (minY > minX && minY > minZ) {
                largestMinValue = minY;
            }

            double smallestMaxValue = maxX;

            if (maxZ < maxX && maxZ < maxY) {
                smallestMaxValue = maxZ;
            } else if (maxY < maxX && maxY < maxZ) {
                smallestMaxValue = maxY;
            }

            if (smallestMaxValue > largestMinValue) {
                System.out.println(player.worldObj.getBlock((int)blockVec.xCoord, (int)blockVec.yCoord, (int)blockVec.zCoord).getLocalizedName());
                break;
            }
        }













        for (EntityLivingBase entity : surrounding_entities) {
            if (entity != player) {
                double minX = (entity.boundingBox.minX - player.posX) / lookVec.xCoord;
                double maxX = (entity.boundingBox.maxX - player.posX) / lookVec.xCoord;

                double minY = (entity.boundingBox.minY - (player.posY + player.eyeHeight)) / lookVec.yCoord;
                double maxY = (entity.boundingBox.maxY - (player.posY + player.eyeHeight)) / lookVec.yCoord;

                double minZ = (entity.boundingBox.minZ - player.posZ) / lookVec.zCoord;
                double maxZ = (entity.boundingBox.maxZ - player.posZ) / lookVec.zCoord;

                if (lookVec.xCoord < 0) {
                    double min_x = minX;
                    minX = maxX;
                    maxX = min_x;
                }

                if (lookVec.yCoord < 0) {
                    double min_y = minY;
                    minY = maxY;
                    maxY = min_y;
                }

                if (lookVec.zCoord < 0) {
                    double min_z = minZ;
                    minZ = maxZ;
                    maxZ = min_z;
                }

                double largestMinValue = minX;

                if (minZ > minX && minZ > minY) {
                    largestMinValue = minZ;
                } else if (minY > minX && minY > minZ) {
                    largestMinValue = minY;
                }

                double smallestMaxValue = maxX;

                if (maxZ < maxX && maxZ < maxY) {
                    smallestMaxValue = maxZ;
                } else if (maxY < maxX && maxY < maxZ) {
                    smallestMaxValue = maxY;
                }

                if (smallestMaxValue > largestMinValue) {
                    return entity;
                }
            }
        }

        return null;
    }

}
