package com.minelife.realestate;

import com.minelife.AbstractMod;
import com.minelife.CommonProxy;
import com.minelife.Minelife;
import com.minelife.realestate.client.ClientProxy;
import com.minelife.realestate.network.*;
import com.minelife.realestate.server.CommandEstate;
import com.minelife.realestate.server.SelectionHandler;
import com.minelife.realestate.server.ServerProxy;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.relauncher.Side;
import net.minecraftforge.common.MinecraftForge;

public class ModRealEstate extends AbstractMod {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        registerPacket(PacketSendSelection.Handler.class, PacketSendSelection.class, Side.CLIENT);
        registerPacket(PacketGuiCreateEstate.Handler.class, PacketGuiCreateEstate.class, Side.CLIENT);
        registerPacket(PacketCreateEstate.Handler.class, PacketCreateEstate.class, Side.SERVER);
        registerPacket(PacketGuiPurchaseEstate.Handler.class, PacketGuiPurchaseEstate.class, Side.CLIENT);
        registerPacket(PacketPurchaseEstate.Handler.class, PacketPurchaseEstate.class, Side.SERVER);
        registerPacket(PacketGuiModifyEstate.Handler.class, PacketGuiModifyEstate.class, Side.CLIENT);
        registerPacket(PacketUpdateEstate.Handler.class, PacketUpdateEstate.class, Side.SERVER);
        registerPacket(PacketGetMembers.Handler.class, PacketGetMembers.class, Side.SERVER);
        registerPacket(PacketSendMembers.Handler.class, PacketSendMembers.class, Side.CLIENT);
        registerPacket(PacketAddMember.Handler.class, PacketAddMember.class, Side.SERVER);
        registerPacket(PacketRemoveMember.Handler.class, PacketRemoveMember.class, Side.SERVER);
        registerPacket(PacketModifyMember.Handler.class, PacketModifyMember.class, Side.SERVER);
        MinecraftForge.EVENT_BUS.register(new SelectionHandler());
        FMLCommonHandler.instance().bus().register(new SelectionHandler());
    }

    @Override
    public void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandEstate());
    }

    @Override
    public Class<? extends CommonProxy> getClientProxyClass() {
        return com.minelife.realestate.client.ClientProxy.class;
    }

    @Override
    public Class<? extends CommonProxy> getServerProxyClass() {
        return com.minelife.realestate.server.ServerProxy.class;
    }

    public static ServerProxy getServerProxy() {
        return (ServerProxy) Minelife.getModInstance(ModRealEstate.class).serverProxy;
    }

    public static ClientProxy getClientProxy() {
        return (ClientProxy) Minelife.getModInstance(ModRealEstate.class).clientProxy;
    }

}
