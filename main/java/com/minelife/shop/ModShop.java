package com.minelife.shop;

import com.minelife.MLMod;
import com.minelife.MLProxy;

public class ModShop extends MLMod {

    @Override
    public Class<? extends MLProxy> getServerProxyClass() {
        return com.minelife.shop.server.ServerProxy.class;
    }

    @Override
    public Class<? extends MLProxy> getClientProxyClass() {
        return com.minelife.shop.client.ClientProxy.class;
    }
}
