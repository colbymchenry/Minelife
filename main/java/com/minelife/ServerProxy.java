package com.minelife;

import com.minelife.util.BreakHelper;
import com.minelife.util.SoundTrack;
import com.minelife.util.server.MLCommand;
import com.minelife.util.server.UUIDFetcher;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ServerProxy extends MLProxy {


    @Override
    public void preInit(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new UUIDFetcher());
        MinecraftForge.EVENT_BUS.register(new MLCommand.Ticker());
        MinecraftForge.EVENT_BUS.register(new SoundTrack());
        MinecraftForge.EVENT_BUS.register(new BreakHelper());

        Minelife.getModList().forEach(mod -> {
            try {
                mod.serverProxy = mod.getServerProxyClass().newInstance();
                mod.serverProxy.preInit(event);
            } catch (NullPointerException e1) {
            } catch (InstantiationException | IllegalAccessException ignored) {
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void init(FMLInitializationEvent event) {
        Minelife.getModList().forEach(mod -> {
            try {
                mod.serverProxy.init(event);
            } catch (NullPointerException e1) {
            } catch (InstantiationException | IllegalAccessException ignored) {
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

}
