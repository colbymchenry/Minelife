package com.minelife.notification;

import com.minelife.MLProxy;
import com.minelife.MLKeys;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;

public class ClientProxy extends MLProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(new NotificationOverlay());
        FMLCommonHandler.instance().bus().register(this);
    }

    @SubscribeEvent
    public void onKeyPress(InputEvent.KeyInputEvent event) {
        if(MLKeys.keyNotifications.isPressed()) Minecraft.getMinecraft().displayGuiScreen(new GuiNotifications());
    }
}
