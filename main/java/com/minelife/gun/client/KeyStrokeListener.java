package com.minelife.gun.client;

import com.minelife.MLKeys;
import com.minelife.Minelife;
import com.minelife.gun.item.guns.ItemGun;
import com.minelife.gun.packet.PacketReload;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

public class KeyStrokeListener {

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (MLKeys.keyReload.isPressed()) {
            Minelife.NETWORK.sendToServer(new PacketReload());
        }

        if (MLKeys.keyChangeAmmo.isPressed()) {
            EntityPlayer player = Minecraft.getMinecraft().thePlayer;
            ItemGun gun = player.getHeldItem() != null && player.getHeldItem().getItem() instanceof com.minelife.gun.item.guns.ItemGun ? (ItemGun) player.getHeldItem().getItem() : null;
            if (gun == null) return;

            if (Minecraft.getMinecraft().currentScreen == null)
                Minecraft.getMinecraft().displayGuiScreen(new GuiChangeAmmoType(gun));
        }
    }

}
