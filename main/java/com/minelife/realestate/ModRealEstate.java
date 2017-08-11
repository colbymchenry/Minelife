package com.minelife.realestate;

import com.minelife.AbstractMod;
import com.minelife.CommonProxy;

public class ModRealEstate extends AbstractMod {

    public static int pricePerBlock = 2;

    @Override
    public Class<? extends CommonProxy> getClientProxy() {
        return com.minelife.realestate.client.ClientProxy.class;
    }

    @Override
    public Class<? extends CommonProxy> getServerProxy() {
        return com.minelife.realestate.server.ServerProxy.class;
    }
}