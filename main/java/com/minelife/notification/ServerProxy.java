package com.minelife.notification;

import com.minelife.CommonProxy;
import com.minelife.Minelife;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

import java.sql.SQLException;

public class ServerProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event)
    {
        try {
            Minelife.SQLITE.query("CREATE TABLE IF NOT EXISTS notifications (uuid VARCHAR(36) NOT NULL, player VARCHAR(36) NOT NULL, clazz TEXT, tagCompound TEXT NOT NULL)");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        FMLCommonHandler.instance().bus().register(new ServerJoinListener());
    }
}
