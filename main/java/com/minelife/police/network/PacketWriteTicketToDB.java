package com.minelife.police.network;

import com.minelife.MLItems;
import com.minelife.Minelife;
import com.minelife.police.Charge;
import com.minelife.police.ItemTicket;
import com.minelife.police.TicketInventory;
import com.minelife.util.ListToString;
import com.minelife.util.client.PacketPopupMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class PacketWriteTicketToDB implements IMessage {

    public PacketWriteTicketToDB() {
    }

    @Override
    public void fromBytes(ByteBuf buf) {

    }

    @Override
    public void toBytes(ByteBuf buf) {

    }

    public static class Handler implements IMessageHandler<PacketWriteTicketToDB, IMessage> {

        @SideOnly(Side.SERVER)
        public IMessage onMessage(PacketWriteTicketToDB message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().playerEntity;
            ItemStack ticketStack = player.inventory.getStackInSlot(player.inventory.currentItem);

            if (ticketStack == null || ticketStack.getItem() != MLItems.ticket) {
                Minelife.NETWORK.sendTo(new PacketPopupMessage("Ticket not found.", 0xC6C6C6), player);
                return null;
            }

            UUID playerUUID = ItemTicket.getPlayerForTicket(ticketStack);
            UUID officerUUID = ItemTicket.getOfficerForTicket(ticketStack);
            List<Charge> chargeList = ItemTicket.getChargesForTicket(ticketStack);
            TicketInventory ticketInventory = new TicketInventory(ticketStack, player.inventory.currentItem);

            if(playerUUID == null || officerUUID == null) {
                Minelife.NETWORK.sendTo(new PacketPopupMessage("Incomplete ticket.", 0xC6C6C6), player);
                return null;
            }

            ListToString<Charge> listToString = new ListToString<Charge>(chargeList) {
                @Override
                public String toString(Charge o) {
                    return o.toString();
                }
            };

            try {
                Minelife.SQLITE.query("INSERT INTO policetickets (ticketID, playerUUID, officerUUID, charges, inventory) VALUES " +
                        "('" + ItemTicket.getTicketID(ticketStack) + "', '" + playerUUID.toString() + "', '" + officerUUID.toString() + "', " +
                        "'" + listToString.getListAsString() + "', '" + ticketInventory.getInventoryAsString() + "')");
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }

            player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
            return null;
        }
    }
}
