package com.minelife.realestate.client.renderer;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;

public class ClientRenderer {

    @SubscribeEvent
    public void tick(RenderWorldLastEvent event) {
        SelectionRenderer.render(event);
    }

    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Pre event) {
        SelectionRenderer.renderPrice(event);
    }

}