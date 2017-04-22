package com.minelife.gun.client;

import com.minelife.Minelife;
import com.minelife.gun.PacketReload;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;

public class KeyStrokeListener {

    private KeyBinding keyReload = new KeyBinding("key.tut_inventory.desc", Keyboard.KEY_R, "key." + Minelife.MOD_ID + ".category");

    public KeyStrokeListener() {
        ClientRegistry.registerKeyBinding(keyReload);
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if(keyReload.isPressed()) {
            System.out.println("CALLED");
            Minelife.NETWORK.sendToServer(new PacketReload());
        }
    }

}
