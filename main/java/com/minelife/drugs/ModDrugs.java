package com.minelife.drugs;

import com.minelife.MLMod;
import com.minelife.MLProxy;

public class ModDrugs extends MLMod {

    @Override
    public Class<? extends MLProxy> getClientProxyClass() {
        return com.minelife.drugs.client.ClientProxy.class;
    }

    @Override
    public Class<? extends MLProxy> getServerProxyClass() {
        return com.minelife.drugs.server.ServerProxy.class;
    }
}
