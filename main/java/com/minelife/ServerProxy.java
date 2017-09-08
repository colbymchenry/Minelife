package com.minelife;

import com.minelife.util.server.FetchNameThread;
import com.minelife.util.server.FetchUUIDThread;
import com.minelife.util.server.UUIDFetcher;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import lib.PatPeter.SQLibrary.SQLite;
import net.minecraftforge.common.MinecraftForge;

public class ServerProxy extends CommonProxy {


    @Override
    public void preInit(FMLPreInitializationEvent event) {
        FMLCommonHandler.instance().bus().register(new UUIDFetcher());
        new Thread(FetchNameThread.instance = new FetchNameThread()).start();
        new Thread(FetchUUIDThread.instance = new FetchUUIDThread()).start();

        initSQLite();
        Minelife.MODS.forEach(mod -> {
            try {
                mod.getServerProxy().newInstance().preInit(event);
            } catch (InstantiationException | IllegalAccessException | NullPointerException ignored) {
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void init(FMLInitializationEvent event) {
        Minelife.MODS.forEach(mod -> {
            try {
                mod.getServerProxy().newInstance().init(event);
            } catch (InstantiationException | IllegalAccessException | NullPointerException ignored) {
            } catch (Exception e) {
                e.printStackTrace();
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
