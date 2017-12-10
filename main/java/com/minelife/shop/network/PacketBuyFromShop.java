package com.minelife.shop.network;

import com.google.common.collect.Maps;
import com.minelife.Minelife;
import com.minelife.economy.ModEconomy;
import com.minelife.shop.TileEntityShopBlock;
import com.minelife.util.NumberConversions;
import com.minelife.util.client.PacketPopupMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import java.util.List;
import java.util.Map;

public class PacketBuyFromShop implements IMessage {

    private int x, y, z, amount;

    public PacketBuyFromShop(int x, int y, int z, int amount) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.amount = amount;
    }

    public PacketBuyFromShop() {
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        x = buf.readInt();
        y = buf.readInt();
        z = buf.readInt();
        amount = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
        buf.writeInt(amount);
    }

    public static class Handler implements IMessageHandler<PacketBuyFromShop, IMessage> {

        @SideOnly(Side.SERVER)
        public IMessage onMessage(PacketBuyFromShop message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().playerEntity;

            if(player.worldObj.getTileEntity(message.x, message.y, message.z) == null) {
                player.closeScreen();
                player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.RED + "Could not find shop block."));
                return null;
            }

            if(!(player.worldObj.getTileEntity(message.x, message.y, message.z) instanceof TileEntityShopBlock)) {
                player.closeScreen();
                player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.RED + "Could not find shop block."));
                return null;
            }

            if(message.amount > ((TileEntityShopBlock) player.worldObj.getTileEntity(message.x, message.y, message.z)).getStackToSale().stackSize) {
                player.closeScreen();
                player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.RED + "Stack size too large."));
                return null;
            }

            if(message.amount == 0) {
                player.closeScreen();
                player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.RED + "Stack size cannot be 0."));
                return null;
            }

            doTransaction((TileEntityShopBlock) player.worldObj.getTileEntity(message.x, message.y, message.z), message.amount, player);
            return null;
        }

        @SideOnly(Side.SERVER)
        public void doTransaction(TileEntityShopBlock tile, int amountToBuy, EntityPlayerMP player) {
            List<TileEntityShopBlock.Stock> stock = tile.getStock();
            Map<TileEntityShopBlock.Stock, Integer> toRemove = Maps.newHashMap();
            ItemStack toSale = tile.getStackToSale().copy();

            int amount = 0;
            for (TileEntityShopBlock.Stock s : stock) {
                ItemStack stack = s.chest.getStackInSlot(s.slot);
                if(stack.stackSize >= amountToBuy) {
                    toRemove.put(s, stack.stackSize - amountToBuy);
                    amount = amountToBuy;
                    break;
                } else {
                    toRemove.put(s, 0);
                    amount += stack.stackSize;
                }

                if (amount >= amountToBuy) {
                    amount = amountToBuy;
                    break;
                }
            }

            if(amount == 0) {
                Minelife.NETWORK.sendTo(new PacketPopupMessage(EnumChatFormatting.RED + "Out of stock."), player);
                return;
            }

            try {
                toRemove.forEach((stock1, stackSize) -> {
                    if(stackSize <= 0) {
                        stock1.chest.setInventorySlotContents(stock1.slot, null);
                    } else {
                        ItemStack s = stock1.chest.getStackInSlot(stock1.slot);
                        s.stackSize = stackSize;
                        stock1.chest.setInventorySlotContents(stock1.slot, s);
                    }
                });

                ModEconomy.withdraw(player.getUniqueID(), tile.getPrice() * ((double) amount / (double) toSale.stackSize), true);
                ModEconomy.deposit(tile.getOwner(), tile.getPrice() * ((double) amount / (double) toSale.stackSize), false);

                ItemStack toDrop = toSale.copy();
                toDrop.stackSize = amount;
                EntityItem entity_item = player.dropPlayerItemWithRandomChoice(toDrop, false);
                entity_item.delayBeforeCanPickup = 0;

                player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.RED + "-$" + NumberConversions.formatter.format(tile.getPrice() * ((double) amount / (double) toSale.stackSize))));
            } catch (Exception e) {
                e.printStackTrace();
                player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.RED + Minelife.default_error_message));
            }
        }

    }
}
