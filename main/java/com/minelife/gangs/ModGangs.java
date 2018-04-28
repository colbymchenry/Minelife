package com.minelife.gangs;

import com.minelife.MLMod;
import com.minelife.MLProxy;
import com.minelife.gangs.network.*;
import com.minelife.gangs.server.CommandGang;
import com.minelife.gangs.server.ServerProxy;
import lib.PatPeter.SQLibrary.Database;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.relauncher.Side;

public class ModGangs extends MLMod {

// TODO: Add rep, 5 ranks, pvp battles for betting, etc.


    @Override
    public void preInit(FMLPreInitializationEvent event) {
        registerPacket(PacketOpenGangGui.Handler.class, PacketOpenGangGui.class, Side.CLIENT);
        registerPacket(PacketAddMember.Handler.class, PacketAddMember.class, Side.SERVER);
        registerPacket(PacketRequestAlliance.Handler.class, PacketRequestAlliance.class, Side.SERVER);
        registerPacket(PacketRemoveAlliance.Handler.class, PacketRemoveAlliance.class, Side.SERVER);
        registerPacket(PacketSetMemberRole.Handler.class, PacketSetMemberRole.class, Side.SERVER);
        registerPacket(PacketRemoveMember.Handler.class, PacketRemoveMember.class, Side.SERVER);
        registerPacket(PacketSendGangMembers.Handler.class, PacketSendGangMembers.class, Side.CLIENT);
    }

    @Override
    public Class<? extends MLProxy> getServerProxyClass() {
        return com.minelife.gangs.server.ServerProxy.class;
    }

    @Override
    public Class<? extends MLProxy> getClientProxyClass() {
        return com.minelife.gangs.client.ClientProxy.class;
    }

    @Override
    public void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandGang());
    }

    public static Database getDatabase() {
        return ServerProxy.DB;
    }

}
