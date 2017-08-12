package com.minelife.realestate.server;

import com.minelife.CommonProxy;
import com.minelife.realestate.ModRealEstate;
import com.minelife.util.MLConfig;
import com.minelife.util.configuration.InvalidConfigurationException;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

import java.io.IOException;

public class ServerProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) throws IOException, InvalidConfigurationException
    {
        ModRealEstate.config = new MLConfig("real_estate");
        ModRealEstate.config.addDefault("price_per_block", 2);
        ModRealEstate.config.save();
    }
}