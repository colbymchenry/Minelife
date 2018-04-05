package com.minelife.notifications.client;

import com.google.common.collect.Lists;
import com.minelife.notifications.Notification;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;

public class OverlayRenderer {

    private static List<NotificationRenderInfo> notificationRenderInfoList = Lists.newArrayList();

    @SubscribeEvent
    public void onGameOverlay(RenderGameOverlayEvent.Pre event) {
        if(event.getType() != RenderGameOverlayEvent.ElementType.HEALTH) return;

        if(ClientProxy.notifications.isEmpty()) return;

        for (NotificationRenderInfo notificationRenderInfo : notificationRenderInfoList) {
            if(ClientProxy.notifications.contains(notificationRenderInfo.notification)) {

            }
        }
        // TODO
    }

    class NotificationRenderInfo {
        Notification notification;
        int x, y;

        public NotificationRenderInfo(Notification notification, int x, int y) {
            this.notification = notification;
            this.x = x;
            this.y = y;
        }
    }

}
