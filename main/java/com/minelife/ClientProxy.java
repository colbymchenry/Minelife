package com.minelife;

import com.minelife.util.client.render.RenderPlayerCustom;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import lib.PatPeter.SQLibrary.SQLite;
import net.minecraft.entity.player.EntityPlayer;

public class ClientProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event)
    {
        initSQLite();
        MLKeys.registerKeys();
        RenderingRegistry.registerEntityRenderingHandler(EntityPlayer.class, new RenderPlayerCustom());

        Minelife.MODS.forEach(mod -> {
            try {
                mod.clientProxy = mod.getClientProxyClass().newInstance();
                mod.clientProxy.preInit(event);
            } catch (InstantiationException | IllegalAccessException | NullPointerException ignored) {
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void init(FMLInitializationEvent event)
    {
        Minelife.MODS.forEach(mod -> {
            try {
                mod.clientProxy.init(event);
            } catch (InstantiationException | IllegalAccessException | NullPointerException e) {
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void initSQLite()
    {
        String prefix = "[" + Minelife.NAME + "]";
        String directory = Minelife.getConfigDirectory().getAbsolutePath();
        String dbName = Minelife.MOD_ID;
        Minelife.SQLITE = new SQLite(Minelife.getLogger(), prefix, directory, dbName);
        Minelife.SQLITE.open();
    }
}
