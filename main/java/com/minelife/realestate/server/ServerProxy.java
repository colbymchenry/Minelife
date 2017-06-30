package com.minelife.realestate.server;

import com.minelife.CommonProxy;
import com.minelife.Minelife;
import com.minelife.realestate.Estate;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

import java.sql.SQLException;
import java.util.logging.Level;

public class ServerProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event)
    {
        try
        {
            Minelife.SQLITE.query("CREATE TABLE IF NOT EXISTS RealEstate_Estates (uuid VARCHAR(36) NOT NULL, region VARCHAR(36) NOT NULL);");
            Estate.initEstates();
        } catch (SQLException e)
        {
            e.printStackTrace();
            Minelife.getLogger().log(Level.SEVERE, "", e);
        }
    }
}
