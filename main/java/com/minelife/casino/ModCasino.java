package com.minelife.casino;

import com.minelife.MLMod;
import com.minelife.MLProxy;
import com.minelife.util.MLConfig;

public class ModCasino extends MLMod {

    public static MLConfig config;

    @Override
    public Class<? extends MLProxy> getClientProxyClass() {
        return com.minelife.casino.client.ClientProxy.class;
    }

    @Override
    public Class<? extends MLProxy> getServerProxyClass() {
        return com.minelife.casino.server.ServerProxy.class;
    }
}
