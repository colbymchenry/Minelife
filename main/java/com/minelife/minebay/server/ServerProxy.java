package com.minelife.minebay.server;

import com.minelife.MLProxy;
import com.minelife.Minelife;
import lib.PatPeter.SQLibrary.Database;
import lib.PatPeter.SQLibrary.SQLite;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.util.logging.Logger;

public class ServerProxy extends MLProxy {

    public static Database DB;

    @Override
    public void preInit(FMLPreInitializationEvent event) throws Exception {
        super.preInit(event);
        DB = new SQLite(Logger.getLogger("Minecraft"), "[Minebay]", Minelife.getDirectory().getAbsolutePath(), "minebay");
        DB.open();
        DB.query("CREATE TABLE IF NOT EXISTS itemListings (uuid VARCHAR(36), seller VARCHAR(36), price INT, title TEXT, description TEXT, item TEXT, storage INT, datePublished VARCHAR(36))");
    }
}
