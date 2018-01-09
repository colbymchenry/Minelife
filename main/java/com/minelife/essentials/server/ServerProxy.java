package com.minelife.essentials.server;

import com.minelife.MLProxy;
import com.minelife.Minelife;
import com.minelife.essentials.ModEssentials;
import com.minelife.essentials.TeleportHandler;
import com.minelife.util.MLConfig;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import lib.PatPeter.SQLibrary.SQLite;

import java.io.File;

public class ServerProxy extends MLProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) throws Exception {
        ModEssentials.config = new MLConfig("essentials");
        ModEssentials.config.addDefault("teleport_warmup", 5);
        ModEssentials.config.addDefault("teleport_cooldown", 10);
        ModEssentials.config.save();

        FMLCommonHandler.instance().bus().register(new TeleportHandler());

        String prefix = "[MinelifeEssentials]";
        String directory = Minelife.getConfigDirectory().getAbsolutePath() + File.separator + "essentials";
        String dbName = "storage";
        ModEssentials.db = new SQLite(Minelife.getLogger(), prefix, directory, dbName);
        ModEssentials.db.open();
    }
}
