package com.minelife.police;

import com.minelife.AbstractGuiHandler;
import com.minelife.AbstractMod;
import com.minelife.CommonProxy;
import com.minelife.Minelife;
import com.minelife.police.arresting.PacketDropPlayer;
import com.minelife.police.arresting.PlayerListener;
import com.minelife.police.client.ClientProxy;
import com.minelife.police.computer.TileEntityPoliceComputer;
import com.minelife.police.network.PacketCreateTicket;
import com.minelife.police.network.PacketOpenTicketInventory;
import com.minelife.police.network.PacketWriteTicketToDB;
import com.minelife.police.server.CommandPolice;
import com.minelife.police.server.ServerProxy;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;

public class ModPolice extends AbstractMod {

    // TODO: Make the model for the computer to submit tickets to the DB
    @Override
    public void preInit(FMLPreInitializationEvent event) {
        registerPacket(PacketCreateTicket.Handler.class, PacketCreateTicket.class, Side.SERVER);
        registerPacket(PacketOpenTicketInventory.Handler.class, PacketOpenTicketInventory.class, Side.SERVER);
        registerPacket(PacketWriteTicketToDB.Handler.class, PacketWriteTicketToDB.class, Side.SERVER);
        registerPacket(PacketDropPlayer.Handler.class, PacketDropPlayer.class, Side.SERVER);

        MinecraftForge.EVENT_BUS.register(PlayerListener.instance);
        FMLCommonHandler.instance().bus().register(PlayerListener.instance);

        GameRegistry.registerTileEntity(TileEntityPoliceComputer.class, "police_computer");
    }

    @Override
    public Class<? extends CommonProxy> getServerProxyClass() {
        return ServerProxy.class;
    }

    @Override
    public Class<? extends CommonProxy> getClientProxyClass() {
        return ClientProxy.class;
    }

    @Override
    public AbstractGuiHandler gui_handler() {
        return new GuiHandler();
    }

    @SideOnly(Side.SERVER)
    public static ServerProxy getServerProxy() {
        return (ServerProxy) ((ModPolice) Minelife.getModInstance(ModPolice.class)).serverProxy;
    }

    @Override
    public void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandPolice());
    }
}
