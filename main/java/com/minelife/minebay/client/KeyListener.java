package com.minelife.minebay.client;

import com.minelife.MLKeys;
import com.minelife.minebay.client.gui.ListingsGui;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import net.minecraft.client.Minecraft;


public class KeyListener {

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if(MLKeys.key_minebay.isPressed()) {
            Minecraft.getMinecraft().displayGuiScreen(new ListingsGui());
        }
    }

}
