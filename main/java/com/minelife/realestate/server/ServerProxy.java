package com.minelife.realestate.server;

import com.minelife.CommonProxy;
import com.minelife.Minelife;
import com.minelife.realestate.ModRealEstate;
import com.minelife.realestate.Region;
import com.minelife.util.MLConfig;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

import java.util.UUID;

public class ServerProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) throws Exception {
        ModRealEstate.config = new MLConfig("real_estate");
        ModRealEstate.config.addDefault("price_per_block", 2);
        ModRealEstate.config.save();
        Minelife.SQLITE.query("CREATE TABLE IF NOT EXISTS plots (uuid VARCHAR(36) NOT NULL DEFAULT '" + UUID.randomUUID().toString() + "', " +
                "world VARCHAR(60) NOT NULL, minX INT NOT NULL, minY INT NOT NULL, minZ INT NOT NULL, maxX INT NOT NULL, maxY INT NOT NULL, maxZ INT NOT NULL)");
        Region.initRegions();
    }

}