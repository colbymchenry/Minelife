package com.minelife.police;

import com.minelife.AbstractMod;
import com.minelife.CommonProxy;
import com.minelife.police.client.ClientProxy;
import com.minelife.police.network.PacketCreateTicket;
import com.minelife.police.server.ServerProxy;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayer;

public class ModPolice extends AbstractMod {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        registerPacket(PacketCreateTicket.Handler.class, PacketCreateTicket.class, Side.SERVER);
    }

    @Override
    public Class<? extends CommonProxy> getServerProxy() {
        return ServerProxy.class;
    }

    @Override
    public Class<? extends CommonProxy> getClientProxy() {
        return ClientProxy.class;
    }
}
