package com.minelife.police;

import com.minelife.CommonProxy;
import com.minelife.SubMod;
import com.minelife.police.client.ClientProxy;
import com.minelife.police.packet.PacketArrestPlayer;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;

public class ModPolice extends SubMod {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        registerPacket(PacketArrestPlayer.Handler.class, PacketArrestPlayer.class, Side.CLIENT);

        GameRegistry.registerItem(ItemHandcuff.INSTANCE, ItemHandcuff.NAME);
    }

    @Override
    public Class<? extends CommonProxy> getServerProxy() {
        return com.minelife.police.server.ServerProxy.class;
    }

    @Override
    public Class<? extends CommonProxy> getClientProxy() {
        return ClientProxy.class;
    }

    // TODO: Make jail regions, and jail spawns/cells

}
