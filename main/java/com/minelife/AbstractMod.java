package com.minelife;

import com.google.common.collect.Maps;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.block.Block;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class AbstractMod {

    public void preInit(FMLPreInitializationEvent event) {}

    public void init(FMLInitializationEvent event) {}

    public void serverStarting(FMLServerStartingEvent event) {}

    public Class<? extends CommonProxy> getClientProxy() {return null;}

    public Class<? extends CommonProxy> getServerProxy() {return null;}

    public AbstractGuiHandler gui_handler() { return null; }

    private static int PACKET_ID = 0;
    private static Map<Integer, Class> gui_map = Maps.newHashMap();
    private static Map<Integer, Class> container_map = Maps.newHashMap();

    public static final void registerPacket(Class messageHandler, Class message, Side receivingSide) {
        Minelife.NETWORK.registerMessage(messageHandler, message, PACKET_ID++, receivingSide);
    }

}
