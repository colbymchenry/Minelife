package com.minelife.gangs.server;

import com.minelife.MLProxy;
import com.minelife.Minelife;
import com.minelife.gangs.Gang;
import lib.PatPeter.SQLibrary.Database;
import lib.PatPeter.SQLibrary.SQLite;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.util.logging.Logger;

public class ServerProxy extends MLProxy {

    public static Database DB;

    @Override
    public void preInit(FMLPreInitializationEvent event) throws Exception {
        DB = new SQLite(Logger.getLogger("Minecraft"), "[Gangs]", Minelife.getDirectory().getAbsolutePath(), "gangs");
        DB.open();
        DB.query("CREATE TABLE IF NOT EXISTS gangs (uuid VARCHAR(36), nbt TEXT)");
        Gang.populateGangs();
    }

}
