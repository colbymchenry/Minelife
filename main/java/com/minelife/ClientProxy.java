package com.minelife;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import lib.PatPeter.SQLibrary.SQLite;

public class ClientProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        initSQLite();
        MLKeys.registerKeys();

        Minelife.MODS.forEach(mod -> {
            try {
                mod.getClientProxy().newInstance().preInit(event);
            } catch (InstantiationException | IllegalAccessException | NullPointerException ignored) {
            }
        });
    }

    @Override
    public void init(FMLInitializationEvent event) {
        Minelife.MODS.forEach(mod -> {
            try {
                mod.getClientProxy().newInstance().init(event);
            } catch (InstantiationException | IllegalAccessException | NullPointerException e) {
            }
        });
    }

    private void initSQLite() {
        String prefix = "[" + Minelife.NAME + "]";
        String directory = Minelife.getConfigDirectory().getAbsolutePath();
        String dbName = Minelife.MOD_ID;
        Minelife.SQLITE = new SQLite(Minelife.getLogger(), prefix, directory, dbName);
        Minelife.SQLITE.open();
    }
}
