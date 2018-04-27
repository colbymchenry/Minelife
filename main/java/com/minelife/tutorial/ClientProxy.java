package com.minelife.tutorial;

import com.minelife.MLProxy;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends MLProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) throws Exception {
        ModTutorial.itemTutorialBook.registerModel();
    }
}
