package com.minelife.realestate;

import com.google.common.collect.Maps;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Vec3;
import scala.actors.threadpool.Arrays;

import java.util.List;
import java.util.Map;

public class EstateHandler {

    public static Estate createEstate(EntityPlayer player, Selection selection) throws Exception {
        for (Estate estate : ModRealEstate.getServerProxy().loadedEstates) {
            // check if the current estate in for loop intersects with any estates
            if (estate.intersects(selection)) throw new Exception("Intersects with an other estate.");
            // [INSIDE ESTATE]
            if (estate.contains(selection)) return createInsideEstate(player, selection, estate);

            // grab the selection for the current estate in for loop
            Selection estate_selection = new Selection();
            estate_selection.setWorld(estate.getWorld());
            estate_selection.setPos1((int) estate.getBounds().minX, (int) estate.getBounds().minY, (int) estate.getBounds().minZ);
            estate_selection.setPos2((int) estate.getBounds().maxX, (int) estate.getBounds().maxY, (int) estate.getBounds().maxZ);
            // [AROUND ESTATE]
            if (selection.contains(estate_selection)) return createAroundEstate(player, selection, estate);
        }

        // TODO: Create estate
    }


    // TODO: Finish this up
    private static Estate createInsideEstate(EntityPlayer player, Selection selection, Estate estate) throws Exception {
        Map<Double, Estate> parentEstates = Maps.newTreeMap();
        for (Estate e : estate.getContainingEstates()) {
            Vec3 min = Vec3.createVectorHelper(e.getBounds().minX, e.getBounds().minY, e.getBounds().minZ);
            parentEstates.put(min.distanceTo(Vec3.createVectorHelper(estate.getBounds().minX, estate.getBounds().minY, estate.getBounds().minZ)), e);
        }
        Estate parentEstate = parentEstates.get(0);

    }

    // TODO: Finish this up
    private static Estate createAroundEstate(EntityPlayer player, Selection selection, Estate estate) throws Exception {

    }

    public static List<Permission> getPlayerPermissions(EntityPlayer player, Estate estate) {
        Estate masterEstate = estate.getMasterEstate();

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
                if (e.getOwner() != null && e.getOwner().equals(player.getUniqueID()) && e.contains(estate)) {
                    return e.getOwnerPermissions();
                }
                // if player is renter
                else if (e.getRenter() != null && e.getRenter().equals(player.getUniqueID()) && e.contains(estate)) {
                    return e.getRenterPermissions();
                }
                // if player is member
                else if (e.getMembers().containsKey(player.getUniqueID()) && e.contains(estate)) {
                    return e.getMembers().get(player.getUniqueID());
                }
            }
        }

        return estate.getGlobalPermissions();
    }

}
