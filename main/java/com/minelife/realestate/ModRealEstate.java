package com.minelife.realestate;

import com.minelife.SubMod;
import com.minelife.realestate.client.SelectionController;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;

public class ModRealEstate extends SubMod {

    @Override
    public void preInit(FMLPreInitializationEvent event)
    {
        GameRegistry.registerItem(SelectionController.Selector.getInstance(), "Selector");
    }
}
