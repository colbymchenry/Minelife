package com.minelife.notifications;

import net.minecraftforge.fml.common.eventhandler.Event;

public class NotificationClickEvent extends Event {

    private Notification notification;

    public NotificationClickEvent(Notification notification) {
        this.notification = notification;
    }

    public Notification getNotification() {
        return notification;
    }
}
