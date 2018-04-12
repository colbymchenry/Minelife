package com.minelife.guns.turret;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.minelife.Minelife;
import com.minelife.drugs.ModDrugs;
import com.minelife.guns.Bullet;
import com.minelife.guns.ModGuns;
import com.minelife.guns.item.EnumGun;
import com.minelife.guns.packet.PacketBullet;
import com.minelife.realestate.Estate;
import com.minelife.realestate.ModRealEstate;
import com.minelife.util.MLInventory;
import com.minelife.util.MLTileEntity;
import com.minelife.util.client.render.Vector;
import ic2.core.ref.ItemName;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import java.util.*;


public class TileEntityTurret extends MLTileEntity implements ITickable {

    private EnumFacing direction = EnumFacing.NORTH;
    private EntityLivingBase target;
    private int targetID, tick;
    private MLInventory inventory;
    private boolean hitRight = true;
    private Set<EnumMob> MobWhiteList = Sets.newTreeSet();
    private Map<UUID, Long> AgroPlayers = Maps.newHashMap();
    private UUID owner;
    private boolean waitForAgroPlayer = false;

    public float rotationYaw, rotationPitch;

    public TileEntityTurret() {
        inventory = new MLInventory(54, "turret", 64);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        NBTTagCompound inventoryTag = new NBTTagCompound();
        inventory.writeToNBT(inventoryTag);
        compound.setTag("Inventory", inventoryTag);
        compound.setString("direction", this.direction.name());
        compound.setInteger("targetID", targetID);
        if (owner != null)
            compound.setString("owner", owner.toString());
        String mobs = "";
        for (EnumMob enumMob : MobWhiteList) mobs += enumMob.name() + ",";

        compound.setString("MobWhiteList", mobs);
        compound.setBoolean("waitForAgroPlayer", waitForAgroPlayer);
        return super.writeToNBT(compound);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.direction = compound.hasKey("direction") ?
                EnumFacing.valueOf(compound.getString("direction")) : EnumFacing.NORTH;
        this.targetID = compound.getInteger("targetID");
        inventory.readFromNBT(compound.getCompoundTag("Inventory"));
        if (compound.hasKey("owner"))
            this.owner = UUID.fromString(compound.getString("owner"));

        if (compound.hasKey("MobWhiteList")) {
            MobWhiteList.clear();
            for (String mob : compound.getString("MobWhiteList").split(",")) {
                if (!mob.isEmpty()) {
                    MobWhiteList.add(EnumMob.valueOf(mob));
                }
            }
        }

        waitForAgroPlayer = compound.getBoolean("waitForAgroPlayer");
    }

