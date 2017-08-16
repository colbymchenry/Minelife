package com.minelife.realestate.client.listener;

import com.minelife.MLItems;
import com.minelife.MLKeys;
import com.minelife.realestate.client.Selection;
import com.minelife.realestate.client.renderer.SelectionRenderer;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.MouseEvent;

@SideOnly(Side.CLIENT)
public class ClientListener {

    @SubscribeEvent
    public void onClick(MouseEvent event) {
        EntityPlayer player = Minecraft.getMinecraft().thePlayer;
        if (event.button == 0 && player.getHeldItem() != null && player.getHeldItem().getItem().equals(MLItems.estate_claim_form)) {
            event.setCanceled(true);
            player.getHeldItem().getItem().onEntitySwing(player, player.getHeldItem());
        }
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (MLKeys.keyClearSelection.isPressed()) SelectionRenderer.clear();
        if (MLKeys.keyPurchaseSelection.isPressed()) {
            Selection selection = SelectionRenderer.getSelection();
            if (selection != null && selection.isAvailable()) selection.purchase();
        }
    }

}