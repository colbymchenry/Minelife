package com.minelife.minebay;

import com.minelife.AbstractMod;
import com.minelife.CommonProxy;

public class ModMinebay extends AbstractMod {

    @Override
    public Class<? extends CommonProxy> getClientProxy()
    {
        return com.minelife.minebay.client.ClientProxy.class;
    }

    @Override
    public Class<? extends CommonProxy> getServerProxy()
    {
        return com.minelife.minebay.server.ServerProxy.class;
    }
}
