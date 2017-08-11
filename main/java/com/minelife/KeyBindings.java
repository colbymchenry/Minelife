package com.minelife;

import cpw.mods.fml.client.registry.ClientRegistry;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;

public class KeyBindings {

    public static KeyBinding keyNotifications = new KeyBinding("key." + Minelife.MOD_ID + ".notifications", Keyboard.KEY_N, Minelife.NAME);
    public static KeyBinding keyReload = new KeyBinding("key." + Minelife.MOD_ID + ".guns.reload", Keyboard.KEY_R, Minelife.NAME);
    public static KeyBinding keyChangeAmmo = new KeyBinding("key." + Minelife.MOD_ID + ".guns.changeAmmo", Keyboard.KEY_Q, Minelife.NAME);
    public static KeyBinding keyZoneInfo = new KeyBinding("key." + Minelife.MOD_ID + ".zone.info", Keyboard.KEY_I, Minelife.NAME);

    public static void registerKeys() {
        ClientRegistry.registerKeyBinding(keyNotifications);
        ClientRegistry.registerKeyBinding(keyReload);
        ClientRegistry.registerKeyBinding(keyChangeAmmo);
        ClientRegistry.registerKeyBinding(keyZoneInfo);
    }

}