package com.minelife.jobs.server;

import com.minelife.MLProxy;
import com.minelife.Minelife;
import com.minelife.jobs.EntityJobNPC;
import com.minelife.jobs.ModJobs;
import com.minelife.jobs.RenderEntityJobNPC;
import com.minelife.jobs.job.farmer.FarmerListener;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import lib.PatPeter.SQLibrary.SQLite;
import net.minecraftforge.common.MinecraftForge;

public class ServerProxy extends MLProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) throws Exception {
        String prefix = "[Jobs]";
        String directory = Minelife.getConfigDirectory().getAbsolutePath();
        String dbName = "jobs";
        ModJobs.db = new SQLite(Minelife.getLogger(), prefix, directory, dbName);
        ModJobs.db.open();

        ModJobs.db.query("CREATE TABLE IF NOT EXISTS farmer (playerID VARCHAR(36), int xp)");

        MinecraftForge.EVENT_BUS.register(new FarmerListener());
    }
}