    @Override
    public void update() {
        /**
         * Handles the rotation animation for the client
         */
        if (getWorld().isRemote) {
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
        Map<Integer, ItemStack> ammo = getAmmo();

        tick++;

        if (tick < 10) return;

        tick = 0;

        if (ammo.isEmpty()) return;


        target = null;
        targetID = -1;

        int range = 20;
        List<EntityLivingBase> nearbyTargets = getWorld().getEntitiesWithinAABB(EntityLivingBase.class,
                new AxisAlignedBB(getPos().getX() - range, getPos().getY() - range, getPos().getZ() - range,
                        getPos().getX() + range, getPos().getY() + range, getPos().getZ() + range));

        List<EntityLivingBase> toRemove = Lists.newArrayList();
        Estate estate = ModRealEstate.getEstateAt(getWorld(), getPos());
        Set<UUID> memberKeys = estate != null ? estate.getSurroundingMembers().keySet() : Sets.newTreeSet();
        Set<UUID> ownersKeys = estate != null ? estate.getSurroundingOwners() : Sets.newTreeSet();

        /**
         * Remove all white listed mobs and players
         */
        nearbyTargets.forEach(e -> {
            for (EnumMob enumMob : MobWhiteList) {
                if (e.getClass().equals(enumMob.getMobClass())) toRemove.add(e);
            }

            if (e instanceof EntityPlayer) {
                if (((EntityPlayerMP) e).isCreative()) toRemove.add(e);
                if (owner != null && owner.equals(e.getUniqueID())) toRemove.add(e);

                boolean agro = e.getHeldItemMainhand().getItem() == ModGuns.itemGun
                        || e.getHeldItemMainhand().getItem() == ItemName.dynamite.getInstance()
                        || e.getHeldItemMainhand().getItem() == ItemName.dynamite_sticky.getInstance();

                if (estate != null) {
                    if (Objects.equals(estate.getOwnerID(), e.getUniqueID())) toRemove.add(e);
                    if (memberKeys.contains(e.getUniqueID())) toRemove.add(e);
                    if (ownersKeys.contains(e.getUniqueID())) toRemove.add(e);

                    if (isWaitForAgroPlayer()) {
                        if (agro) {
                            AgroPlayers.put(e.getUniqueID(), System.currentTimeMillis() + (60000L * 5));
                        } else {
                            if (!AgroPlayers.containsKey(e.getUniqueID())) toRemove.add(e);
                        }
                    }
                }

                if (Objects.equals(getOwner(), e.getUniqueID())) toRemove.add(e);

                if (AgroPlayers.containsKey(e.getUniqueID()) && System.currentTimeMillis() > AgroPlayers.get(e.getUniqueID())) {
                    toRemove.add(e);
                    AgroPlayers.remove(e.getUniqueID());
                }
            }

        });

        nearbyTargets.removeAll(toRemove);

        /**
         * Spawn bullet on capable entity
         */
        targetLoop:
        for (EntityLivingBase nearbyTarget : nearbyTargets) {
            Vector vector = getLookVec(nearbyTarget);
            Vec3d vec3d = new Vec3d(vector.getX(), vector.getY(), vector.getZ());
            Bullet bullet = new Bullet(getWorld(), getPos().getX() + 0.5, getPos().getY() + 1.5, getPos().getZ() + 0.5, 0L, vec3d, 3.5, 0, null);
            bulletLoop:
            for (int i = 0; i < 10; i++) {
                Bullet.HitResult result = bullet.tick(0, true);
                if (result.getBlockState() != null) {
                    target = null;
                    targetID = -1;
                    break targetLoop;
                }
                if (result.getEntity() != null) {
                    target = result.getEntity();
                    targetID = result.getEntity().getEntityId();
                    break targetLoop;
                }
            }
        }

        if (target != null) {
            int slot = (int) getAmmo().keySet().toArray()[0];
            ItemStack stack = getAmmo().get(slot);
            stack.setCount(stack.getCount() - 1);

            inventory.setInventorySlotContents(slot, stack.getCount() < 1 ? ItemStack.EMPTY : stack);
            Vector vector = getLookVec(target);
            Vec3d vec3d = new Vec3d(vector.getX(), vector.getY(), vector.getZ());
            Bullet bullet = new Bullet(getWorld(), getPos().getX() + 0.5, getPos().getY() + 1.5, getPos().getZ() + 0.5, 0L, vec3d, 3.5, 2.5, null);
            Bullet.BULLETS.add(bullet);
            Minelife.getNetwork().sendToAllAround(new PacketBullet(EnumGun.M4A4, bullet),
                    new NetworkRegistry.TargetPoint(getWorld().provider.getDimension(), getPos().getX(), getPos().getY(), getPos().getZ(), 80));
            sendUpdates();
        }
    }

    public MLInventory getInventory() {
        return inventory;
    }

    public Vector getLookVec(EntityLivingBase target) {
        if (target == null) return new Vector(0, 0, 0);

        Vector from = new Vector(getPos().getX() + 0.5, getPos().getY() + 1.5, getPos().getZ() + 0.5);
        Vector to = new Vector(target.posX, target.posY + target.getEyeHeight(), target.posZ);
        Vector vector = to.subtract(from);
        return vector.multiply(0.05);
    }

    public int getTargetID() {
        return targetID;
    }

    public void setDirection(EnumFacing direction) {
        this.direction = direction;
        this.sendUpdates();
    }

    public EnumFacing getDirection() {
        return direction;
    }

    public UUID getOwner() {
        return owner;
    }

    public void setMobWhiteList(Set<EnumMob> MobWhiteList) {
        this.MobWhiteList = MobWhiteList;
        this.sendUpdates();
    }

    public Set<EnumMob> getMobWhiteList() {
        return MobWhiteList;
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
        this.sendUpdates();
    }

    public Map<Integer, ItemStack> getAmmo() {
        Map<Integer, ItemStack> ammo = Maps.newHashMap();

        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            if (inventory.getStackInSlot(i).getItem() == ModGuns.itemAmmo)
                ammo.put(i, inventory.getStackInSlot(i));
        }

        return ammo;
    }

    public void setWaitForAgroPlayer(boolean waitForAgroPlayer) {
        this.waitForAgroPlayer = waitForAgroPlayer;
        sendUpdates();
    }

    public boolean isWaitForAgroPlayer() {
        return waitForAgroPlayer;
    }

    public void setAgroPlayers(Map<UUID, Long> agroPlayers) {
        AgroPlayers = agroPlayers;
        sendUpdates();
    }

    public Map<UUID, Long> getAgroPlayers() {
        return AgroPlayers;
    }

    public boolean hasPermissionToModifySettings(EntityPlayerMP Player) {
        Estate estate = ModRealEstate.getEstateAt(getWorld(), getPos());

        if (Objects.equals(getOwner(), Player.getUniqueID())) return true;

        if (estate != null) {
            if (Objects.equals(estate.getOwnerID(), Player.getUniqueID())) return true;
            if (estate.getSurroundingMembers().keySet().contains(Player.getUniqueID())) return true;
            if (estate.getSurroundingOwners().contains(Player.getUniqueID())) return true;
        }

        return false;
    }
}