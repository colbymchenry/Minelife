package com.minelife.realestate.network;

import com.minelife.Minelife;
import com.minelife.economy.Billing;
import com.minelife.economy.ModEconomy;
import com.minelife.economy.MoneyHandler;
import com.minelife.realestate.Estate;
import com.minelife.realestate.EstateHandler;
import com.minelife.realestate.PaymentNotification;
import com.minelife.realestate.RentBillHandler;
import com.minelife.util.PlayerHelper;
import com.minelife.util.client.PacketPopupMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;

public class PacketPurchaseEstate implements IMessage {

    private boolean renting;
    private int estateID;

    public PacketPurchaseEstate(int estateID, boolean renting) {
        this.estateID = estateID;
        this.renting = renting;
    }

    public PacketPurchaseEstate() {
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        estateID = buf.readInt();
        renting = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(estateID);
        buf.writeBoolean(renting);
    }

    public static class Handler implements IMessageHandler<PacketPurchaseEstate, IMessage> {

        @SideOnly(Side.SERVER)
        public IMessage onMessage(PacketPurchaseEstate message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().playerEntity;
            try {
                Estate estate = EstateHandler.getEstate(message.estateID);

                if (estate == null) return null;

                if (message.renting && !estate.isForRent()) {
                    Minelife.NETWORK.sendTo(new PacketPopupMessage("Estate not for rent", 0xC6C6C6), player);
                    return null;
                } else if (!message.renting && !estate.isPurchasable()) {
                    Minelife.NETWORK.sendTo(new PacketPopupMessage("Estate not for purchase", 0xC6C6C6), player);
                    return null;
                }

                int playerBalance = MoneyHandler.getBalanceInventory(player);

                if ((message.renting && playerBalance < estate.getRentPrice()) ||
                        (!message.renting && playerBalance < estate.getPurchasePrice())) {
                    Minelife.NETWORK.sendTo(new PacketPopupMessage("Insufficient funds", 0xC6C6C6), player);
                    return null;
                }

                if (message.renting) {
                    if(playerBalance < estate.getRentPrice()) {
                        Minelife.NETWORK.sendTo(new PacketPopupMessage("Insufficient funds", 0xC6C6C6), player);
                        return null;
                    }
                    estate.setRenter(player.getUniqueID());
                    RentBillHandler billHandler = new RentBillHandler();
                    billHandler.estateID = estate.getID();
                    Billing.createBill(estate.getRentPeriod(), estate.getRentPrice(), player.getUniqueID(),
                            "Estate Rent: #" + estate.getID(), true, billHandler);
                    MoneyHandler.takeMoneyInventory(player, estate.getRentPrice());
                    MoneyHandler.addMoneyVault(estate.getOwner(), estate.getRentPrice());
                    estate.setBill(billHandler);
                } else {
                    MoneyHandler.takeMoneyInventory(player, estate.getPurchasePrice());
                    MoneyHandler.addMoneyVault(estate.getOwner(), estate.getPurchasePrice());
                }

                PaymentNotification notification = new PaymentNotification(estate.getOwner(), message.renting ? estate.getRentPrice() : estate.getPurchasePrice(), estate.getID(), message.renting);
                if (PlayerHelper.getPlayer(estate.getOwner()) != null)
                    notification.sendTo(PlayerHelper.getPlayer(estate.getOwner()));
                else
                    notification.writeToDB();


                if (!message.renting) {
                    estate.setOwner(player.getUniqueID());
                    estate.setPurchasePrice(-1);
                }

            } catch (Exception e) {
                e.printStackTrace();
                player.addChatComponentMessage(new ChatComponentText(Minelife.default_error_message));
            }

            return null;
        }
    }
}
