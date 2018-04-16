package com.minelife.minebay.network;

import com.google.common.collect.Sets;
import com.minelife.minebay.ItemListing;
import com.minelife.util.ItemHelper;
import com.minelife.util.PlayerHelper;
import com.minelife.util.client.PacketPopup;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.server.FMLServerHandler;

import java.util.Calendar;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class PacketSellItem implements IMessage {

    private ItemStack item_to_sale;
    private int storage;
    private int price;
    private int stackSize;

    public PacketSellItem() {
    }

    public PacketSellItem(ItemStack item_to_sale, int storage, int price, int stackSize) {
        this.item_to_sale = item_to_sale;
        this.storage = storage;
        this.price = price;
        this.stackSize = stackSize;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.item_to_sale = ByteBufUtils.readItemStack(buf);
        this.storage = buf.readInt();
        this.price = buf.readInt();
        this.stackSize = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeItemStack(buf, item_to_sale);
        buf.writeInt(this.storage);
        buf.writeInt(this.price);
        buf.writeInt(this.stackSize);
    }

    public static class Handler implements IMessageHandler<PacketSellItem, IMessage> {

        @SideOnly(Side.SERVER)
        public IMessage onMessage(PacketSellItem message, MessageContext ctx) {
            FMLServerHandler.instance().getServer().addScheduledTask(()-> {
                EntityPlayerMP player = ctx.getServerHandler().player;

                if (message.item_to_sale == null) {
                    PacketPopup.sendPopup("Item not found", 0xcb00cb, 0xFFFFFF, player);
                    return;
                }

                if (ItemHelper.amountInInventory(player, message.item_to_sale) < message.storage) {
                    PacketPopup.sendPopup("You do not have enough of that item in your inventory.", 0xcb00cb, 0xFFFFFF, player);
                    return;
                }

                if(message.stackSize > message.storage) {
                    PacketPopup.sendPopup("You must store more than the item's stack size.", 0xcb00cb, 0xFFFFFF, player);
                    return;
                }

                if(message.stackSize > message.item_to_sale.getMaxStackSize()) {
                    PacketPopup.sendPopup("Stack size is larger than the max stack size of the item.", 0xcb00cb, 0xFFFFFF, player);
                    return;
                }

                ItemHelper.removeFromPlayerInventory(player, message.item_to_sale, message.storage);

                message.item_to_sale.setCount(message.stackSize);
                Calendar postingDate = Calendar.getInstance();
                postingDate.add(Calendar.DATE, 7);
                ItemListing listing = new ItemListing(UUID.randomUUID(), player.getUniqueID(), message.price, message.item_to_sale.getDisplayName(), message.item_to_sale.getUnlocalizedName(), postingDate.getTime(), message.item_to_sale, message.storage);
                listing.save();
                player.closeScreen();
            });
            return null;
        }
    }
}
