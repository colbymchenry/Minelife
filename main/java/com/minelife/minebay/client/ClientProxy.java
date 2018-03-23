package com.minelife.minebay.client;

import com.minelife.MLProxy;
import com.minelife.Minelife;
import com.minelife.minebay.client.gui.GuiItemListings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;

public class ClientProxy extends MLProxy {

    private KeyBinding minebayKey = new KeyBinding("key." + Minelife.MOD_ID + ".minebay", Keyboard.KEY_M, Minelife.NAME);

    @Override
    public void preInit(FMLPreInitializationEvent event) throws Exception {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if(minebayKey.isPressed()) {
            Minecraft.getMinecraft().displayGuiScreen(new GuiItemListings());
        }
    }


}
