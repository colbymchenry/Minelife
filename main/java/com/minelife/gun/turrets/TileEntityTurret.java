package com.minelife.gun.turrets;

import buildcraft.core.lib.inventory.SimpleInventory;
import codechicken.lib.vec.Vector3;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.minelife.MLBlocks;
import com.minelife.Minelife;
import com.minelife.gangs.ModGangs;
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
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;

import javax.swing.text.html.parser.Entity;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TileEntityTurret extends TileEntity implements IInventory {

    private EnumFacing direction = EnumFacing.NORTH;
    private EntityLivingBase target;
    private int targetID;
    private int tick;
    public float rotationYaw, rotationPitch;
    private SimpleInventory inventory;
    private boolean hitRight = true;
    private Set<EnumMob> MobWhiteList = Sets.newTreeSet();
    private Set<Integer> GangWhiteList = Sets.newTreeSet();

    public TileEntityTurret() {
        inventory = new SimpleInventory(54, "Turret", 64);
    }

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

        Map<Integer, ItemStack> ammo = getAmmo();

        if (ammo.isEmpty()) return;


        int range = MinecraftServer.getServer().getConfigurationManager().getEntityViewDistance() / 2;
        List<EntityLivingBase> entities = worldObj.getEntitiesWithinAABB(EntityLivingBase.class,
                AxisAlignedBB.getBoundingBox(xCoord - range, yCoord - range, zCoord - range,
                        xCoord + range, yCoord + range, zCoord + range));

        List<EntityLivingBase> toRemove = Lists.newArrayList();

        entities.forEach(e -> {
            for (EnumMob enumMob : MobWhiteList) {
                if(e.getClass().equals(enumMob.getMobClass())) toRemove.add(e);
            }
        });

        entities.removeAll(toRemove);


        // TODO: Implement gangs

        EntityLivingBase closestEntity = null;
        double distance = 1000;
        for (EntityLivingBase entity : entities) {
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


                    if (entity.boundingBox.expand(0.3F, 0.3F, 0.3F).isVecInside(target)) {
                        break;
                    }

                    if (block != Blocks.air && block != MLBlocks.turret && block != MLBlocks.turret.topTurret) {
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

            int slot = (int) getAmmo().keySet().toArray()[0];
            ItemStack stack = getAmmo().get(slot);
            stack.stackSize -= 1;

            setInventorySlotContents(slot, stack.stackSize < 1 ? null : stack);

            Sync();

            Minelife.NETWORK.sendToAllAround(new PacketBullet(BulletHandler.addBullet(this, ItemAmmo.AmmoType.NORMAL)),
                    new NetworkRegistry.TargetPoint(worldObj.provider.dimensionId, xCoord, yCoord, zCoord, MinecraftServer.getServer().getConfigurationManager().getEntityViewDistance()));
        }

    }

    public Map<Integer, ItemStack> getAmmo() {
        Map<Integer, ItemStack> ammo = Maps.newHashMap();
        for (int i = 0; i < getSizeInventory(); i++) {
            if (getStackInSlot(i) != null) ammo.put(i, getStackInSlot(i));
        }

        return ammo;
    }

    public double distance(Vec3 v1, Vec3 v2) {
        return Math.sqrt(NumberConversions.square(v1.xCoord - v2.xCoord) + NumberConversions.square(v1.yCoord - v2.yCoord) + NumberConversions.square(v1.zCoord - v2.zCoord));
    }

    @Override
    public void writeToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);
        tagCompound.setString("direction", this.direction.name());
        tagCompound.setInteger("targetID", targetID);
        inventory.writeToNBT(tagCompound, "Items");

        String mobs = "";
        String gangs = "";
        for (EnumMob enumMob : MobWhiteList) mobs += enumMob.name() + ",";
        for (int gangID : GangWhiteList) gangs += gangID + ",";

        tagCompound.setString("MobWhiteList", mobs);
        tagCompound.setString("GangWhiteList", gangs);
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);
        this.direction = tagCompound.hasKey("direction") ?
                EnumFacing.valueOf(tagCompound.getString("direction")) : EnumFacing.NORTH;
        this.targetID = tagCompound.getInteger("targetID");
        this.inventory.readFromNBT(tagCompound, "Items");

        if (tagCompound.hasKey("MobWhiteList")) {
            for (String mob : tagCompound.getString("MobWhiteList").split(",")) {
                if (!mob.isEmpty()) {
                    MobWhiteList.add(EnumMob.valueOf(mob));
                }
            }
        }

        if (tagCompound.hasKey("GangWhiteList")) {
            for (String gangID : tagCompound.getString("GangWhiteList").split(",")) {
                if (!gangID.isEmpty()) {
                    GangWhiteList.add(Integer.parseInt(gangID));
                }
            }
        }
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

    public void Sync() {
        this.getWorldObj().markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
        this.markDirty();
    }

    public Vector getLookVec() {
        if (target == null) return new Vector(0, 0, 0);

        Vector from = new Vector(xCoord + 0.5, yCoord + 1.5, zCoord + 0.5);
        Vector to = new Vector(target.posX, target.posY, target.posZ);
        Vector vector = to.subtract(from);
        return vector.multiply(0.05);
    }

    public Vector getLookVec(EntityLivingBase target) {
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

    @Override
    public int getSizeInventory() {
        return inventory.getSizeInventory();
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return inventory.getStackInSlot(slot);
    }

    @Override
    public ItemStack decrStackSize(int p_70298_1_, int p_70298_2_) {
        return inventory.decrStackSize(p_70298_1_, p_70298_2_);
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int p_70304_1_) {
        return inventory.getStackInSlotOnClosing(p_70304_1_);
    }

    @Override
    public void setInventorySlotContents(int p_70299_1_, ItemStack p_70299_2_) {
        inventory.setInventorySlotContents(p_70299_1_, p_70299_2_);
    }

    @Override
    public String getInventoryName() {
        return inventory.getInventoryName();
    }

    @Override
    public boolean hasCustomInventoryName() {
        return inventory.hasCustomInventoryName();
    }

    @Override
    public int getInventoryStackLimit() {
        return inventory.getInventoryStackLimit();
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer p_70300_1_) {
        return true;
    }

    @Override
    public void openInventory() {

    }

    @Override
    public void closeInventory() {

    }

    @Override
    public boolean isItemValidForSlot(int p_94041_1_, ItemStack p_94041_2_) {
        System.out.println("CALLED");
        return p_94041_2_ != null && p_94041_2_.getItem() instanceof ItemAmmo;
    }

    public Set<Integer> getGangWhiteList() {
        return GangWhiteList;
    }

    public Set<EnumMob> getMMobWhiteList() {
        return MobWhiteList;
    }

    public void setGangWhiteList(Set<Integer> gangWhiteList) {
        GangWhiteList = gangWhiteList;
        Sync();
    }

    public void setMobWhiteList(Set<EnumMob> MobWhiteList) {
        this.MobWhiteList = MobWhiteList;
        Sync();
    }
}
