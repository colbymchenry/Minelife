package com.minelife.minebay.packet;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;

import java.util.UUID;

public class PacketBuyItem implements IMessage {

    private UUID listing_uuid;

    public PacketBuyItem() {}

    public PacketBuyItem(UUID listing_uuid) {
        this.listing_uuid = listing_uuid;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {

    }

    @Override
    public void toBytes(ByteBuf buf)
    {

    }
}
