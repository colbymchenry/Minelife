package com.minelife.minereset;

import com.google.common.collect.Lists;
import com.minelife.MLMod;
import com.minelife.MLProxy;
import com.minelife.util.MLConfig;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

import java.util.List;

public class ModMineReset extends MLMod {

    public static List<Mine> MINES = Lists.newArrayList();

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);
    }

    @Override
    public void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandMineReset());
    }

    @Override
    public Class<? extends MLProxy> getServerProxyClass() {
        return com.minelife.minereset.ServerProxy.class;
    }

    public static MLConfig getConfig() {
        return ServerProxy.config;
    }

}
