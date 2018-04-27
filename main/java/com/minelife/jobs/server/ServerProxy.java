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
    public void preInit(FMLPreInitializationEvent event) {
        try {
            DB = new SQLite(Logger.getLogger("Minecraft"), "[Jobs]", Minelife.getDirectory().getAbsolutePath(), "jobs");
            DB.open();

            DB.query("CREATE TABLE IF NOT EXISTS bounties (placer VARCHAR(36), target VARCHAR(36), amount INT)");

            for (EnumJob enumJob : EnumJob.values()) {
                DB.query("CREATE TABLE IF NOT EXISTS " + enumJob.name().toLowerCase().replace("_", "") + " (playerID VARCHAR(36), xp LONG)");
                if (enumJob.getListener() != null)
                    MinecraftForge.EVENT_BUS.register(enumJob.getListener());
                if (enumJob.getHandler() != null)
                    enumJob.getHandler().setupConfig();
            }

            MinecraftForge.TERRAIN_GEN_BUS.register(EnumJob.LUMBERJACK.getListener());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
