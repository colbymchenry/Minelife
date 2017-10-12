package com.minelife.realestate;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.minelife.economy.Billing;
import com.minelife.util.ArrayUtil;
import com.minelife.util.MLConfig;
import com.minelife.util.configuration.InvalidConfigurationException;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import java.io.IOException;
import java.util.*;

public class Estate implements Comparable<Estate> {

    private int id;
    private MLConfig config;

    protected Estate() {}

    public Estate(int id) throws IOException, InvalidConfigurationException {
        this.id = id;
        this.config = new MLConfig(ModRealEstate.getServerProxy().estatesDir, String.valueOf(id));
    }

    public int getID() {
        return id;
    }

    public MLConfig getConfig() {
        return config;
    }

    public double getPurchasePrice() {
        return config.getDouble("price.purchase", -1);
    }

    public boolean isPurchasable() {
        return getPurchasePrice() != -1;
    }

    public double getRentPrice() {
        return config.getDouble("price.rent", -1);
    }

    public boolean isForRent() {
        if (getRenter() != null) return false;
        return getRentPrice() != -1;
    }

    public int getRentPeriod() {
        return config.getInt("price.rent_period", 1);
    }

    public String getOutro() {
        return config.getString("outro");
    }

    public String getIntro() {
        return config.getString("intro");
    }

    public List<Permission> getGlobalPermissions() {
        List<Permission> permissions = Lists.newArrayList();
        config.getStringList("permissions.global").forEach(p -> permissions.add(Permission.valueOf(p)));
        return permissions;
    }

    public List<Permission> getOwnerPermissions() {
        List<Permission> permissions = Lists.newArrayList();
        config.getStringList("permissions.owner").forEach(p -> permissions.add(Permission.valueOf(p)));
        return permissions;
    }

    public List<Permission> getRenterPermissions() {
        List<Permission> permissions = Lists.newArrayList();
        config.getStringList("permissions.renter").forEach(p -> permissions.add(Permission.valueOf(p)));
        return permissions;
    }

    public List<Permission> getGlobalPermissionsAllowedToChange() {
        List<Permission> permissions = Lists.newArrayList();
        config.getStringList("permissions.allowedToChange").forEach(p -> permissions.add(Permission.valueOf(p)));
        return permissions;
    }

    public List<Permission> getEstatePermissions() {
        List<Permission> permissions = Lists.newArrayList();
        config.getStringList("permissions.estate").forEach(p -> permissions.add(Permission.valueOf(p)));
        return permissions;
    }

    public UUID getOwner() {
        return config.getUUID("owner", null);
    }

    public UUID getRenter() {
        return config.getUUID("renter", null);
    }

    public Map<UUID, List<Permission>> getMembers() {
        Map<UUID, List<Permission>> members = Maps.newHashMap();
        for (String member : config.getStringList("members")) {
            List<Permission> permissions = Lists.newArrayList();
            config.getStringList("members." + member).forEach(p -> permissions.add(Permission.valueOf(p)));
            members.put(UUID.fromString(member), permissions);
        }
        return members;
    }

    public AxisAlignedBB getBounds() {
        Selection s = new Selection();
        s.setPos1(config.getInt("pos1.x"), config.getInt("pos1.y"), config.getInt("pos1.z"));
        s.setPos2(config.getInt("pos2.x"), config.getInt("pos2.y"), config.getInt("pos2.z"));
        return AxisAlignedBB.getBoundingBox(s.getMin().xCoord, s.getMin().yCoord, s.getMin().zCoord,
                s.getMax().xCoord, s.getMax().yCoord, s.getMax().zCoord);
    }

    public World getWorld() {
        return Arrays.stream(MinecraftServer.getServer().worldServers)
                .filter(w -> w.getWorldInfo().getWorldName().equals(config.getString("world"))).findFirst().orElse(null);
    }

    public boolean contains(Estate estate) {
        AxisAlignedBB bounds = getBounds();
        AxisAlignedBB bounds1 = estate.getBounds();
        Vec3 pos1 = Vec3.createVectorHelper(bounds1.minX, bounds1.minY, bounds1.minZ);
        Vec3 pos2 = Vec3.createVectorHelper(bounds1.maxX, bounds1.maxY, bounds1.maxZ);
        return bounds.isVecInside(pos1) && bounds.isVecInside(pos2) &&
                estate.getWorld().getWorldInfo().getWorldName().equals(getWorld().getWorldInfo().getWorldName());
    }

    public boolean contains(World world, double x, double y, double z) {
        return getWorld().getWorldInfo().getWorldName().equals(world.getWorldInfo().getWorldName()) &&
                getBounds().isVecInside(Vec3.createVectorHelper(x, y, z));
    }

    public boolean contains(Selection selection) {
        Vec3 min = selection.getMin(), max = selection.getMax();
        return contains(selection.getWorld(), min.xCoord, min.yCoord, min.zCoord) && contains(selection.getWorld(), max.xCoord, max.yCoord, max.zCoord);
    }

    public boolean intersects(Selection selection) {
        Vec3 min = selection.getMin(), max = selection.getMax();
        if (contains(selection)) return false;
        return getBounds().intersectsWith(AxisAlignedBB.getBoundingBox(min.xCoord, min.yCoord, min.zCoord, max.xCoord, max.yCoord, max.zCoord));
    }

