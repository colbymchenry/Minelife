package com.minelife.gun.client;

import com.minelife.Minelife;
import com.minelife.gun.packet.PacketReload;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;

public class KeyStrokeListener {

    private KeyBinding keyReload = new KeyBinding("key." + Minelife.MOD_ID + ".guns.reload", Keyboard.KEY_R, "key." + Minelife.MOD_ID + ".guns");
    private KeyBinding keyChangeAmmo = new KeyBinding("key." + Minelife.MOD_ID + ".guns.changeAmmo", Keyboard.KEY_Q, "key." + Minelife.MOD_ID + ".guns");

    public KeyStrokeListener() {
        ClientRegistry.registerKeyBinding(keyReload);
        ClientRegistry.registerKeyBinding(keyChangeAmmo);
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if(keyReload.isPressed()) {
            Minelife.NETWORK.sendToServer(new PacketReload());
        }
    }

}
