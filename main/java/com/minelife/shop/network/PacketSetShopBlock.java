package com.minelife.shop.network;

import com.minelife.economy.ModEconomy;
import com.minelife.shop.TileEntityShopBlock;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

public class PacketSetShopBlock implements IMessage {

    private int x,y,z;
    private int amount;
    private ItemStack stack;
    private double price;

    public PacketSetShopBlock() {
    }

    public PacketSetShopBlock(ItemStack stack, int amount, double price, int x, int y, int z) {
        this.stack = stack;
        this.amount = amount;
        this.price = price;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        stack = ByteBufUtils.readItemStack(buf);
        amount = buf.readInt();
        price = buf.readDouble();
        x = buf.readInt();
        y = buf.readInt();
        z = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeItemStack(buf, stack);
        buf.writeInt(amount);
        buf.writeDouble(price);
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
    }

    public static class Handler implements IMessageHandler<PacketSetShopBlock, IMessage> {

        @SideOnly(Side.SERVER)
        public IMessage onMessage(PacketSetShopBlock message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().playerEntity;

            if(player.getEntityWorld().getTileEntity(message.x, message.y, message.z) == null) {
                player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.RED + "Could not find shop block."));
                return null;
            }

            if(!(player.getEntityWorld().getTileEntity(message.x, message.y, message.z) instanceof TileEntityShopBlock)) {
                player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.RED + "Could not find shop block."));
                return null;
            }

            TileEntityShopBlock shopBlock = (TileEntityShopBlock) player.getEntityWorld().getTileEntity(message.x, message.y, message.z);
            if(shopBlock.getOwner() == null) {
                shopBlock.setOwner(player.getUniqueID());
            } else if(!shopBlock.getOwner().equals(player.getUniqueID())) {
                player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.RED + "You are not the owner of this shop block!"));
                return null;
            }

            if(message.stack == null) {
                player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.RED + "Item not found."));
                return null;
            }

            if(!ModEconomy.isValidAmount(String.valueOf(message.price))) {
                player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.RED + "Invalid price."));
                return null;
            }

            if(message.amount < 1) {
                player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.RED + "Amount cannot be less than 1."));
                return null;
            }

            if(message.amount > 64) {
                player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.RED + "Amount cannot be greater than 64."));
                return null;
            }

            ItemStack toSale = message.stack.copy();
            toSale.stackSize = message.amount;
            shopBlock.setStackToSale(toSale);
            shopBlock.setPrice(message.price);
            player.closeScreen();
            player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.GREEN + "Shop block updated!"));
            return null;
        }
    }

}
