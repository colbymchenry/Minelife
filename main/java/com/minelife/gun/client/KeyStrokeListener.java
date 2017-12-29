package com.minelife.gun.client;

import com.minelife.MLKeys;
import com.minelife.Minelife;
import com.minelife.gun.client.guns.ItemGunClient;
import com.minelife.gun.item.guns.ItemGun;
import com.minelife.gun.packet.PacketReload;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.input.Keyboard;

public class KeyStrokeListener {

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {

        if (MLKeys.keyReload.isPressed() && !ItemGunClient.modifying) {
            Minelife.NETWORK.sendToServer(new PacketReload());
        }

        if (MLKeys.keyChangeAmmo.isPressed() && !ItemGunClient.modifying) {
            EntityPlayer player = Minecraft.getMinecraft().thePlayer;
            ItemGun gun = player.getHeldItem() != null && player.getHeldItem().getItem() instanceof com.minelife.gun.item.guns.ItemGun ? (ItemGun) player.getHeldItem().getItem() : null;
            if (gun == null) return;

            if (Minecraft.getMinecraft().currentScreen == null)
                Minecraft.getMinecraft().displayGuiScreen(new GuiChangeAmmoType(gun));
        }

        if(Keyboard.isKeyDown(Keyboard.KEY_0) ||
                Keyboard.isKeyDown(Keyboard.KEY_1) ||
                Keyboard.isKeyDown(Keyboard.KEY_2) ||
                Keyboard.isKeyDown(Keyboard.KEY_3) ||
                Keyboard.isKeyDown(Keyboard.KEY_4) ||
                Keyboard.isKeyDown(Keyboard.KEY_5) ||
                Keyboard.isKeyDown(Keyboard.KEY_6) ||
                Keyboard.isKeyDown(Keyboard.KEY_7) ||
                Keyboard.isKeyDown(Keyboard.KEY_8) ||
                Keyboard.isKeyDown(Keyboard.KEY_9)) {
            ItemGunClient.aimingDownSight = false;
        }
    }

}
