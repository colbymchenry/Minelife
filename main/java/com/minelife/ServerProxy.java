package com.minelife;

import com.minelife.util.SoundTrack;
import com.minelife.util.client.netty.ChatClient;
import com.minelife.util.client.netty.ConnectionRetryHandler;
import com.minelife.util.client.netty.NettyPlayerListener;
import com.minelife.util.client.netty.PacketSendNettyServer;
import com.minelife.util.server.*;
import com.minelife.welfare.WelfareListener;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import lib.PatPeter.SQLibrary.SQLite;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;

import java.util.logging.Level;

public class ServerProxy extends MLProxy {


    @Override
    public void preInit(FMLPreInitializationEvent event) {
        FMLCommonHandler.instance().bus().register(new UUIDFetcher());
        FMLCommonHandler.instance().bus().register(new MLCommand.Ticker());

        try {
            Minelife.NETTY_CONNECTION = new ChatClient(Minelife.config.getString("netty_ip"), Minelife.config.getInt("netty_port"));
            Minelife.NETTY_CONNECTION.run();
        } catch (Exception e) {
            e.printStackTrace();
        }

        FMLCommonHandler.instance().bus().register(new ConnectionRetryHandler());
        FMLCommonHandler.instance().bus().register(new WelfareListener());
        FMLCommonHandler.instance().bus().register(new SoundTrack());

        initSQLite();
        Minelife.MODS.forEach(mod -> {
            try {
                mod.serverProxy = mod.getServerProxyClass().newInstance();
                mod.serverProxy.preInit(event);
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
                mod.serverProxy.init(event);
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
