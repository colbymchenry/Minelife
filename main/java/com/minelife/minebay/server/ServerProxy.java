package com.minelife.minebay.server;

import com.minelife.CommonProxy;
import com.minelife.Minelife;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

import java.sql.SQLException;

public class ServerProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) throws SQLException
    {
        Minelife.SQLITE.query("CREATE TABLE IF NOT EXISTS item_listings (uuid VARCHAR(36), seller VARCHAR(36), price LONG, title TEXT, description TEXT, item_stack TEXT)");
    }
}
