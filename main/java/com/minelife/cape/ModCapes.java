package com.minelife.cape;

import com.minelife.MLMod;
import com.minelife.MLProxy;
import com.minelife.cape.network.PacketSetPixels;
import com.minelife.cape.network.PacketUpdateCape;
import com.minelife.cape.network.PacketUpdateCapeStatus;
import com.minelife.cape.server.CommandCape;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.relauncher.Side;

public class ModCapes extends MLMod {

    public static ItemCape itemCape;

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        registerItem(itemCape = new ItemCape());

        registerPacket(PacketSetPixels.Handler.class, PacketSetPixels.class, Side.SERVER);
        registerPacket(PacketUpdateCape.Handler.class, PacketUpdateCape.class, Side.CLIENT);
        registerPacket(PacketUpdateCapeStatus.Handler.class, PacketUpdateCapeStatus.class, Side.CLIENT);
    }

    @Override
    public void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandCape());
    }

    @Override
    public Class<? extends MLProxy> getClientProxyClass() {
        return com.minelife.cape.client.ClientProxy.class;
    }

    @Override
    public Class<? extends MLProxy> getServerProxyClass() {
        return com.minelife.cape.server.ServerProxy.class;
    }
}
