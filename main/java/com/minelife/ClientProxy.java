package com.minelife;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import lib.PatPeter.SQLibrary.SQLite;

import java.util.logging.Logger;

public class ClientProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        initSQLite();
        KeyBindings.registerKeys();

        Minelife.MODS.stream().forEach(mod -> {
            try {
                mod.getClientProxy().newInstance().preInit(event);
            } catch (InstantiationException e) {
            } catch (IllegalAccessException e) {
            } catch (NullPointerException e) {
            }
        });
    }

    @Override
    public void init(FMLInitializationEvent event) {
        Minelife.MODS.stream().forEach(mod -> {
            try {
                mod.getClientProxy().newInstance().init(event);
            } catch (InstantiationException e) {
            } catch (IllegalAccessException e) {
            } catch (NullPointerException e) {
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
