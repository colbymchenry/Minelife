package com.minelife;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
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
}
