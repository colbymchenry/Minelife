package com.minelife.clothing;

import com.minelife.MLMod;
import com.minelife.MLProxy;

public class ModClothes extends MLMod {

    @Override
    public Class<? extends MLProxy> getClientProxyClass() {
        return com.minelife.clothing.ClientProxy.class;
    }
}
