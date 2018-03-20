package com.minelife;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends MLProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        Minelife.getModList().forEach(mod -> {
            try {
                mod.clientProxy = mod.getClientProxyClass().newInstance();
                mod.clientProxy.preInit(event);
            } catch (NullPointerException e1) {
            } catch (InstantiationException | IllegalAccessException ignored) {
                ignored.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void init(FMLInitializationEvent event) {
        Minelife.getModList().forEach(mod -> {
            try {
                mod.clientProxy.init(event);
            } catch (NullPointerException e1) {
            } catch (InstantiationException | IllegalAccessException e) {
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

}
