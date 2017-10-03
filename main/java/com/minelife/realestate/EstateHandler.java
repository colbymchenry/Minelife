package com.minelife.realestate;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.minelife.util.MLConfig;
import com.minelife.util.configuration.InvalidConfigurationException;
import cpw.mods.fml.common.Mod;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Vec3;
import scala.actors.threadpool.Arrays;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class EstateHandler {

    public static Set<Estate> loadedEstates = new TreeSet<>();

    public static Estate createEstate(EntityPlayer player, Selection selection) throws Exception {
        List<Estate> childEstates = Lists.newArrayList();
        Map<Double, Estate> parentEstates = Maps.newTreeMap();

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
        if (!parentEstates.isEmpty()) childEstates.add(parentEstates.get(0));
        // use estates inside selection
        return createAroundEstates(player, selection,
                childEstates.toArray(new Estate[childEstates.size()]));
    }


    // TODO: May still need this, not sure
//    private static Estate createInsideEstate(EntityPlayer player, Selection selection, Estate estate) throws Exception {
//        Map<Double, Estate> parentEstates = Maps.newTreeMap();
//        for (Estate e : estate.getContainingEstates()) {
//            Vec3 min = Vec3.createVectorHelper(e.getBounds().minX, e.getBounds().minY, e.getBounds().minZ);
//            parentEstates.put(min.distanceTo(Vec3.createVectorHelper(estate.getBounds().minX, estate.getBounds().minY, estate.getBounds().minZ)), e);
//        }
//
//        Estate parentEstate = parentEstates.isEmpty() ? estate : parentEstates.get(0);
//        if(!getPlayerPermissions(player, estate).contains(Permission.ESTATE_CREATION))
//            throw new Exception("You do not have permission to create an estate here.");
//
//
//    }

    private static Estate createAroundEstates(EntityPlayer player, Selection selection, Estate[] estates) throws Exception {
        // check if they are not allowed to create an estate an ANY of the estates
        for (Estate estate : estates)
            if (!estate.getPlayerPermissions(player).contains(Permission.ESTATE_CREATION))
                throw new Exception("You cannot create an estate around that estate.");


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
        estate_config.addDefault("world", selection.world.getWorldInfo().getWorldName());
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

}
