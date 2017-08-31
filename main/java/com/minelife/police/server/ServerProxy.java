package com.minelife.police.server;

import com.minelife.CommonProxy;
import com.minelife.Minelife;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class ServerProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) throws Exception {
        Minelife.SQLITE.query("CREATE TABLE IF NOT EXISTS policeofficers (playerUUID TEXT, xp INT)");
    }
}
