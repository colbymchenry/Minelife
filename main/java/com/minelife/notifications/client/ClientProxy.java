package com.minelife.notifications.client;

import com.google.common.collect.Lists;
import com.minelife.MLProxy;
import com.minelife.notifications.Notification;

import java.util.List;

public class ClientProxy extends MLProxy {

    public static List<Notification> notifications = Lists.newArrayList();

}
