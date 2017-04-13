package com.minelife;

import com.minelife.gun.EntityShotEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;

import java.util.List;
import java.util.UUID;

public class PlayerHelper {

    @SideOnly(Side.SERVER)
    public static EntityPlayerMP getPlayer(UUID playerUUID) {
        for (WorldServer worldServer : MinecraftServer.getServer().worldServers) {
            if(worldServer.func_152378_a(playerUUID) != null)
                return (EntityPlayerMP) worldServer.func_152378_a(playerUUID);
        }

        return null;
    }

    public static EntityLivingBase getTargetEntity(EntityPlayer player, int range) {
        AxisAlignedBB surrounding_check = AxisAlignedBB.getBoundingBox(player.posX - range, player.posY - range, player.posZ - range, player.posX + range, player.posY + range, player.posZ + range);

        List<EntityLivingBase> surrounding_entities = player.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, surrounding_check);
        Vec3 lookVec = player.getLookVec();

        for (EntityLivingBase entity : surrounding_entities) {
            if (entity != player) {
                double minX = (entity.boundingBox.minX - player.posX) / lookVec.xCoord;
                double maxX = (entity.boundingBox.maxX - player.posX) / lookVec.xCoord;

                double minY = (entity.boundingBox.minY - player.posY) / lookVec.yCoord;
                double maxY = (entity.boundingBox.maxY - player.posY) / lookVec.yCoord;

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
