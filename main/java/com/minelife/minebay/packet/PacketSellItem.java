package com.minelife.minebay.packet;

import com.minelife.minebay.ItemListing;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

import java.util.UUID;

public class PacketSellItem implements IMessage {

    private int slot, amount;
    private long price;

    public PacketSellItem() {}

    public PacketSellItem(int slot, int amount, long price) {
        this.slot = slot;
        this.amount = amount;
        this.price = price;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        this.slot = buf.readInt();
        this.amount = buf.readInt();
        this.price = buf.readLong();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(this.slot);
        buf.writeInt(this.amount);
        buf.writeLong(this.price);
    }

    public static class Handler implements IMessageHandler<PacketSellItem, IMessage> {

        @SideOnly(Side.SERVER)
        public IMessage onMessage(PacketSellItem message, MessageContext ctx)
        {
            EntityPlayerMP player = ctx.getServerHandler().playerEntity;
            ItemStack item_stack = player.inventory.mainInventory[message.slot];

            if(item_stack == null) return null;

            if(item_stack.stackSize < message.amount) return null;

            ItemStack item_stack_to_sale = item_stack.copy();
            item_stack_to_sale.stackSize = message.amount;

            item_stack.stackSize -= message.amount;
            player.inventory.mainInventory[message.slot] = item_stack;

            ItemListing listing = new ItemListing(UUID.randomUUID(), player.getUniqueID(), message.price, item_stack_to_sale);
            listing.write_to_db();
            player.closeScreen();
            return null;
        }
    }

}
