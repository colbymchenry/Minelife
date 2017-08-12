package com.minelife.realestate.client.renderer;

import com.minelife.MLKeys;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import net.minecraftforge.client.event.MouseEvent;

public class ClientListener {

    @SubscribeEvent
    public void onClick(MouseEvent event) {
        SelectionRenderer.cancelSelectionLeftClick(event);
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (MLKeys.keySelectionClear.isPressed()) {
            SelectionRenderer.setStart(null);
            SelectionRenderer.setEnd(null);
        }
    }

}