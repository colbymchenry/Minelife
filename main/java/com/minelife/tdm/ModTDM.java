package com.minelife.tdm;

import com.minelife.MLMod;
import com.minelife.MLProxy;
import com.minelife.tdm.network.*;
import com.minelife.tdm.server.CommandTDM;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.relauncher.Side;

public class ModTDM extends MLMod {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        registerPacket(PacketOpenMatchSearch.Handler.class, PacketOpenMatchSearch.class, Side.CLIENT);
        registerPacket(PacketJoinGame.Handler.class, PacketJoinGame.class, Side.SERVER);
        registerPacket(PacketOpenLobby.Handler.class, PacketOpenLobby.class, Side.CLIENT);
        registerPacket(PacketUpdateLobby.Handler.class, PacketUpdateLobby.class, Side.CLIENT);
        registerPacket(PacketLeaveMatch.Handler.class, PacketLeaveMatch.class, Side.SERVER);
        registerPacket(PacketSetLoudout.Handler.class, PacketSetLoudout.class, Side.SERVER);
        registerPacket(PacketSendSecondsTillStart.Handler.class, PacketSendSecondsTillStart.class, Side.CLIENT);
        registerPacket(PacketOpenLobbyPlayers.Handler.class, PacketOpenLobbyPlayers.class, Side.CLIENT);
    }

    @Override
    public Class<? extends MLProxy> getClientProxyClass() {
        return com.minelife.tdm.client.ClientProxy.class;
    }

    @Override
    public Class<? extends MLProxy> getServerProxyClass() {
        return com.minelife.tdm.server.ServerProxy.class;
    }

    @Override
    public void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandTDM());
    }
}
