package com.minelife.police.network;

import com.minelife.MLItems;
import com.minelife.Minelife;
import com.minelife.police.Charge;
import com.minelife.police.ItemTicket;
import com.minelife.police.TicketInventory;
import com.minelife.util.ItemUtil;
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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class PacketWriteTicketToDB implements IMessage {

    private int slot;

    public PacketWriteTicketToDB() {
    }

    public PacketWriteTicketToDB(int slot)
    {
        this.slot = slot;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        slot = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(slot);
    }

    public static class Handler implements IMessageHandler<PacketWriteTicketToDB, IMessage> {

        @SideOnly(Side.SERVER)
        public IMessage onMessage(PacketWriteTicketToDB message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().playerEntity;
            ItemStack ticketStack = player.inventory.getStackInSlot(message.slot);

            if (ticketStack == null || ticketStack.getItem() != MLItems.ticket) {
                Minelife.NETWORK.sendTo(new PacketPopupMessage("Ticket not found.", 0xC6C6C6), player);
                return null;
            }

            UUID playerUUID = ItemTicket.getPlayerForTicket(ticketStack);
            UUID officerUUID = ItemTicket.getOfficerForTicket(ticketStack);

            if(playerUUID == null || officerUUID == null) {
                Minelife.NETWORK.sendTo(new PacketPopupMessage("Incomplete ticket.", 0xC6C6C6), player);
                return null;
            }

            try {
                ResultSet r = Minelife.SQLITE.query("SELECT * FROM policetickets WHERE ticketID='" + ItemTicket.getTicketID(ticketStack) + "'");
                if(r.next()) {
                    Minelife.NETWORK.sendTo(new PacketPopupMessage("There is a ticket with that ID already.", 0xC6C6C6), player);
                    return null;
                }

                Minelife.SQLITE.query("INSERT INTO policetickets (ticketID, playerUUID, officerUUID, ticketNBT) VALUES " +
                        "('" + ItemTicket.getTicketID(ticketStack) + "', '" + playerUUID.toString() + "', '" + officerUUID.toString() + "', " +
                        "'" + ItemUtil.itemToString(ticketStack) + "')");
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }

            player.inventory.setInventorySlotContents(message.slot, null);
            return null;
        }
    }
}
