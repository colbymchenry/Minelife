package com.minelife.realestate;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
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

    protected Estate() {
    }

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

    public Set<Permission> getGlobalPermissions() {
        Set<Permission> permissions = Sets.newTreeSet();
        config.getStringList("permissions.global").forEach(p -> permissions.add(Permission.valueOf(p)));
        return permissions;
    }

    public Set<Permission> getOwnerPermissions() {
        Set<Permission> permissions = Sets.newTreeSet();
        config.getStringList("permissions.owner").forEach(p -> permissions.add(Permission.valueOf(p)));
        return permissions;
    }

    public Set<Permission> getRenterPermissions() {
        Set<Permission> permissions = Sets.newTreeSet();
        config.getStringList("permissions.renter").forEach(p -> permissions.add(Permission.valueOf(p)));
        return permissions;
    }

    public Set<Permission> getGlobalPermissionsAllowedToChange() {
        Set<Permission> permissions = Sets.newTreeSet();
        config.getStringList("permissions.allowedToChange").forEach(p -> permissions.add(Permission.valueOf(p)));
        return permissions;
    }

    public Set<Permission> getEstatePermissions() {
        Set<Permission> permissions = Sets.newTreeSet();
        config.getStringList("permissions.estate").forEach(p -> permissions.add(Permission.valueOf(p)));
        return permissions;
    }

    public UUID getOwner() {
        return config.getUUID("owner", null);
    }

    public UUID getRenter() {
        return config.getUUID("renter", null);
    }

    public Map<UUID, Set<Permission>> getMembers() {
        Map<UUID, Set<Permission>> members = Maps.newHashMap();
        if(config.getConfigurationSection("members") != null) {
            for (String member : config.getConfigurationSection("members").getKeys(true)) {
                Set<Permission> permissions = Sets.newTreeSet();
                config.getStringList("members." + member).forEach(p -> permissions.add(Permission.valueOf(p)));
                members.put(UUID.fromString(member), permissions);
            }
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

    public boolean isAbsoluteOwner(UUID playerID) {
        Estate parentEstate = getParentEstate();
        if (parentEstate == null && Objects.equals(playerID, getOwner())) return true;
        while (parentEstate != null) {
            if (Objects.equals(playerID, getOwner())) return true;
            parentEstate = parentEstate.getParentEstate();
        }
        return false;
    }

    public Set<Permission> getPlayerPermissions(UUID player) {
        Set<Permission> permissions = Sets.newTreeSet();
        permissions.addAll(getActualGlobalPerms());

        if (getParentEstate() == null && Objects.equals(player, getOwner())) {
            permissions.addAll(toSet(Arrays.asList(Permission.values())));
            return permissions;
        }

        if(Objects.equals(player, getMasterEstate().getOwner())) {
            permissions.addAll(toSet(Arrays.asList(Permission.values())));
            return permissions;
        }

        if (Objects.equals(player, getOwner())) {
            permissions.addAll(getActualOwnerPerms());
            permissions.addAll(getActualPermsAllowedToChange());
        } else if (Objects.equals(player, getRenter())) {
            permissions.addAll(getActualRenterPerms());
            permissions.addAll(getActualPermsAllowedToChange());
        } else if (getMembers().containsKey(player)) {
            permissions.addAll(getActualMemberPerms(player));
        }

        return permissions;
    }

    public Set<Permission> getActualGlobalPerms() {
        Set<Permission> globalPermissions = getGlobalPermissions();
        Set<Permission> toRemove = Sets.newTreeSet();

        Estate parentEstate = getParentEstate();

        while (parentEstate != null) {

            for (Permission p : globalPermissions)
                if (!parentEstate.getGlobalPermissions().contains(p) && !parentEstate.getPlayerPermissions(parentEstate.getOwner()).contains(p)) toRemove.add(p);

            parentEstate = parentEstate.getParentEstate();
        }

        globalPermissions.removeAll(toRemove);
        return globalPermissions;
    }

    public Set<Permission> getActualRenterPerms() {
        Set<Permission> renterPerms = getRenterPermissions();
        Set<Permission> toRemove = Sets.newTreeSet();

        Estate parentEstate = getParentEstate();

        while (parentEstate != null) {

            for (Permission p : renterPerms)
                if (!parentEstate.getRenterPermissions().contains(p) && !parentEstate.getPlayerPermissions(parentEstate.getOwner()).contains(p)) toRemove.add(p);

            parentEstate = parentEstate.getParentEstate();
        }

        renterPerms.removeAll(toRemove);
        return renterPerms;
    }

    public Set<Permission> getActualOwnerPerms() {
        Set<Permission> ownerPerms = getOwnerPermissions();
        Set<Permission> toRemove = Sets.newTreeSet();

        Estate parentEstate = getParentEstate();

        while (parentEstate != null) {

            for (Permission p : ownerPerms)
                if (!parentEstate.getOwnerPermissions().contains(p) && !parentEstate.getPlayerPermissions(parentEstate.getOwner()).contains(p)) toRemove.add(p);

            parentEstate = parentEstate.getParentEstate();
        }

        ownerPerms.removeAll(toRemove);
        return ownerPerms;
    }

    public Set<Permission> getActualPermsAllowedToChange() {
        Set<Permission> allowedToChange = getGlobalPermissionsAllowedToChange();
        Set<Permission> toRemove = Sets.newTreeSet();

        Estate parentEstate = getParentEstate();

        while (parentEstate != null) {

            for (Permission p : allowedToChange)
                if (!parentEstate.getGlobalPermissionsAllowedToChange().contains(p) && !parentEstate.getPlayerPermissions(parentEstate.getOwner()).contains(p)) toRemove.add(p);

            parentEstate = parentEstate.getParentEstate();
        }

        allowedToChange.removeAll(toRemove);
        return allowedToChange;
    }

    // 0TODO: Will still need some testings and fine tuning
    public Set<Permission> getActualMemberPerms(UUID playerID) {
        Set<Permission> memberPerms = getMembers().get(playerID);
        Set<Permission> toRemove = Sets.newTreeSet();

        Estate parentEstate = getParentEstate();

        while (parentEstate != null) {

            for (Permission p : memberPerms)
                if (parentEstate.getMembers().containsKey(playerID) &&
                        !parentEstate.getMembers().get(playerID).contains(p))
                    toRemove.add(p);

            parentEstate = parentEstate.getParentEstate();
        }

        memberPerms.removeAll(toRemove);
        return memberPerms;
    }

    public void setGlobalPermissions(Set<Permission> permissions) {
        config.set("permissions.global", ArrayUtil.toStringList(toList(permissions)));
        config.save();
    }

    public void setOwnerPermissions(Set<Permission> permissions) {
        config.set("permissions.owner", ArrayUtil.toStringList(toList(permissions)));
        config.save();
    }

    public void setRenterPermissions(Set<Permission> permissions) {
        config.set("permissions.renter", ArrayUtil.toStringList(toList(permissions)));
        config.save();
    }

    public void setPermissionsAllowedToChange(Set<Permission> permissions) {
        config.set("permissions.allowedToChange", ArrayUtil.toStringList(toList(permissions)));
        config.save();
    }

    public void setEstatePermissions(Set<Permission> permissions) {
        config.set("permissions.estate", ArrayUtil.toStringList(toList(permissions)));
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

    public void setMembers(Map<UUID, Set<Permission>> members) {
        List<UUID> uuids = Lists.newArrayList();
        uuids.addAll(members.keySet());
        config.set("members", ArrayUtil.toStringList(uuids));
        for (UUID uuid : uuids)
            config.set("members." + uuid.toString(), ArrayUtil.toStringList(toList(members.get(uuid))));
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

    private Set<Permission> toSet(List<Permission> list) {
        Set<Permission> set = Sets.newTreeSet();
        set.addAll(list);
        return set;
    }

    private List<Permission> toList(Set<Permission> treeSet) {
        List<Permission> list = Lists.newArrayList();
        list.addAll(treeSet);
        return list;
    }

}
