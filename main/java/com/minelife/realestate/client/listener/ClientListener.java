package com.minelife.realestate.client.listener;

import com.minelife.MLKeys;
import com.minelife.realestate.client.estateselection.Selection;
import com.minelife.realestate.util.PlayerUtil;
import com.minelife.util.NumberConversions;
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
            if (selection != null) {

                PlayerUtil.sendTo(player, "Are you sure you would like to purchase this estate for $" + NumberConversions.formatter.format(price) + "?");
            } else {
                PlayerUtil.sendTo(player, "Estate is null.");
            }
        }
    }

}