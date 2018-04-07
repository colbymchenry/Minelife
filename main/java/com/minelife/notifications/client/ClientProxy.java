package com.minelife.notifications.client;

import com.google.common.collect.Lists;
import com.minelife.MLProxy;
import com.minelife.Minelife;
import com.minelife.notifications.Notification;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;

import java.util.LinkedList;

public class ClientProxy extends MLProxy {

    protected static LinkedList<Notification> notifications = Lists.newLinkedList();
    private KeyBinding notificationsKey = new KeyBinding("key." + Minelife.MOD_ID + ".notifications", Keyboard.KEY_N, Minelife.NAME);

    @Override
    public void preInit(FMLPreInitializationEvent event) throws Exception {
        MinecraftForge.EVENT_BUS.register(new OverlayRenderer());
        MinecraftForge.EVENT_BUS.register(this);
        ClientRegistry.registerKeyBinding(notificationsKey);
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (notificationsKey.isPressed()) {
            Minecraft.getMinecraft().displayGuiScreen(new GuiNotifications());
        }
    }

}
