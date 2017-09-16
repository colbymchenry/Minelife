package com.minelife.police;

import com.minelife.AbstractGuiHandler;
import com.minelife.AbstractMod;
import com.minelife.CommonProxy;
import com.minelife.police.arresting.PacketDropPlayer;
import com.minelife.police.arresting.PlayerListener;
import com.minelife.police.client.ClientProxy;
import com.minelife.police.network.PacketCreateTicket;
import com.minelife.police.network.PacketOpenTicketInventory;
import com.minelife.police.network.PacketWriteTicketToDB;
import com.minelife.police.server.ServerProxy;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;

public class ModPolice extends AbstractMod {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        registerPacket(PacketCreateTicket.Handler.class, PacketCreateTicket.class, Side.SERVER);
        registerPacket(PacketOpenTicketInventory.Handler.class, PacketOpenTicketInventory.class, Side.SERVER);
        registerPacket(PacketWriteTicketToDB.Handler.class, PacketWriteTicketToDB.class, Side.SERVER);
        registerPacket(PacketDropPlayer.Handler.class, PacketDropPlayer.class, Side.SERVER);

        MinecraftForge.EVENT_BUS.register(new PlayerListener());
    }

    @Override
    public Class<? extends CommonProxy> getServerProxy() {
        return ServerProxy.class;
    }

    @Override
    public Class<? extends CommonProxy> getClientProxy() {
        return ClientProxy.class;
    }

    @Override
    public AbstractGuiHandler gui_handler() {
        return new GuiHandler();
    }
}
