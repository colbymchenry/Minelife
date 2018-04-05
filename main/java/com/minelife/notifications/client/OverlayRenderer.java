package com.minelife.notifications.client;

import com.google.common.collect.Lists;
import com.minelife.notifications.Notification;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;

public class OverlayRenderer {

    @SubscribeEvent
    public void onGameOverlay(RenderGameOverlayEvent.Pre event) {
        if(event.getType() != RenderGameOverlayEvent.ElementType.HEALTH) return;

        if(ClientProxy.notifications.isEmpty()) return;

        List<Notification> notifications = Lists.newArrayList();
        // TODO
    }

}
