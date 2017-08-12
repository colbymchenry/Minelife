package com.minelife.realestate.server;

import com.minelife.CommonProxy;
import com.minelife.realestate.ModRealEstate;
import com.minelife.util.MLConfig;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class ServerProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        try {
            ModRealEstate.config = new MLConfig("real_estate");
            if (!ModRealEstate.config.getKeys(true).contains("price_per_block")) {
                ModRealEstate.config.addDefault("price_per_block", 2);
                ModRealEstate.config.save();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}