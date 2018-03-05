package com.minelife.minebay.packet;

import codechicken.lib.inventory.InventoryUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.minelife.Minelife;
import com.minelife.minebay.ItemListing;
import com.minelife.minebay.ModMinebay;
import com.minelife.util.PlayerHelper;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class PacketSellItem implements IMessage {

    private ItemStack item_to_sale;
    private int amount;
    private int price;

    public PacketSellItem() {
    }

    public PacketSellItem(ItemStack item_to_sale, int amount, int price) {
        this.item_to_sale = item_to_sale;
        this.amount = amount;
        this.price = price;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.item_to_sale = ByteBufUtils.readItemStack(buf);
        this.amount = buf.readInt();
        this.price = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeItemStack(buf, item_to_sale);
        buf.writeInt(this.amount);
        buf.writeInt(this.price);
    }

    public static class Handler implements IMessageHandler<PacketSellItem, IMessage> {

        @SideOnly(Side.SERVER)
        public IMessage onMessage(PacketSellItem message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().playerEntity;

            if (message.item_to_sale == null) {
                PacketPopupMsg.send("Item not found.", player);
                return null;
            }

            Map<Integer, ItemStack> stacks = getTotalStacks(player, message.item_to_sale);
            int totalCount = 0;
            for (ItemStack stack : stacks.values()) totalCount += stack.stackSize;

            if (totalCount < message.amount) {
                PacketPopupMsg.send("You do not have enough of that item in your inventory.", player);
                return null;
            }

            Set<Integer> slotsToRemove = Sets.newTreeSet();
            int slotToDecrease = -1;
            int totalRemoved = message.amount;
            for (Integer slotID : stacks.keySet()) {
                ItemStack stack = stacks.get(slotID);

                totalRemoved -= stack.stackSize;

                if (totalRemoved >= 0) {
                    // remove item
                    slotsToRemove.add(slotID);
                } else {
                    // decrease stack size
                    slotToDecrease = slotID;
                    break;
                }
            }

            slotsToRemove.forEach(slot -> player.inventory.mainInventory[slot] = null);

            if(slotToDecrease != -1) {
                ItemStack lastStack = player.inventory.mainInventory[slotToDecrease].copy();
                lastStack.stackSize = Math.abs(totalRemoved);
                player.inventory.mainInventory[slotToDecrease] = lastStack;
            }
            player.inventoryContainer.detectAndSendChanges();

            ItemListing listing = new ItemListing(UUID.randomUUID(), player.getUniqueID(), message.price, message.amount, message.item_to_sale);
            listing.write_to_db();
            player.closeScreen();
            return null;
        }

        private Map<Integer, ItemStack> getTotalStacks(EntityPlayerMP player, ItemStack item_to_sale) {
            Map<Integer, ItemStack> itemStackMap = Maps.newHashMap();
            for (int i = 0; i < player.inventory.mainInventory.length; i++) {
                ItemStack stack = player.inventory.mainInventory[i];
                if (stack != null && ModMinebay.areStacksIdentical(stack, item_to_sale)) itemStackMap.put(i, stack);
            }
            return itemStackMap;
        }
    }

}
