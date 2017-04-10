package com.minelife.region.server;

import com.minelife.CommonProxy;
import com.minelife.Minelife;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

import java.sql.SQLException;
import java.util.UUID;

public class ServerProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        try {
            Minelife.SQLITE.query("CREATE TABLE IF NOT EXISTS regions (uuid VARCHAR(36) NOT NULL DEFAULT '" + UUID.randomUUID().toString() + "', " +
                    "world VARCHAR(60) NOT NULL, minX INT NOT NULL, minY INT NOT NULL, minZ INT NOT NULL, maxX INT NOT NULL, maxY INT NOT NULL, maxZ INT NOT NULL)");

            Minelife.SQLITE.query("CREATE TABLE IF NOT EXISTS subregions (uuid VARCHAR(36) NOT NULL DEFAULT '" + UUID.randomUUID().toString() + "', " +
                    "parentregionuuid VARCHAR(60) NOT NULL, minX INT NOT NULL, minY INT NOT NULL, minZ INT NOT NULL, maxX INT NOT NULL, maxY INT NOT NULL, maxZ INT NOT NULL)");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
