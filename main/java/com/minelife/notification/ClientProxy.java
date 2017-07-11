package com.minelife.notification;

import com.minelife.CommonProxy;
import com.minelife.KeyBindings;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;

public class ClientProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(new NotificationOverlay());
        FMLCommonHandler.instance().bus().register(this);
    }

    @SubscribeEvent
    public void onKeyPress(InputEvent.KeyInputEvent event) {
        if(KeyBindings.keyNotifications.isPressed()) Minecraft.getMinecraft().displayGuiScreen(new GuiNotifications());
    }
}
