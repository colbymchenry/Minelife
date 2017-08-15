package com.minelife.realestate.client.renderer;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;

@SideOnly(Side.CLIENT)
public class InWorldRenderer {

    @SubscribeEvent
    public void render(RenderWorldLastEvent event) {
        SelectionRenderer.onEventTick(event);
    }

    @SubscribeEvent
    public void render(RenderGameOverlayEvent event) {
        SelectionRenderer.onEventTick(event);
    }

}