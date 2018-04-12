package com.minelife.jobs;

import com.minelife.MLMod;
import com.minelife.MLProxy;
import com.minelife.Minelife;
import com.minelife.jobs.server.CommandJob;
import com.minelife.jobs.server.ServerProxy;
import lib.PatPeter.SQLibrary.Database;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.registry.EntityRegistry;

public class ModJobs extends MLMod {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
//        EntityRegistry.registerModEntity(new ResourceLocation(Minelife.MOD_ID, "jobNPC"), EntityJobNPC.class, "jobNPC", 0, Minelife.MOD_ID, 50, 1, true);
    }

    @Override
    public Class<? extends MLProxy> getClientProxyClass() {
        return com.minelife.jobs.client.ClientProxy.class;
    }

    @Override
    public Class<? extends MLProxy> getServerProxyClass() {
        return com.minelife.jobs.server.ServerProxy.class;
    }

    @Override
    public void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandJob());
    }

    @Override
    public void entityRegistration(RegistryEvent.Register<EntityEntry> event) {
        event.getRegistry().register(
                EntityEntryBuilder.create()
                        .entity(EntityJobNPC.class)
                        .id(new ResourceLocation("minecraft", "job_npc"), 33)
                        .name("JobNPC")
                        .tracker(160, 2, false)
                        .build()
        );
    }

    public static Database getDatabase() {
        return ServerProxy.DB;
    }

}
