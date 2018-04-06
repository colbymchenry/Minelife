package com.minelife.notifications.client;

import com.google.common.collect.Lists;
import com.minelife.MLProxy;
import com.minelife.notifications.Notification;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.util.LinkedList;

public class ClientProxy extends MLProxy {

    protected static LinkedList<Notification> notifications = Lists.newLinkedList();

    @Override
    public void preInit(FMLPreInitializationEvent event) throws Exception {
        MinecraftForge.EVENT_BUS.register(new OverlayRenderer());
    }
}
