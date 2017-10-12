package com.minelife.realestate;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.minelife.util.MLConfig;
import com.minelife.util.configuration.InvalidConfigurationException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class EstateHandler {

    public static Set<Estate> loadedEstates = new TreeSet<>();

    public static Estate createEstate(EntityPlayer player, Selection selection) throws Exception {
        if(!canCreateEstate(player, selection)) return null;

        // if they are allowed to create estates inside all of the estates; create the estate
        int id = getMaxEstateID() + 1;
        MLConfig estate_config = new MLConfig(ModRealEstate.getServerProxy().estatesDir, String.valueOf(id));
        estate_config.addDefault("owner", player.getUniqueID().toString());
        estate_config.addDefault("renter", "");
        estate_config.addDefault("members", new String[]{});
        estate_config.addDefault("permissions.global", new String[]{});
        estate_config.addDefault("permissions.renter", new String[]{});
        estate_config.addDefault("permissions.owner", new String[]{});
        estate_config.addDefault("permissions.allowedToChange", new String[]{});
        estate_config.addDefault("permissions.estate", new String[]{});
        estate_config.addDefault("world", selection.getWorld().getWorldInfo().getWorldName());
        estate_config.addDefault("pos1.x", selection.getMin().xCoord);
        estate_config.addDefault("pos1.y", selection.getMin().yCoord);
        estate_config.addDefault("pos1.z", selection.getMin().zCoord);
        estate_config.addDefault("pos2.x", selection.getMax().xCoord);
        estate_config.addDefault("pos2.y", selection.getMax().yCoord);
        estate_config.addDefault("pos2.z", selection.getMax().zCoord);
        estate_config.save();
        Estate createdEstate = new Estate(id);
        loadedEstates.add(createdEstate);
        return createdEstate;
    }

    public static boolean canCreateEstate(EntityPlayer player, Selection selection) throws Exception {
        List<Estate> childEstates = Lists.newArrayList();
        TreeMap<Double, Estate> parentEstates = Maps.newTreeMap();

        if(!selection.isComplete()) throw new Exception("Please make a full selection.");

        for (Estate estate : loadedEstates) {
            // check if the current estate in for loop intersects with any estates
            if (estate.intersects(selection)) throw new Exception("Intersects with an other estate.");

            // [INSIDE ESTATE]
            if (estate.contains(selection)) {
                Vec3 min = Vec3.createVectorHelper(estate.getBounds().minX, estate.getBounds().minY, estate.getBounds().minZ);
                parentEstates.put(min.distanceTo(Vec3.createVectorHelper(selection.getMin().xCoord, selection.getMin().yCoord, selection.getMin().zCoord)), estate);
            }


            // grab the selection for the current estate in for loop
            Selection estate_selection = new Selection();
            estate_selection.setWorld(estate.getWorld());
            estate_selection.setPos1((int) estate.getBounds().minX, (int) estate.getBounds().minY, (int) estate.getBounds().minZ);
            estate_selection.setPos2((int) estate.getBounds().maxX, (int) estate.getBounds().maxY, (int) estate.getBounds().maxZ);

            // [AROUND ESTATE]
            if (selection.contains(estate_selection)) childEstates.add(estate);
        }

        // add parent estate
        if (!parentEstates.isEmpty()) childEstates.add(parentEstates.firstEntry().getValue());
        // use estates inside selection
        for (Estate estate : childEstates.toArray(new Estate[childEstates.size()]))
            if (!estate.getPlayerPermissions(player).contains(Permission.ESTATE_CREATION))
                throw new Exception("You cannot create an estate around that estate.");

        return true;
    }

    public static boolean canDeleteEstate(EntityPlayer player, Estate estate) {
        return true;
    }

    public static Estate getEstateAt(World world, Vec3 vec3)
    {
        TreeMap<Double, Estate> parentEstates = Maps.newTreeMap();
        for (Estate estate : loadedEstates) {
            if(estate.getWorld().getWorldInfo().getWorldName().equals(world.getWorldInfo().getWorldName())) {
                if(estate.contains(world, vec3.xCoord, vec3.yCoord, vec3.zCoord)) {
                    Vec3 min = Vec3.createVectorHelper(estate.getBounds().minX, estate.getBounds().minY, estate.getBounds().minZ);
                    parentEstates.put(min.distanceTo(vec3), estate);
                }
            }
        }
        return parentEstates.isEmpty() ? null : parentEstates.firstEntry().getValue();
    }

    public static int getMaxEstateID() {
        int maxID = 0;
        for (Estate loadedEstate : loadedEstates)
            maxID = loadedEstate.getID() > maxID ? loadedEstate.getID() : maxID;
        return maxID;
    }

    public static void reloadEstates() {
        loadedEstates.clear();
        for (File file : ModRealEstate.getServerProxy().estatesDir.listFiles()) {
            try {
                loadedEstates.add(new Estate(Integer.parseInt(file.getName().replaceAll(".yml", ""))));
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InvalidConfigurationException e) {
                e.printStackTrace();
            }
        }
    }

    public static Estate getEstate(int id) {
        return loadedEstates.stream().filter(e -> e.getID() == id).findFirst().orElse(null);
    }

}
