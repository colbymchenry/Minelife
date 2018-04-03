package com.minelife.economy.server;

import com.minelife.MLProxy;
import com.minelife.Minelife;
import lib.PatPeter.SQLibrary.Database;
import lib.PatPeter.SQLibrary.SQLite;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.util.logging.Logger;

public class ServerProxy extends MLProxy {

    public static Database DB;

    // TODO: Need to check bills if they are late or not in a ServerTick, also call the events
    @Override
    public void preInit(FMLPreInitializationEvent event) throws Exception {
        DB = new SQLite(Logger.getLogger("Minecraft"), "[Economy]", Minelife.getDirectory().getAbsolutePath(), "economy");
        DB.open();
        DB.query("CREATE TABLE IF NOT EXISTS atm (player VARCHAR(36), balance LONG)");
        DB.query("CREATE TABLE IF NOT EXISTS cashpiles (dimension INT, x INT, y INT, z INT)");
    }
}
