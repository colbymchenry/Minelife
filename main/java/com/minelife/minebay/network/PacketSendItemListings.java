package com.minelife.minebay.network;

import com.google.common.collect.Lists;
import com.minelife.minebay.ItemListing;
import com.minelife.minebay.client.gui.GuiItemListings;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class PacketSendItemListings implements IMessage {

    private List<ItemListing> listings;
    private int pages;

    public PacketSendItemListings() {
    }

    public PacketSendItemListings(List<ItemListing> listings, int pages) {
        this.listings = listings;
        this.pages = pages;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        listings = Lists.newArrayList();
        int size = buf.readInt();
        for (int i = 0; i < size; i++) listings.add(ItemListing.fromBytes(buf));
        pages = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(listings.size());
        listings.forEach(listing -> listing.toBytes(buf));
        buf.writeInt(pages);
    }

    public static class Handler implements IMessageHandler<PacketSendItemListings, IMessage> {

        @SideOnly(Side.CLIENT)
        public IMessage onMessage(PacketSendItemListings message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() ->
                    Minecraft.getMinecraft().displayGuiScreen(new GuiItemListings(message.listings, message.pages))
            );
            return null;
        }
    }

}
