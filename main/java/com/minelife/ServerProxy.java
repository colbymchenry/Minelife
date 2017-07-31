package com.minelife;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import lib.PatPeter.SQLibrary.SQLite;
import net.minecraft.init.Blocks;

import java.util.logging.Logger;

public class ServerProxy extends CommonProxy {


    @Override
    public void preInit(FMLPreInitializationEvent event) {
        initSQLite();
        Minelife.MODS.forEach(mod -> {
            try {
                mod.getServerProxy().newInstance().preInit(event);
            } catch (InstantiationException | IllegalAccessException | NullPointerException ignored) {
            }
        });
    }

    @Override
    public void init(FMLInitializationEvent event) {
        Minelife.MODS.forEach(mod -> {
            try {
                mod.getServerProxy().newInstance().init(event);
            } catch (InstantiationException | IllegalAccessException | NullPointerException ignored) {
            }
        });
    }

    private void initSQLite() {
        String prefix = "[" + Minelife.NAME + "]";
        String directory = Minelife.getDirectory().getAbsolutePath();
        String dbName = Minelife.MOD_ID;
        Minelife.SQLITE = new SQLite(Minelife.getLogger(), prefix, directory, dbName);
        Minelife.SQLITE.open();
    }

}
