package com.minelife.police.network;

import com.google.common.collect.Lists;
import com.minelife.police.TicketSearchResult;
import com.minelife.police.client.gui.computer.GuiTicketSearch;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;

import java.util.List;

public class PacketResponseTicketSearch implements IMessage {

    private List<TicketSearchResult> results;

    public PacketResponseTicketSearch(List<TicketSearchResult> results)
    {
        this.results = results;
    }

    public PacketResponseTicketSearch()
    {
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        results = Lists.newArrayList();
        int numOfResults = buf.readInt();
        for (int i = 0; i < numOfResults; i++) results.add(TicketSearchResult.fromBytes(buf));
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(results.size());
        results.forEach(result -> result.toBytes(buf));
    }

    public static class Handler implements IMessageHandler<PacketResponseTicketSearch, IMessage> {

        @SideOnly(Side.CLIENT)
        public IMessage onMessage(PacketResponseTicketSearch message, MessageContext ctx)
        {
            if(Minecraft.getMinecraft().currentScreen != null) {
                if(Minecraft.getMinecraft().currentScreen instanceof GuiTicketSearch) {
                    Minecraft.getMinecraft().displayGuiScreen(new GuiTicketSearch(message.results));
                }
            }
            return null;
        }
    }
}
