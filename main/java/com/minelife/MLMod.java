package com.minelife;

import com.google.common.collect.Maps;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraftforge.client.event.TextureStitchEvent;

import java.util.Map;

public abstract class MLMod {

    @SideOnly(Side.SERVER)
    public MLProxy serverProxy;

    @SideOnly(Side.CLIENT)
    public MLProxy clientProxy;

    public void preInit(FMLPreInitializationEvent event)
    {
    }

    public void init(FMLInitializationEvent event)
    {
    }

    public void serverStarting(FMLServerStartingEvent event)
    {
    }

    public void onLoadComplete(FMLLoadCompleteEvent event)
    {
    }

    public void postInit(FMLPostInitializationEvent event)
    {
    }

    public Class<? extends MLProxy> getClientProxyClass()
    {
        return null;
    }

    public Class<? extends MLProxy> getServerProxyClass()
    {
        return null;
    }

    public AbstractGuiHandler gui_handler()
    {
        return null;
    }

    @SideOnly(Side.CLIENT)
    public void textureHook(TextureStitchEvent.Post event)
    {

    }

    private static int PACKET_ID = 0;
    private static Map<Integer, Class> gui_map = Maps.newHashMap();
    private static Map<Integer, Class> container_map = Maps.newHashMap();

    public static final void registerPacket(Class messageHandler, Class message, Side receivingSide)
    {
        Minelife.NETWORK.registerMessage(messageHandler, message, PACKET_ID++, receivingSide);
    }

}
