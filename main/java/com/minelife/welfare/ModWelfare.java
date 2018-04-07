package com.minelife.welfare;

import com.minelife.MLMod;
import com.minelife.MLProxy;

public class ModWelfare extends MLMod {

    @Override
    public Class<? extends MLProxy> getServerProxyClass() {
        return com.minelife.welfare.ServerProxy.class;
    }
}
