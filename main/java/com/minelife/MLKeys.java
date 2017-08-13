package com.minelife;

import cpw.mods.fml.client.registry.ClientRegistry;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;

public class MLKeys {

    public static KeyBinding keyNotifications = new KeyBinding("key." + Minelife.MOD_ID + ".notifications", Keyboard.KEY_N, Minelife.NAME);
    public static KeyBinding keyReload = new KeyBinding("key." + Minelife.MOD_ID + ".guns.reload", Keyboard.KEY_R, Minelife.NAME);
    public static KeyBinding keyChangeAmmo = new KeyBinding("key." + Minelife.MOD_ID + ".guns.changeAmmo", Keyboard.KEY_F, Minelife.NAME);
    public static KeyBinding keyZoneInfo = new KeyBinding("key." + Minelife.MOD_ID + ".zone.info", Keyboard.KEY_I, Minelife.NAME);
    // Real Estate Key Bindings
    public static KeyBinding keySelectionClear = new KeyBinding("key." + Minelife.MOD_ID + ".clear.selection", Keyboard.KEY_C, Minelife.NAME);
    public static KeyBinding keyPurchaseSelection = new KeyBinding("key." + Minelife.MOD_ID + ".purchase.selection", Keyboard.KEY_B, Minelife.NAME);


    public static void registerKeys() {
        ClientRegistry.registerKeyBinding(keyNotifications);
        ClientRegistry.registerKeyBinding(keyReload);
        ClientRegistry.registerKeyBinding(keyChangeAmmo);
        ClientRegistry.registerKeyBinding(keyZoneInfo);
        ClientRegistry.registerKeyBinding(keySelectionClear);
        ClientRegistry.registerKeyBinding(keyPurchaseSelection);
    }

}
