package com.minelife.realestate;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.minelife.util.PlayerHelper;
import com.minelife.util.StringHelper;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.server.FMLServerHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class Estate implements Comparable<Estate> {

    private UUID uniqueID;
    private NBTTagCompound tagCompound;

    public Estate(UUID uniqueID, NBTTagCompound tagCompound) {
        this.uniqueID = uniqueID;
        this.tagCompound = tagCompound;
    }

    public NBTTagCompound getTagCompound() {
        return tagCompound;
    }

    public World getWorld() {
        return FMLServerHandler.instance().getServer().getWorld(this.tagCompound.getInteger("Dimension"));
    }

    public UUID getUniqueID() {
        return this.uniqueID;
    }

    public UUID getOwnerID() {
        return this.tagCompound.hasKey("Owner") ? UUID.fromString(this.tagCompound.getString("Owner")) : null;
    }

    public UUID getRenterID() {
        return this.tagCompound.hasKey("Renter") ? UUID.fromString(this.tagCompound.getString("Renter")) : null;
    }

    public void setRenterID(UUID uniqueID) {
        if (uniqueID == null)
            this.tagCompound.removeTag("Renter");
        else
            this.tagCompound.setString("Renter", uniqueID.toString());
    }

    public void setOwnerID(UUID uniqueID) {
        this.tagCompound.setString("Owner", uniqueID.toString());
    }

    public void setIdentifier(String identifier) { this.tagCompound.setString("Identifier", identifier); }

    public String getIdentifier() {
        if(!this.tagCompound.hasKey("Identifier")) return "No Identifier";
        return this.tagCompound.getString("Identifier");
    }

    public BlockPos getMaximum() {
        int[] max = this.tagCompound.getIntArray("Max");
        return max.length < 3 ? null : new BlockPos(max[0], max[1], max[2]);
    }

    public BlockPos getMinimum() {
        int[] min = this.tagCompound.getIntArray("Min");
        return min.length < 3 ? null : new BlockPos(min[0], min[1], min[2]);
    }

    public void setMaximum(BlockPos pos) {
        this.tagCompound.setIntArray("Max", new int[]{pos.getX(), pos.getY(), pos.getZ()});
    }

    public void setMinimum(BlockPos pos) {
        this.tagCompound.setIntArray("Min", new int[]{pos.getX(), pos.getY(), pos.getZ()});
    }

    public void setWorld(World world) {
        this.tagCompound.setInteger("Dimension", world.provider.getDimension());
    }

    public int getPurchasePrice() {
        return tagCompound.hasKey("PurchasePrice") ? tagCompound.getInteger("PurchasePrice") : 0;
    }

    public void setPurchasePrice(int price) {
        if (price < 1)
            tagCompound.removeTag("PurchasePrice");
        else
            tagCompound.setInteger("PurchasePrice", price);
    }

    public int getRentPrice() {
        return tagCompound.hasKey("RentPrice") ? tagCompound.getInteger("RentPrice") : 0;
    }

    public void setRentPrice(int price) {
        if (price < 1)
            tagCompound.removeTag("RentPrice");
        else
            tagCompound.setInteger("RentPrice", price);
    }

    public int getRentPeriod() {
        return tagCompound.hasKey("RentPeriod") ? tagCompound.getInteger("RentPeriod") : 0;
    }

    public void setRentPeriod(int mcDays) {
        if (mcDays < 1)
            tagCompound.removeTag("RentPeriod");
        else
            tagCompound.setInteger("RentPeriod", mcDays);
    }

    public Set<UUID> getMemberIDs() {
        Set<UUID> members = Sets.newTreeSet();
        if (this.tagCompound.hasKey("Members")) {
            for (String uniqueID : this.tagCompound.getString("Members").split(",")) {
                if (!uniqueID.isEmpty()) members.add(UUID.fromString(uniqueID));
            }
        }
        return members;
    }

    public void setMemberIDs(Set<UUID> members) {
        StringBuilder builder = new StringBuilder();
        for (UUID member : members) builder.append(member.toString()).append(",");
        this.tagCompound.setString("Members", builder.toString());
    }

    public Map<UUID, Set<PlayerPermission>> getMembersWithPermissions() {
        Map<UUID, Set<PlayerPermission>> members = Maps.newHashMap();
        for (UUID uuid : getMemberIDs()) members.put(uuid, getMemberPermissions(uuid));
        return members;
    }

    public Set<EstateProperty> getProperties() {
        Set<EstateProperty> properties = Sets.newTreeSet();
        if (this.tagCompound.hasKey("Properties")) {
            for (String s : this.tagCompound.getString("Properties").split(",")) {
                if (!s.isEmpty()) properties.add(EstateProperty.valueOf(s));
            }
        }
        return properties;
    }

    public void setProperties(Set<EstateProperty> properties) {
        StringBuilder builder = new StringBuilder();
        for (EstateProperty property : properties) builder.append(property.name()).append(",");
        this.tagCompound.setString("Properties", builder.toString());
    }

    public Set<PlayerPermission> getRenterPermissions() {
        Set<PlayerPermission> permissions = Sets.newTreeSet();
        if (this.tagCompound.hasKey("RenterPermissions")) {
            for (String permission : this.tagCompound.getString("RenterPermissions").split(","))
                if (!permission.isEmpty())
                    permissions.add(PlayerPermission.valueOf(permission));
        }
        return permissions;
    }

    public void setRenterPermissions(Set<PlayerPermission> permissions) {
        StringBuilder builder = new StringBuilder();
        for (PlayerPermission permission : permissions) builder.append(permission.name()).append(",");
        this.tagCompound.setString("RenterPermissions", builder.toString());
    }

    public Set<PlayerPermission> getGlobalPermissions() {
        Set<PlayerPermission> permissions = Sets.newTreeSet();
        if (this.tagCompound.hasKey("GlobalPermissions")) {
            for (String permission : this.tagCompound.getString("GlobalPermissions").split(","))
                if (!permission.isEmpty())
                    permissions.add(PlayerPermission.valueOf(permission));
        }
        return permissions;
    }

    public void setGlobalPermissions(Set<PlayerPermission> permissions) {
        StringBuilder builder = new StringBuilder();
        for (PlayerPermission permission : permissions) builder.append(permission.name()).append(",");
        this.tagCompound.setString("GlobalPermissions", builder.toString());
    }

    public Set<PlayerPermission> getMemberPermissions(UUID memberID) {
        Set<PlayerPermission> permissions = Sets.newTreeSet();
        if (this.tagCompound.hasKey(memberID.toString())) {
            for (String permission : ((NBTTagCompound) this.tagCompound.getTag(memberID.toString())).getString("Permissions").split(",")) {
                if (!permission.isEmpty()) permissions.add(PlayerPermission.valueOf(permission));
            }
        }
        return permissions;
    }

    public void setMemberPermissions(UUID memberID, Set<PlayerPermission> permissions) {
        StringBuilder builder = new StringBuilder();
        for (PlayerPermission permission : permissions) builder.append(permission.name()).append(",");
        NBTTagCompound tagCompound = new NBTTagCompound();
        tagCompound.setString("Permissions", builder.toString());
        this.tagCompound.setTag(memberID.toString(), tagCompound);
    }

    public Map<UUID, Set<PlayerPermission>> getSurroundingMembers() {
        Map<UUID, Set<PlayerPermission>> members = Maps.newHashMap();
        Estate e = getParentEstate();
        while (e != null) {
            members.putAll(e.getMembersWithPermissions());
            e = e.getParentEstate();
        }

        members.putAll(getMembersWithPermissions());
        return members;
    }

    public Set<UUID> getSurroundingOwners() {
        Set<UUID> owners = Sets.newTreeSet();
        Estate e = getParentEstate();
        while (e != null) {
            owners.add(e.getOwnerID());
            e = e.getParentEstate();
        }
        owners.add(getOwnerID());
        return owners;
    }

    public String getIntro() {
        return this.tagCompound.hasKey("Intro") ? this.tagCompound.getString("Intro") : null;
    }

    public String getOutro() {
        return this.tagCompound.hasKey("Outro") ? this.tagCompound.getString("Outro") : null;
    }

    public void setIntro(String msg) {
        if (msg == null) return;
        this.tagCompound.setString("Intro", msg);
    }

    public void setOutro(String msg) {
        if (msg == null) return;
        this.tagCompound.setString("Outro", msg);
    }

    public void save() throws SQLException {
        ResultSet result = ModRealEstate.getDatabase().query("SELECT * FROM estates WHERE uuid='" + this.getUniqueID().toString() + "'");
        if (result.next())
            ModRealEstate.getDatabase().query("UPDATE estates SET tagCompound='" + this.tagCompound.toString().replace("'", "''") + "' WHERE uuid='" + getUniqueID().toString() + "'");
        else
            ModRealEstate.getDatabase().query("INSERT INTO estates (uuid, tagCompound) VALUES ('" + this.getUniqueID().toString() + "', '" + this.tagCompound.toString().replace("'", "''") + "')");
    }

    public void delete() throws SQLException {
        ModRealEstate.getDatabase().query("DELETE FROM estates WHERE uuid='" + this.getUniqueID().toString() + "'");
        ModRealEstate.getLoadedEstates().remove(this);
    }

    @Override
    public int compareTo(Estate o) {
        return o.getUniqueID().compareTo(this.getUniqueID());
    }


    /*
     * COMPLICATED METHODS
     */
    public boolean intersects(BlockPos min, BlockPos max) {
        return this.getMinimum() != null && this.getMaximum() != null && !this.contains(min) && !this.contains(max) &&
                new AxisAlignedBB(this.getMinimum().getX(), this.getMinimum().getY(), this.getMinimum().getZ(),
                        this.getMaximum().getX(), this.getMaximum().getY(), this.getMaximum().getZ()).
                        intersects(new AxisAlignedBB(min.getX(), min.getY(), min.getZ(), max.getX(), max.getY(), max.getZ()));
    }

    public boolean isInside(BlockPos min, BlockPos max) {
        return this.getMinimum() != null && this.getMaximum() != null &&
                this.getMinimum().getX() > min.getX() && this.getMinimum().getY() > min.getY() && this.getMinimum().getZ() > min.getZ() &&
                this.getMaximum().getX() < max.getX() && this.getMaximum().getY() < max.getY() && this.getMaximum().getZ() < max.getZ();
    }

    public boolean contains(Estate estate) {
        BlockPos min = this.getMinimum();
        BlockPos max = this.getMaximum();
        BlockPos min1 = estate.getMinimum();
        BlockPos max1 = estate.getMaximum();
        return this.getMinimum() != null && this.getMaximum() != null && estate.getWorld().equals(getWorld()) &&
                min1.getX() > min.getX() && min1.getY() > min.getY() && min1.getZ() > min.getZ() &&
                max1.getX() < max.getX() && max1.getY() < max.getY() && max1.getZ() < max.getZ();
    }

    public boolean contains(BlockPos pos) {
        BlockPos min = this.getMinimum();
        BlockPos max = this.getMaximum();
        return min != null && max != null && (min.getX() <= pos.getX() && min.getY() <= pos.getY() && min.getZ() <= pos.getZ() &&
                max.getX() >= pos.getX() && max.getY() >= pos.getY() && max.getZ() >= pos.getZ());
    }

    public Estate getParentEstate() {
        Estate closest = null;
        if (this.getMinimum() == null || this.getMaximum() == null) return closest;

        for (Estate estate : ModRealEstate.getLoadedEstates()) {
            if (estate.contains(this) && estate.getWorld().equals(getWorld())) {
                if (closest == null) closest = estate;
                else {
                    int distMinX = this.getMinimum().getX() - estate.getMinimum().getX();
                    int distMinX1 = this.getMinimum().getX() - closest.getMinimum().getX();
                    if (distMinX < distMinX1) closest = estate;
                }
            }
        }
        return closest;
    }

    public Estate getMasterEstate() {
        Estate master = this;
        while (master != null) master = master.getParentEstate();
        return master == this ? null : master;
    }

    public Set<Estate> getContainingEstates() {
        Set<Estate> estates = Sets.newTreeSet();
        ModRealEstate.getLoadedEstates().forEach(estate -> {
            if (this.contains(estate)) estates.add(estate);
        });
        return estates;
    }

    // TODO: this needs a lot more testing
    public Set<PlayerPermission> getPlayerPermissions(UUID playerID) {
        Set<PlayerPermission> permissions = Sets.newTreeSet();
        permissions.addAll(getActualGlobalPerms());

        if (getParentEstate() == null && Objects.equals(playerID, getOwnerID())) {
            permissions.addAll(toSet(Arrays.asList(PlayerPermission.values())));
            return permissions;
        }

        if (getMasterEstate() != null && Objects.equals(playerID, getMasterEstate().getOwnerID())) {
            permissions.addAll(toSet(Arrays.asList(PlayerPermission.values())));
            return permissions;
        }

        if (PlayerHelper.isOp(PlayerHelper.getPlayer(playerID))) {
            permissions.addAll(toSet(Arrays.asList(PlayerPermission.values())));
            return permissions;
        }

        if (Objects.equals(playerID, getOwnerID())) {
            permissions.addAll(Arrays.asList(PlayerPermission.values()));
        } else if (Objects.equals(playerID, getRenterID())) {
            permissions.addAll(getActualRenterPerms());
        } else if (getMembersWithPermissions().containsKey(playerID)) {
            permissions.addAll(getActualMemberPerms(playerID));
        }

        return permissions;
    }

    public Set<PlayerPermission> getActualGlobalPerms() {
        Set<PlayerPermission> globalPermissions = getGlobalPermissions();
        Set<PlayerPermission> toRemove = Sets.newTreeSet();
        Estate parentEstate = getParentEstate();
        while (parentEstate != null) {
            for (PlayerPermission p : globalPermissions)
                if (!parentEstate.getGlobalPermissions().contains(p) && !parentEstate.getPlayerPermissions(parentEstate.getOwnerID()).contains(p))
                    toRemove.add(p);
            parentEstate = parentEstate.getParentEstate();
        }

        globalPermissions.removeAll(toRemove);
        return globalPermissions;
    }

    public Set<PlayerPermission> getActualRenterPerms() {
        Set<PlayerPermission> renterPerms = getRenterPermissions();
        Set<PlayerPermission> toRemove = Sets.newTreeSet();

        Estate parentEstate = getParentEstate();

        while (parentEstate != null) {

            for (PlayerPermission p : renterPerms)
                if (!parentEstate.getRenterPermissions().contains(p) && !parentEstate.getPlayerPermissions(parentEstate.getOwnerID()).contains(p))
                    toRemove.add(p);

            parentEstate = parentEstate.getParentEstate();
        }

        renterPerms.removeAll(toRemove);
        return renterPerms;
    }

    // 0TODO: Will still need some testings and fine tuning
    public Set<PlayerPermission> getActualMemberPerms(UUID playerID) {
        Set<PlayerPermission> memberPerms = getMembersWithPermissions().get(playerID);
        Set<PlayerPermission> toRemove = Sets.newTreeSet();

        Estate parentEstate = getParentEstate();

        while (parentEstate != null) {

            for (PlayerPermission p : memberPerms)
                if (parentEstate.getMembersWithPermissions().containsKey(playerID) &&
                        !parentEstate.getMembersWithPermissions().get(playerID).contains(p))
                    toRemove.add(p);

            parentEstate = parentEstate.getParentEstate();
        }

        memberPerms.removeAll(toRemove);
        return memberPerms;
    }

    private Set<PlayerPermission> toSet(List<PlayerPermission> list) {
        Set<PlayerPermission> set = Sets.newTreeSet();
        set.addAll(list);
        return set;
    }

    private List<PlayerPermission> toList(Set<PlayerPermission> treeSet) {
        List<PlayerPermission> list = Lists.newArrayList();
        list.addAll(treeSet);
        return list;
    }

}
