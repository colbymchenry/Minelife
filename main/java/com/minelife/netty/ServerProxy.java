package com.minelife.netty;

import com.minelife.MLProxy;
import com.minelife.util.MLConfig;
import com.minelife.util.configuration.InvalidConfigurationException;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.io.IOException;

public class ServerProxy extends MLProxy {

    protected static MLConfig CONFIG;
    protected static ChatClient CONNECTION;

    @Override
    public void preInit(FMLPreInitializationEvent event) throws Exception {
        try {
            CONFIG = new MLConfig("proxy");
            CONFIG.addDefault("netty_ip", "127.0.0.1");
            CONFIG.addDefault("netty_port", 8000);
            CONFIG.save();
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }

        CONNECTION = new ChatClient(CONFIG.getString("netty_ip"), CONFIG.getInt("netty_port"));
        CONNECTION.run();

        MinecraftForge.EVENT_BUS.register(new ConnectionRetryHandler());
    }
}
