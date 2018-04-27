package com.minelife.tutorial;

import com.minelife.MLProxy;
import com.minelife.Minelife;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.io.File;

public class ServerProxy extends MLProxy {

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public void preInit(FMLPreInitializationEvent event) throws Exception {
        new File(Minelife.getDirectory(), "tutorials").mkdirs();
    }
}