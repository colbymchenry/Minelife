package com.minelife.gun.server;

import com.minelife.gun.item.guns.ItemGun;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

public class TickListener {

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event)
    {

        // TODO: Does this allow instant reload? Need to fix this
        if (event.player.getHeldItem() == null || !(event.player.getHeldItem().getItem() instanceof ItemGun)) {
            for (int i = 0; i < event.player.inventory.getSizeInventory(); i++) {
                if (event.player.inventory.getStackInSlot(i) != null &&
                        event.player.inventory.getStackInSlot(i).getItem() instanceof ItemGun) {
                    if (event.player.inventory.getStackInSlot(i).hasTagCompound())
                        event.player.inventory.getStackInSlot(i).stackTagCompound.removeTag("reloadTime");
                }
            }
        }
    }

}
