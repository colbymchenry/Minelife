package com.minelife.minebay.packet;

import com.google.common.collect.Lists;
import com.minelife.minebay.ItemListing;
import com.minelife.minebay.client.gui.ListingsGui;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;

import java.util.List;

public class PacketResponseListings implements IMessage {

    private List<ItemListing> listings;

    public PacketResponseListings() {}

    public PacketResponseListings(List<ItemListing> listings) {
        this.listings = listings;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        listings = Lists.newArrayList();
        int size = buf.readInt();
        for(int i = 0; i < size; i++) listings.add(ItemListing.from_bytes(buf));
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(listings.size());
        listings.forEach(listing -> listing.to_bytes(buf));
    }

    public static class Handler implements IMessageHandler<PacketResponseListings, IMessage> {

        @SideOnly(Side.CLIENT)
        public IMessage onMessage(PacketResponseListings message, MessageContext ctx)
        {
            Minecraft.getMinecraft().displayGuiScreen(new ListingsGui(message.listings));
            return null;
        }
    }
}
