package com.minelife.jobs.server;

import com.minelife.MLProxy;
import com.minelife.Minelife;
import com.minelife.jobs.EnumJob;
import com.minelife.jobs.job.farmer.FarmerListener;
import lib.PatPeter.SQLibrary.Database;
import lib.PatPeter.SQLibrary.SQLite;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.util.logging.Logger;

public class ServerProxy extends MLProxy {

    public static Database DB;

    @Override
    public void preInit(FMLPreInitializationEvent event) throws Exception {
        DB = new SQLite(Logger.getLogger("Minecraft"), "[Jobs]", Minelife.getDirectory().getAbsolutePath(), "jobs");
        DB.open();

        for (EnumJob enumJob : EnumJob.values()) {
            DB.query("CREATE TABLE IF NOT EXISTS " + enumJob.name().toLowerCase().replace("_", "") + " (playerID VARCHAR(36), xp LONG)");
            MinecraftForge.EVENT_BUS.register(enumJob.getListener());
            enumJob.getHandler().setupConfig();
        }
    }
}
