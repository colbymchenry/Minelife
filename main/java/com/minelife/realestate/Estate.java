package com.minelife.realestate;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.minelife.Minelife;
import com.minelife.util.MLConfig;
import com.minelife.util.configuration.InvalidConfigurationException;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.client.Minecraft;
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

    public Estate(int id) throws IOException, InvalidConfigurationException {
        this.id = id;
        this.config = new MLConfig(ModRealEstate.getServerProxy().estatesDir, String.valueOf(id));
    }

    public int getID() { return id; }

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
        return getRentPrice() != -1;
    }

    public int getRentPeriod() {
        return config.getInt("price.rent_period", 1);
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

    public UUID getOwner() {
        return config.getString("owner") != null ? UUID.fromString(config.getString("owner")) : null;
    }

    public UUID getRenter() {
        return config.getString("renter") != null ? UUID.fromString(config.getString("renter")) : null;
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
        if(Minelife.getSide() == Side.SERVER) {
            return Arrays.stream(MinecraftServer.getServer().worldServers)
                    .filter(w -> w.getWorldInfo().getWorldName().equals(config.getString("world"))).findFirst().orElse(null);
        } else {
            return Minecraft.getMinecraft().theWorld;
        }
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
        return contains(selection.world, min.xCoord, min.yCoord, min.zCoord) && contains(selection.world, max.xCoord, max.yCoord, max.zCoord);
    }

    public boolean intersects(Selection selection) {
        Vec3 min = selection.getMin(), max = selection.getMax();
        if(contains(selection)) return false;
        return getBounds().intersectsWith(AxisAlignedBB.getBoundingBox(min.xCoord, min.yCoord, min.zCoord, max.xCoord, max.yCoord, max.zCoord));
    }

    public Estate getParentEstate() {
        Map<Double, Estate> parentEstates = Maps.newTreeMap();
        for (Estate estate : EstateHandler.loadedEstates) {
            if(estate.contains(this)) {
                Vec3 min = Vec3.createVectorHelper(estate.getBounds().minX, estate.getBounds().minY, estate.getBounds().minZ);
                parentEstates.put(min.distanceTo(Vec3.createVectorHelper(getBounds().minX, getBounds().minY, getBounds().minZ)), estate);
            }
        }
        return parentEstates.get(0);
    }

    public List<Estate> getContainingEstates() {
        List<Estate> estates = Lists.newArrayList();
        for (Estate estate : EstateHandler.loadedEstates) if (contains(estate)) estates.add(estate);
        return estates;
    }

    public Estate getMasterEstate() {
        Estate estate = this;
        while(estate.getParentEstate() != null) estate = estate.getParentEstate();
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

    @Override
    public int compareTo(Estate o) {
        return o.id == id ? 0 : 1;
    }
}
