package com.minelife.realestate.server;

import com.minelife.CommonProxy;
import com.minelife.Minelife;
import com.minelife.realestate.Estate;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.common.MinecraftForge;

import java.sql.ResultSet;

public class ServerProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) throws Exception
    {
        Minelife.SQLITE.query("CREATE TABLE IF NOT EXISTS estates (region VARCHAR(36) NOT NULL, members TEXT NOT NULL DEFAULT '', permissions TEXT NOT NULL DEFAULT '', " +
                "rentPrice DOUBLE DEFAULT 0.0, purchasePrice DOUBLE DEFAULT 0.0, forRent BOOLEAN DEFAULT 0, rentPeriodInDays INT DEFAULT 0, " +
                "owner VARCHAR(36) NOT NULL, renter VARCHAR(36) NOT NULL DEFAULT '', permsAllowedToChange TEXT NOT NULL DEFAULT '')");

        ResultSet result = Minelife.SQLITE.query("SELECT * FROM estates");
        while(result.next()) Estate.estates.add(new Estate(result));

        MinecraftForge.EVENT_BUS.register(new PlayerListener());
    }
}
