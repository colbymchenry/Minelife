package com.minelife.minebay.network;

import com.google.common.collect.Lists;
import com.minelife.Minelife;
import com.minelife.economy.ModEconomy;
import com.minelife.minebay.ItemListing;
import com.minelife.minebay.ModMinebay;
import com.minelife.notifications.Notification;
import com.minelife.notifications.NotificationType;
import com.minelife.util.ItemHelper;
import com.minelife.util.NumberConversions;
import com.minelife.util.PlayerHelper;
import com.minelife.util.client.PacketPopup;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.server.FMLServerHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class PacketBuyItem implements IMessage {

    private int amount;
    private UUID listingID;

    public PacketBuyItem() {
    }

    public PacketBuyItem(int amount, UUID listingID) {
        this.amount = amount;
        this.listingID = listingID;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.amount = buf.readInt();
        this.listingID = UUID.fromString(ByteBufUtils.readUTF8String(buf));
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.amount);
        ByteBufUtils.writeUTF8String(buf, this.listingID.toString());
    }

    public static class Handler implements IMessageHandler<PacketBuyItem, IMessage> {

        @SideOnly(Side.SERVER)
        public IMessage onMessage(PacketBuyItem message, MessageContext ctx) {
            FMLServerHandler.instance().getServer().addScheduledTask(() -> {
                try {
                    EntityPlayerMP player = ctx.getServerHandler().player;
                    ResultSet result = ModMinebay.getDatabase().query("SELECT * FROM items WHERE uuid='" + message.listingID.toString() + "'");
                    if (!result.next()) {
                        player.closeScreen();
                        PacketPopup.sendPopup("Listing is no longer available.", 0xcb00cb, 0xFFFFFF, player);
                        return;
                    }

                    ItemListing listing = new ItemListing(result);
                    if (listing == null) {
                        player.closeScreen();
                        PacketPopup.sendPopup("Listing is no longer available.", 0xcb00cb, 0xFFFFFF, player);
                        return;
                    }

                    if (listing.getSellerID().equals(player.getUniqueID())) {
                        PacketPopup.sendPopup("You cannot buy from yourself.", 0xcb00cb, 0xFFFFFF, player);
                        return;
                    }

                    int totalCost = message.amount * listing.getPrice();
                    int totalToBuy = message.amount * listing.getItemStack().getCount();

                    if (totalToBuy > listing.getAmountStored()) {
                        PacketPopup.sendPopup("There is not enough in storage.", 0xcb00cb, 0xFFFFFF, player);
                        return;
                    }

                    if (totalCost > ModEconomy.getBalanceCashPiles(player.getUniqueID())) {
                        PacketPopup.sendPopup("Insufficient funds in cash piles.", 0xcb00cb, 0xFFFFFF, player);
                        return;
                    }

                    int fullStacks = totalToBuy / listing.getItemStack().getMaxStackSize();
                    int leftOver = totalToBuy % listing.getItemStack().getMaxStackSize();

                    List<ItemStack> toDrop = Lists.newArrayList();

                    for (int i = 0; i < fullStacks; i++) {
                        ItemStack stack = listing.getItemStack().copy();
                        stack.setCount(listing.getItemStack().getMaxStackSize());
                        toDrop.add(stack);
                    }

                    if (leftOver > 0) {
                        ItemStack leftOverStack = listing.getItemStack().copy();
                        leftOverStack.setCount(leftOver);
                        toDrop.add(leftOverStack);
                    }

                    for (ItemStack stack : toDrop) {
                        EntityItem entity_item = player.dropItem(stack, false);
                        entity_item.setPickupDelay(0);
                    }

                    int didNotFitWithdraw = ModEconomy.withdrawCashPiles(player.getUniqueID(), totalCost);
                    int didNotFitDeposit = ModEconomy.depositCashPiles(listing.getSellerID(), totalCost);

                    if (didNotFitDeposit > 0) ModEconomy.depositATM(listing.getSellerID(), didNotFitDeposit);
                    if (didNotFitWithdraw > 0) ModEconomy.depositATM(player.getUniqueID(), didNotFitWithdraw);


                    // TODO: Allow enabling and disabling notifications for a listing when creating it
                    Notification itemSoldNotification = new Notification(listing.getSellerID(),
                            TextFormatting.DARK_PURPLE + "Minebay\n" + TextFormatting.DARK_GRAY + totalToBuy + " " +
                                    listing.getItemStack().getDisplayName() + "'s Sold!\nIncome: " + TextFormatting.DARK_GREEN + "$" + NumberConversions.format(totalCost),
                            new ResourceLocation(Minelife.MOD_ID, "textures/gui/notification/minebay_icon.png"), NotificationType.EDGED, 5, 0xFFFFFF);

                    EntityPlayerMP seller = PlayerHelper.getPlayer(listing.getSellerID());

                    if (listing.getAmountStored() - totalToBuy < 1) {
                        listing.delete();
                    } else {
                        listing.setAmountStored(listing.getAmountStored() - totalToBuy);
                    }

                    if(seller != null) {
                        itemSoldNotification.sendTo(seller, true, true, false);
                    } else {
                        itemSoldNotification.save();
                    }

                    player.closeScreen();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
            return null;
        }

    }
}
