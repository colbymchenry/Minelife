package com.minelife.notification;

import com.minelife.AbstractMod;
import com.minelife.CommonProxy;

public class ModNotifications extends AbstractMod {

    @Override
    public Class<? extends CommonProxy> getClientProxy()
    {
        return com.minelife.notification.ClientProxy.class;
    }
}
