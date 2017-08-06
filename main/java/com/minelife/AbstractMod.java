package com.minelife;

import com.google.common.collect.Maps;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.TextureStitchEvent;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;

public class AbstractMod {

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



    public Class<? extends CommonProxy> getClientProxy()
    {
        return null;
    }

    public Class<? extends CommonProxy> getServerProxy()
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
