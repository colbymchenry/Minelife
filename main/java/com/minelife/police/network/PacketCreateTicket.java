package com.minelife.police.network;

import com.google.common.collect.Lists;
import com.minelife.MLItems;
import com.minelife.Minelife;
import com.minelife.police.Charge;
import com.minelife.police.ItemTicket;
import com.minelife.util.client.PacketPopupMessage;
import com.minelife.util.server.UUIDFetcher;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

import java.util.List;
import java.util.UUID;

public class PacketCreateTicket implements IMessage {

    private int slot;
    private List<Charge> chargeList;
    private String player;
    private boolean closeScreen;

    public PacketCreateTicket() {
    }

    public PacketCreateTicket(int slot, List<Charge> chargeList, String player, boolean closeScreen) {
        this.slot = slot;
        this.chargeList = chargeList;
        this.player = player;
        this.closeScreen = closeScreen;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        slot = buf.readInt();
        player = ByteBufUtils.readUTF8String(buf);
        chargeList = Lists.newArrayList();
        closeScreen = buf.readBoolean();

        int chargesSize = buf.readInt();
        for (int i = 0; i < chargesSize; i++) chargeList.add(Charge.fromString(ByteBufUtils.readUTF8String(buf)));
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(slot);
        ByteBufUtils.writeUTF8String(buf, player);
        buf.writeBoolean(closeScreen);
        buf.writeInt(chargeList.size());
        for (Charge charge : chargeList) ByteBufUtils.writeUTF8String(buf, charge.toString());
    }

    public static class Handler implements IMessageHandler<PacketCreateTicket, IMessage> {

        @SideOnly(Side.SERVER)
        public IMessage onMessage(PacketCreateTicket message, MessageContext ctx) {
            EntityPlayerMP sender = ctx.getServerHandler().playerEntity;
            ItemStack ticketStack = sender.inventory.getStackInSlot(message.slot);
            if (ticketStack == null || ticketStack.getItem() != MLItems.ticket) {
                sender.closeScreen();
                Minelife.NETWORK.sendTo(new PacketPopupMessage("Could not find ticket.", 0x003F7E), sender);
                return null;
            }

            UUID playerUUID = UUIDFetcher.get(message.player);

            if (playerUUID == null) {
                Minelife.NETWORK.sendTo(new PacketPopupMessage("Player not found.", 0x003F7E), sender);
                return null;
            }

            ItemTicket.setPlayerForTicket(ticketStack, playerUUID);
            ItemTicket.setOfficerForTicket(ticketStack, sender.getUniqueID());
            ItemTicket.setChargesForTicket(ticketStack, message.chargeList);
            sender.inventory.setInventorySlotContents(message.slot, ticketStack);
            if (message.closeScreen) sender.closeScreen();
            return null;
        }

    }
}
