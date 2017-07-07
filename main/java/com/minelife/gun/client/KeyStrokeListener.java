package com.minelife.gun.client;

import com.minelife.Minelife;
import com.minelife.gun.item.guns.ItemGun;
import com.minelife.gun.packet.PacketReload;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import org.lwjgl.input.Keyboard;

public class KeyStrokeListener {

    private KeyBinding keyReload = new KeyBinding("key." + Minelife.MOD_ID + ".guns.reload", Keyboard.KEY_R, Minelife.NAME);
    private KeyBinding keyChangeAmmo = new KeyBinding("key." + Minelife.MOD_ID + ".guns.changeAmmo", Keyboard.KEY_Q, Minelife.NAME);

    public KeyStrokeListener() {
        ClientRegistry.registerKeyBinding(keyReload);
        ClientRegistry.registerKeyBinding(keyChangeAmmo);
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (keyReload.isPressed()) {
            Minelife.NETWORK.sendToServer(new PacketReload());
        }

        if (keyChangeAmmo.isPressed()) {
            EntityPlayer player = Minecraft.getMinecraft().thePlayer;
            ItemGun gun = player.getHeldItem() != null && player.getHeldItem().getItem() instanceof com.minelife.gun.item.guns.ItemGun ? (ItemGun) player.getHeldItem().getItem() : null;
            if (gun == null) return;

            if (Minecraft.getMinecraft().currentScreen == null)
                Minecraft.getMinecraft().displayGuiScreen(new GuiChangeAmmoType(gun));
        }
    }

}
