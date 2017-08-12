package com.minelife.realestate.server;

import com.minelife.CommonProxy;
import com.minelife.Minelife;
import com.minelife.realestate.ModRealEstate;
import com.minelife.util.SimpleConfig;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

import java.io.File;

public class ServerProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        try {
            ModRealEstate.config = new SimpleConfig(new File(Minelife.getConfigDirectory(), "real_estate_config.txt"));
            if (!ModRealEstate.config.getOptions().keySet().contains("price_per_block")) {
                ModRealEstate.config.addDefault("price_per_block", 2);
                ModRealEstate.config.setDefaults();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}