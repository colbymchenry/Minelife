package com.minelife.gun.turrets;

import codechicken.lib.vec.Vector3;
import com.google.common.collect.Maps;
import com.minelife.MLBlocks;
import com.minelife.Minelife;
import com.minelife.gun.bullets.BulletHandler;
import com.minelife.gun.item.ammos.ItemAmmo;
import com.minelife.gun.packet.PacketBullet;
import com.minelife.util.NumberConversions;
import com.minelife.util.Vector;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;

import javax.swing.text.html.parser.Entity;
import java.util.List;
import java.util.Map;

public class TileEntityTurret extends TileEntity {

    private EnumFacing direction = EnumFacing.NORTH;
    private EntityLiving target;
    private int targetID;
    private int tick;
    public float rotationYaw, rotationPitch;

    private boolean hitRight = true;

    @Override
    public void updateEntity() {

        /**
         * Handles the rotation animation for the client
         */
        if (worldObj.isRemote) {
            if (rotationYaw < -45) {
                hitRight = false;
                rotationYaw += 0.8F;
            } else if (rotationYaw > 45) {
                hitRight = true;
                rotationYaw -= 0.8F;
            } else {
                if (hitRight) {
                    rotationYaw -= 0.8F;
                } else {
                    rotationYaw += 0.8F;
                }
            }

            return;
        }


        /**
         * From here down is pure server
         */
        tick++;

        if (tick < 20) return;

        tick = 0;

        int range = MinecraftServer.getServer().getConfigurationManager().getEntityViewDistance() / 2;
        List<EntityLiving> entities = worldObj.getEntitiesWithinAABB(EntityLiving.class,
                AxisAlignedBB.getBoundingBox(xCoord - range, yCoord - range, zCoord - range,
                        xCoord + range, yCoord + range, zCoord + range));

        EntityLiving closestEntity = null;
        double distance = 1000;
        for (EntityLiving entity : entities) {
            if (Vec3.createVectorHelper(xCoord, yCoord, zCoord).distanceTo(Vec3.createVectorHelper(entity.posX, entity.posY, entity.posZ)) < distance) {
                Vector v = getLookVec(entity);
                Vec3 lookVec = Vec3.createVectorHelper(v.getX(), v.getY(), v.getZ());
                Vec3 origin = Vec3.createVectorHelper(xCoord + 0.5, yCoord + 1.5, zCoord + 0.5);
                Vec3 target = origin.addVector(lookVec.xCoord, lookVec.yCoord, lookVec.zCoord);

                boolean foundBlock = false;

                for (int i = 0; i < range; i++) {
                    int x = MathHelper.floor_double(origin.xCoord);
                    int y = MathHelper.floor_double(origin.yCoord);
                    int z = MathHelper.floor_double(origin.zCoord);
                    Block block = worldObj.getBlock(x, y, z);


                    if(entity.boundingBox.expand(0.3F, 0.3F, 0.3F).isVecInside(target)) {
                        break;
                    }

                    if (block != Blocks.air && block != MLBlocks.turret) {
                        if (block.getCollisionBoundingBoxFromPool(worldObj, x, y, z) != null) {
                            if (block.getCollisionBoundingBoxFromPool(worldObj, x, y, z).isVecInside(target)) {
                                foundBlock = true;
                                break;
                            }
                        }
                    }

                    origin = origin.addVector(lookVec.xCoord, lookVec.yCoord, lookVec.zCoord);
                    target = target.addVector(lookVec.xCoord, lookVec.yCoord, lookVec.zCoord);
                }

                if (!foundBlock) {
                    closestEntity = entity;
                    distance = Vec3.createVectorHelper(xCoord, yCoord, zCoord).distanceTo(Vec3.createVectorHelper(entity.posX, entity.posY, entity.posZ));
                }
            }
        }


        if (closestEntity != null) {
            this.target = closestEntity;
            this.targetID = closestEntity.getEntityId();

            this.getWorldObj().markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
            this.markDirty();

            Minelife.NETWORK.sendToAllAround(new PacketBullet(BulletHandler.addBullet(this, ItemAmmo.AmmoType.NORMAL)),
                    new NetworkRegistry.TargetPoint(worldObj.provider.dimensionId, xCoord, yCoord, zCoord, MinecraftServer.getServer().getConfigurationManager().getEntityViewDistance()));
        }

    }

    public double distance(Vec3 v1, Vec3 v2) {
        return Math.sqrt(NumberConversions.square(v1.xCoord - v2.xCoord) + NumberConversions.square(v1.yCoord - v2.yCoord) + NumberConversions.square(v1.zCoord - v2.zCoord));
    }

    @Override
    public void writeToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);
        tagCompound.setString("direction", this.direction.name());
        tagCompound.setInteger("targetID", targetID);
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);
        this.direction = tagCompound.hasKey("direction") ?
                EnumFacing.valueOf(tagCompound.getString("direction")) : EnumFacing.NORTH;
        this.targetID = tagCompound.getInteger("targetID");
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound nbtTag = new NBTTagCompound();
        this.writeToNBT(nbtTag);
        return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 1, nbtTag);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
        this.readFromNBT(pkt.func_148857_g());
    }

    public Vector getLookVec() {
        if (target == null) return new Vector(0, 0, 0);

        Vector from = new Vector(xCoord + 0.5, yCoord + 1.5, zCoord + 0.5);
        Vector to = new Vector(target.posX, target.posY, target.posZ);
        Vector vector = to.subtract(from);
        return vector.multiply(0.05);
    }

    public Vector getLookVec(EntityLiving target) {
        if (target == null) return new Vector(0, 0, 0);

        Vector from = new Vector(xCoord + 0.5, yCoord + 1.5, zCoord + 0.5);
        Vector to = new Vector(target.posX, target.posY, target.posZ);
        Vector vector = to.subtract(from);
        return vector.multiply(0.05);
    }

    public int getTargetID() {
        return targetID;
    }

    public void setDirection(EnumFacing direction) {
        this.direction = direction;
        this.getWorldObj().markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
        this.markDirty();
    }

    public EnumFacing getDirection() {
        return direction;
    }
}
