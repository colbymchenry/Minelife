package com.minelife.realestate.server;

import com.minelife.CommonProxy;
import com.minelife.Minelife;
import com.minelife.realestate.ModRealEstate;
import com.minelife.util.MLConfig;
import com.minelife.util.configuration.InvalidConfigurationException;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

import java.io.IOException;
import java.sql.SQLException;
import java.util.UUID;

public class ServerProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) throws SQLException, IOException, InvalidConfigurationException {
        ModRealEstate.config = new MLConfig("real_estate");
        ModRealEstate.config.addDefault("price_per_block", 2);
        ModRealEstate.config.save();
        Minelife.SQLITE.query("CREATE TABLE IF NOT EXISTS ESTATES (UUID VARCHAR(36) NOT NULL DEFAULT '" + UUID.randomUUID().toString() + "', " +
                "NAME VARCHAR(100) NOT NULL, OWNER_UUID VARCHAR(36) NOT NULL, REGION_UUIDs VARCHAR(1000000) NOT NULL)");
        Estate.initEstates();
    }
}