package com.minelife.realestate.server;

import com.minelife.CommonProxy;
import com.minelife.Minelife;
import com.minelife.realestate.Estate;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

import java.sql.ResultSet;

public class ServerProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) throws Exception
    {
        Minelife.SQLITE.query("CREATE TABLE IF NOT EXISTS estates (region VARCHAR(36) NOT NULL, members TEXT NOT NULL, permissions TEXT NOT NULL, " +
                "rentPrice DOUBLE DEFAULT 0.0, purchasePrice DOUBLE DEFAULT 0.0, forRent BOOLEAN DEFAULT 0, rentPeriodInDays INT DEFAULT 0, " +
                "owner VARCHAR(36) NOT NULL, renter VARCHAR(36) NOT NULL, permsAllowedToChange TEXT NOT NULL)");

        ResultSet result = Minelife.SQLITE.query("SELECT region AS r FROM estates");
        while(result.next()) Estate.estates.add(new Estate(result));
    }
}
