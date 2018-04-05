package com.minelife.economy.network;

import com.minelife.Minelife;
import com.minelife.economy.Bill;
import com.minelife.economy.BillEvent;
import com.minelife.economy.ModEconomy;
import com.minelife.realestate.BillHandler;
import com.minelife.util.client.PacketPopup;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.server.FMLServerHandler;

import java.util.UUID;

public class PacketPayBill implements IMessage {

    private UUID billID;
    private int amount;

    public PacketPayBill() {
    }

    public PacketPayBill(UUID billID, int amount) {
        this.billID = billID;
        this.amount = amount;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        billID = UUID.fromString(ByteBufUtils.readUTF8String(buf));
        amount = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, billID.toString());
        buf.writeInt(amount);
    }

    public static class Handler implements IMessageHandler<PacketPayBill, IMessage> {

        @SideOnly(Side.SERVER)
        public IMessage onMessage(PacketPayBill message, MessageContext ctx) {
            FMLServerHandler.instance().getServer().addScheduledTask(() -> {
                EntityPlayerMP player = ctx.getServerHandler().player;

                try {
                    if(ModEconomy.getBalanceATM(player.getUniqueID()) < message.amount) {
                        PacketPopup.sendPopup("Insufficient funds.", player);
                        return;
                    }

                    Bill bill = new Bill(message.billID);
                    BillEvent billEvent = new BillEvent.PayEvent(bill, player, message.amount);
                    MinecraftForge.EVENT_BUS.post(billEvent);
                } catch (Exception e) {
                    e.printStackTrace();
                    PacketPopup.sendPopup("Bill does not exist.", ctx.getServerHandler().player);
                }

                Minelife.getNetwork().sendTo(new PacketSendBills(ModEconomy.getBalanceATM(player.getUniqueID()),
                        BillHandler.getRentBills(player.getUniqueID())), player);
            });

            return null;
        }
    }
}