    public Estate getParentEstate() {
        TreeMap<Double, Estate> parentEstates = Maps.newTreeMap();
        for (Estate estate : EstateHandler.loadedEstates) {
            if (estate.contains(this)) {
                Vec3 min = Vec3.createVectorHelper(estate.getBounds().minX, estate.getBounds().minY, estate.getBounds().minZ);
                parentEstates.put(min.distanceTo(Vec3.createVectorHelper(getBounds().minX, getBounds().minY, getBounds().minZ)), estate);
            }
        }
        return parentEstates.isEmpty() ? null : parentEstates.firstEntry().getValue();
    }

    public List<Estate> getContainingEstates() {
        List<Estate> estates = Lists.newArrayList();
        for (Estate estate : EstateHandler.loadedEstates) if (contains(estate)) estates.add(estate);
        return estates;
    }

    public Estate getMasterEstate() {
        Estate estate = this;
        while (estate.getParentEstate() != null) estate = estate.getParentEstate();
        return estate;
    }

    public void deleteEstate() {
        EstateHandler.loadedEstates.remove(this);
        config.getFile().delete();
    }

    public List<Permission> getPlayerPermissions(EntityPlayer player) {
        Estate masterEstate = getMasterEstate();

        // if player is owner
        if (masterEstate.getOwner() != null && masterEstate.getOwner().equals(player.getUniqueID())) {
            return Arrays.asList(Permission.values());
        }
        // if player is renter
        else if (masterEstate.getRenter() != null && masterEstate.getRenter().equals(player.getUniqueID())) {
            return masterEstate.getRenterPermissions();
        }
        // if player is member
        else if (masterEstate.getMembers().containsKey(player.getUniqueID())) {
            return masterEstate.getMembers().get(player.getUniqueID());
        }
        // if player is not owner, renter, or member of master estate we loop through the estates inside the
        // master estate
        else {
            List<Estate> containingEstates = masterEstate.getContainingEstates();

            for (Estate e : containingEstates) {
                // if player is owner
                if (e.getOwner() != null && e.getOwner().equals(player.getUniqueID()) && e.contains(this)) {
                    return e.getOwnerPermissions();
                }
                // if player is renter
                else if (e.getRenter() != null && e.getRenter().equals(player.getUniqueID()) && e.contains(this)) {
                    return e.getRenterPermissions();
                }
                // if player is member
                else if (e.getMembers().containsKey(player.getUniqueID()) && e.contains(this)) {
                    return e.getMembers().get(player.getUniqueID());
                }
            }
        }

        return getGlobalPermissions();
    }


    public void setGlobalPermissions(List<Permission> permissions) {
        config.set("permissions.global", ArrayUtil.toStringList(permissions));
        config.save();
    }

    public void setOwnerPermissions(List<Permission> permissions) {
        config.set("permissions.owner", ArrayUtil.toStringList(permissions));
        config.save();
    }

    public void setRenterPermissions(List<Permission> permissions) {
        config.set("permissions.renter", ArrayUtil.toStringList(permissions));
        config.save();
    }

    public void setPermissionsAllowedToChange(List<Permission> permissions) {
        config.set("permissions.allowedToChange", ArrayUtil.toStringList(permissions));
        config.save();
    }

    public void setEstatePermissions(List<Permission> permissions) {
        config.set("permissions.estate", ArrayUtil.toStringList(permissions));
        config.save();
    }

    public void setRentPeriod(int period) {
        config.set("price.rent_period", period);
        config.save();
    }

    public void setOwner(UUID owner) {
        config.set("owner", owner);
        config.save();
    }

    public void setRenter(UUID renter) {
        config.set("renter", renter);
        config.save();
    }

    public void setMembers(Map<UUID, List<Permission>> members) {
        List<UUID> uuids = Lists.newArrayList();
        uuids.addAll(members.keySet());
        config.set("members", ArrayUtil.toStringList(uuids));
        for (UUID uuid : uuids)
            config.set("members." + uuid.toString(), ArrayUtil.toStringList(members.get(uuid)));
        config.save();
    }

    public void setPurchasePrice(double price) {
        config.set("price.purchase", price);
        config.save();
    }

    public void setRentPrice(double price) {
        config.set("price.rent", price);
        config.save();
    }

    public void setBounds(AxisAlignedBB bounds) {
        config.set("pos1.x", bounds.minX);
        config.set("pos1.y", bounds.minY);
        config.set("pos1.z", bounds.minZ);
        config.set("pos2.x", bounds.maxX);
        config.set("pos2.y", bounds.maxY);
        config.set("pos2.z", bounds.maxZ);
        config.save();
    }

    public void setWorld(World world) {
        config.set("world", world.getWorldInfo().getWorldName());
        config.save();
    }

    public void setIntro(String intro) {
        config.set("intro", intro);
        config.save();
    }

    public void setOutro(String outro) {
        config.set("outro", outro);
        config.save();
    }

    public void setBill(RentBillHandler bill) {
        config.set("bill", bill == null ? "" : bill.bill.getUniqueID().toString());
        config.save();
    }

    public Billing.Bill getBill() {
        if (config.getUUID("bill", null) == null) return null;
        return Billing.getBill(config.getUUID("bill", null));
    }


    @Override
    public int compareTo(Estate o) {
        return o.id == id ? 0 : 1;
    }

    @Override
    public boolean equals(Object obj) {
        return !(obj instanceof Estate) ? false : ((Estate) obj).getID() == getID();
    }

}
