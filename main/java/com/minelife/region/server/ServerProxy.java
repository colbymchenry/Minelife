package com.minelife.region.server;

import com.minelife.CommonProxy;
import com.minelife.Minelife;
import com.minelife.region.client.WorldEditSelectionController;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.common.MinecraftForge;

import java.util.UUID;
import java.util.logging.Level;

public class ServerProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new WorldEditSelectionController.ServerSelector());

        try {
            Minelife.SQLITE.query("CREATE TABLE IF NOT EXISTS regions (uuid VARCHAR(36) NOT NULL DEFAULT '" + UUID.randomUUID().toString() + "', " +
                    "world VARCHAR(60) NOT NULL, minX INT NOT NULL, minY INT NOT NULL, minZ INT NOT NULL, maxX INT NOT NULL, maxY INT NOT NULL, maxZ INT NOT NULL)");

//            Minelife.SQLITE.query("CREATE TABLE IF NOT EXISTS subregions (regionUniqueID VARCHAR(36) NOT NULL DEFAULT '" + UUID.randomUUID().toString() + "', " +
//                    "parentregionuuid VARCHAR(60) NOT NULL, world VARCHAR(60) NOT NULL, minX INT NOT NULL, minY INT NOT NULL, minZ INT NOT NULL, maxX INT NOT NULL, maxY INT NOT NULL, maxZ INT NOT NULL)");

            Region.initRegions();
        } catch (Exception e) {
            e.printStackTrace();
            Minelife.getLogger().log(Level.SEVERE, "", e);
        }
    }
}
