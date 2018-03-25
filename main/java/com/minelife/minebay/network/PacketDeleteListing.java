package com.minelife.minebay.network;

import com.google.common.collect.Lists;
import com.minelife.minebay.ItemListing;
import com.minelife.minebay.ModMinebay;
import com.minelife.util.client.PacketPopup;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.swing.text.html.parser.Entity;
import java.sql.ResultSet;
import java.util.List;
import java.util.UUID;

public class PacketDeleteListing implements IMessage {

    private UUID listingID;

    public PacketDeleteListing() {
    }

    public PacketDeleteListing(UUID listingID) {
        this.listingID = listingID;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.listingID = UUID.fromString(ByteBufUtils.readUTF8String(buf));
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, this.listingID.toString());
    }

    public static class Handler implements IMessageHandler<PacketDeleteListing, IMessage> {

        @SideOnly(Side.SERVER)
        public IMessage onMessage(PacketDeleteListing message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;

            try {
                ResultSet result = ModMinebay.getDatabase().query("SELECT * FROM items WHERE uuid='" + message.listingID.toString() + "'");
                if (!result.next()) {
                    player.closeScreen();
                    PacketPopup.sendPopup("Listing is no longer available.", 0xcb00cb, 0xFFFFFF, player);
                    return null;
                }

                ItemListing listing = new ItemListing(result);
                if (listing == null) {
                    player.closeScreen();
                    PacketPopup.sendPopup("Listing is no longer available.", 0xcb00cb, 0xFFFFFF, player);
                    return null;
                }

                if (!listing.getSellerID().equals(player.getUniqueID())) {
                    PacketPopup.sendPopup("You cannot delete that listing.", 0xcb00cb, 0xFFFFFF, player);
                    return null;
                }

                int fullStacks = listing.getAmountStored() / listing.getItemStack().getMaxStackSize();
                int leftOver = listing.getAmountStored()  % listing.getItemStack().getMaxStackSize();

                List<ItemStack> toDrop = Lists.newArrayList();

                for (int i = 0; i < fullStacks; i++) {
                    ItemStack stack = listing.getItemStack().copy();
                    stack.setCount(listing.getItemStack().getMaxStackSize());
                    toDrop.add(stack);
                }

                if(leftOver > 0) {
                    ItemStack leftOverStack = listing.getItemStack().copy();
                    leftOverStack.setCount(leftOver);
                    toDrop.add(leftOverStack);
                }

                for (ItemStack stack : toDrop) {
                    EntityItem entityItem = new EntityItem(player.world, player.posX, player.posY, player.posZ, stack);
                    entityItem.setPickupDelay(0);
                    player.dropItemAndGetStack(entityItem);
                }

                listing.delete();
                player.closeScreen();
                player.sendMessage(new TextComponentString(TextFormatting.LIGHT_PURPLE + "[Minebay] " + TextFormatting.GOLD + "Listing deleted."));
            }catch(Exception e) {

            }
            return null;
        }

    }

}
