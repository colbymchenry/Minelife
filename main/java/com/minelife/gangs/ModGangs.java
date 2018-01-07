package com.minelife.gangs;

import com.minelife.MLMod;
import com.minelife.gangs.network.PacketOpenModifySymbolGui;
import com.minelife.gangs.server.CommandGang;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.relauncher.Side;

public class ModGangs extends MLMod {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        registerPacket(PacketOpenModifySymbolGui.Handler.class, PacketOpenModifySymbolGui.class, Side.CLIENT);
    }

    @Override
    public void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandGang());
    }
}
