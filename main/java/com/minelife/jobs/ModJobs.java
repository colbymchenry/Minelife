package com.minelife.jobs;

import buildcraft.core.lib.network.PacketUpdate;
import com.minelife.MLMod;
import com.minelife.MLProxy;
import com.minelife.Minelife;
import com.minelife.jobs.server.CommandJob;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import lib.PatPeter.SQLibrary.Database;

public class ModJobs extends MLMod {

    public static Database db;

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        EntityRegistry.registerModEntity(EntityJobNPC.class, "jobNPC", 250, Minelife.instance, 50, 1, true);
        registerPacket(PacketUpdateNPC.Handler.class, PacketUpdateNPC.class, Side.CLIENT);
    }

    @Override
    public void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandJob());
    }

    @Override
    public Class<? extends MLProxy> getServerProxyClass() {
        return com.minelife.jobs.server.ServerProxy.class;
    }

    @Override
    public Class<? extends MLProxy> getClientProxyClass() {
        return com.minelife.jobs.client.ClientProxy.class;
    }
}
