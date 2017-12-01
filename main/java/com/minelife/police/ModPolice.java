package com.minelife.police;

import com.minelife.MLMod;
import com.minelife.MLProxy;
import com.minelife.Minelife;
import com.minelife.police.server.ServerProxy;

public class ModPolice extends MLMod {

    @Override
    public Class<? extends MLProxy> getServerProxyClass() {
        return com.minelife.police.server.ServerProxy.class;
    }

    public static ServerProxy getServerProxy() {
        return (ServerProxy) Minelife.getModInstance(ModPolice.class).serverProxy;
    }
}
