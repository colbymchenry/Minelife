package com.minelife.realestate.client.listener;

import com.minelife.MLItems;
import com.minelife.MLKeys;
import com.minelife.Minelife;
import com.minelife.realestate.client.estateselection.Selection;
import com.minelife.realestate.packets.client.RegionPurchaseRequestPacket;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.client.event.MouseEvent;

public class ClientListener {

    @SubscribeEvent
    public void onClick(MouseEvent event) {
        Selection.cancelSelectionLeftClick(event);
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        if (MLKeys.keySelectionClear.isPressed()) {
            Selection.setStart(null);
            Selection.setEnd(null);
        }
        if (MLKeys.keyPurchaseSelection.isPressed()) {
            AxisAlignedBB selection = Selection.getSelection();
            long price = Selection.getPrice();
            if (player.getHeldItem() != null && player.getHeldItem().getItem().equals(MLItems.estate_create_form) && selection != null) {
                Minelife.NETWORK.sendToServer(new RegionPurchaseRequestPacket(selection, price));
            }
        }
    }

}