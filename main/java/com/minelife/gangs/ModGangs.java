package com.minelife.gangs;

import com.minelife.MLMod;
import com.minelife.capes.network.PacketCreateCape;
import com.minelife.capes.network.PacketCreateGui;
import com.minelife.gangs.server.CommandGang;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.relauncher.Side;

public class ModGangs extends MLMod {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
    }

    @Override
    public void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandGang());
    }
}
