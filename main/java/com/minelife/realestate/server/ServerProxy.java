package com.minelife.realestate.server;

import com.minelife.CommonProxy;
import com.minelife.Minelife;
import com.minelife.realestate.ModRealEstate;
import com.minelife.util.MLConfig;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

import java.util.UUID;

public class ServerProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        try {
            ModRealEstate.config = new MLConfig("real_estate");
            ModRealEstate.config.addDefault("price_per_block", 2);
            ModRealEstate.config.save();
            Minelife.SQLITE.query("CREATE TABLE IF NOT EXISTS estates (uuid VARCHAR(36) NOT NULL DEFAULT '" + UUID.randomUUID().toString() + "', " +
                    "owner_uuid VARCHAR(36) NOT NULL, region_uuids VARCHAR(1000000) NOT NULL)");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}