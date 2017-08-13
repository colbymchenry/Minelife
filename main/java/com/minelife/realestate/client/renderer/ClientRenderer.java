package com.minelife.realestate.client.renderer;

import com.minelife.realestate.client.estateselection.Selection;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;

public class ClientRenderer {

    @SubscribeEvent
    public void tick(RenderWorldLastEvent event) {
        Selection.render(event);
    }

    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Pre event) {
        Selection.renderPrice(event);
    }

}