package com.minelife.economy;

import com.google.common.collect.Lists;
import com.minelife.Minelife;
import com.minelife.economy.packet.PacketUpdateATMGui;
import com.minelife.util.NBTUtil;
import com.minelife.util.NumberConversions;
import com.minelife.util.PlayerHelper;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class Billing {

    public static Bill createBill(int days, double amount, UUID player, String memo, boolean autoPay, BillHandler billHandler) throws Exception
    {
        return new Bill(days, amount, player, memo, autoPay, billHandler);
    }

    public static Bill createBill(int days, double amount, EntityPlayer player, String memo, boolean autoPay, BillHandler billHandler) throws Exception
    {
        return Billing.createBill(days, amount, player.getUniqueID(), memo, autoPay, billHandler);
    }

    public static Bill getBill(UUID uuid)
    {
        try {
            return new Bill(uuid);
        } catch (SQLException | ParseException | IllegalAccessException | InstantiationException | ClassNotFoundException e) {
            if (e instanceof ClassNotFoundException) {
                try {
                    Minelife.SQLITE.query("DELETE FROM Economy_Bills WHERE uuid='" + uuid.toString() + "'");
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
            e.printStackTrace();
        }
        return null;
    }

    public static List<Bill> getBillsForPlayer(UUID uuid)
    {
        List<Bill> billList = Lists.newArrayList();
        try {
            ResultSet result = Minelife.SQLITE.query("SELECT * FROM Economy_Bills WHERE player='" + uuid.toString() + "'");
            while (result.next()) billList.add(Billing.getBill(UUID.fromString(result.getString("uuid"))));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return billList;
    }

    public static void deleteBill(UUID uuid)
    {
        try {
            Bill bill = getBill(uuid);
            if (bill != null && bill.billHandler != null) {
                bill.billHandler.delete();
            }
            Minelife.SQLITE.query("DELETE FROM Economy_Bills WHERE uuid='" + uuid.toString() + "'");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @SideOnly(Side.CLIENT)
    public static void sendPayPacketToServer(UUID bill_UUID, double amount)
    {
        Minelife.NETWORK.sendToServer(new PacketPayBill(bill_UUID, amount));
    }

    @SideOnly(Side.CLIENT)
    public static void sendModifyPacketToServer(Bill bill)
    {
        Minelife.NETWORK.sendToServer(new PacketModifyBill(bill));
    }

    public static class Bill {
        private static DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        private UUID uuid, player;
        public boolean autoPay;
        private Date dueDate;
        private double amount, amountDue;
        private int days;
        private String memo;
        private BillHandler billHandler;

        private Bill()
        {
        }

        private Bill(UUID uuid) throws SQLException, ParseException, ClassNotFoundException, IllegalAccessException, InstantiationException
        {
            ResultSet result = Minelife.SQLITE.query("SELECT * FROM Economy_Bills WHERE uuid='" + uuid.toString() + "'");
            if (!result.next()) throw new SQLException("Not found.");

            this.uuid = uuid;
            this.dueDate = df.parse(result.getString("dueDate"));
            this.amount = result.getDouble("amount");
            this.days = result.getInt("days");
            this.player = UUID.fromString(result.getString("player"));
            this.memo = result.getString("memo");
            this.amountDue = result.getDouble("amountDue");
            this.autoPay = result.getBoolean("autoPay");
            this.billHandler = (BillHandler) Class.forName(result.getString("handler")).newInstance();
            this.billHandler.bill = this;
            this.billHandler.readFromNBT(NBTUtil.fromString(result.getString("tagCompound")));
        }

        private Bill(int days, double amount, UUID player, String memo, boolean autoPay, BillHandler billHandler) throws Exception
        {
            if (days <= 0) throw new Exception("Days cannot be less than 1.");
            if (amount <= 0) throw new Exception("Amount cannot be less than 1.");
            if (player == null) throw new Exception("Player cannot be null.");
            if (memo == null || memo.isEmpty()) throw new Exception("Must have a memo.");
            if (billHandler == null) throw new Exception("Must have a handler.");

            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE, days);
            this.dueDate = calendar.getTime();
            this.amount = amount;
            this.days = days;
            this.player = player;
            this.memo = memo;
            this.amountDue = amount;
            this.autoPay = autoPay;
            this.billHandler = billHandler;
            this.billHandler.bill = this;
            this.billHandler.tagCompound = new NBTTagCompound();
            this.billHandler.writeToNBT(this.billHandler.tagCompound);
            this.uuid = UUID.randomUUID();

            Minelife.SQLITE.query("INSERT INTO Economy_Bills (uuid, dueDate, days, amount, amountDue, player, memo, autoPay, handler, tagCompound) VALUES ('" + this.uuid.toString() + "', " +
                    "'" + df.format(this.dueDate) + "', '" + this.days + "', '" + this.amount + "', '" + this.amountDue + "', '" + this.player.toString() + "', '" + this.memo + "', '" + (this.autoPay ? 1 : 0) + "', '" + this.billHandler.getClass().getName() + "', '" + this.billHandler.tagCompound.toString() + "')");
        }

        public BillHandler getBillHandler()
        {
            return billHandler;
        }

        public UUID getUniqueID()
        {
            return uuid;
        }

        public Date getDueDate()
        {
            return dueDate;
        }

        public void setDueDate(Date dueDate)
        {
            this.dueDate = dueDate;
            writeToDB();
        }

        public double getAmount()
        {
            return amount;
        }

        public void setAmount(double amount)
        {
            this.amount = amount;
            writeToDB();
        }

        public int getDays()
        {
            return days;
        }

        public void setDays(int days)
        {
            this.days = days;
            writeToDB();
        }

        public UUID getPlayer()
        {
            return player;
        }

        public void setPlayer(UUID player)
        {
            this.player = player;
            writeToDB();
        }

        public String getMemo()
        {
            return memo;
        }

        public void setMemo(String memo)
        {
            this.memo = memo;
            writeToDB();
        }

        public double getAmountDue()
        {
            return amountDue;
        }

        public void setAmountDue(double amountDue)
        {
            this.amountDue = amountDue;
            writeToDB();
        }

        public boolean isAutoPay()
        {
            return autoPay;
        }

        public void setAutoPay(boolean autoPay)
        {
            this.autoPay = autoPay;
            writeToDB();
        }

        public boolean isDue()
        {
            return Calendar.getInstance().getTime().after(this.dueDate);
        }

        public void incrementDueDate()
        {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE, days);
            this.dueDate = calendar.getTime();
            writeToDB();
        }

        public String getDueDateAsString()
        {
            return df.format(this.dueDate);
        }

        public void writeToDB()
        {
            try {
                this.billHandler.tagCompound = new NBTTagCompound();
                this.billHandler.writeToNBT(this.billHandler.tagCompound);
                Minelife.SQLITE.query("UPDATE Economy_Bills SET dueDate='" + df.format(this.dueDate) + "', " +
                        "days='" + days + "', amount='" + amount + "', amountDue='" + amountDue + "', " +
                        "player='" + player.toString() + "', memo='" + memo + "', autoPay='" + (autoPay ? 1 : 0) + "', " +
                        "handler='" + billHandler.getClass().getName() + "', " +
                        "tagCompound='" + this.billHandler.tagCompound.toString() + "' WHERE uuid='" + uuid.toString() + "'");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        public void toBytes(ByteBuf buf)
        {
            ByteBufUtils.writeUTF8String(buf, getUniqueID().toString());
            ByteBufUtils.writeUTF8String(buf, df.format(getDueDate()));
            buf.writeDouble(getAmount());
            buf.writeDouble(getAmountDue());
            buf.writeInt(getDays());
            buf.writeBoolean(isAutoPay());
            ByteBufUtils.writeUTF8String(buf, getMemo());
        }

        public static Bill fromBytes(ByteBuf buf)
        {
            Bill bill = new Bill();
            bill.uuid = UUID.fromString(ByteBufUtils.readUTF8String(buf));
            try {
                bill.dueDate = df.parse(ByteBufUtils.readUTF8String(buf));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            bill.amount = buf.readDouble();
            bill.amountDue = buf.readDouble();
            bill.days = buf.readInt();
            bill.autoPay = buf.readBoolean();
            bill.memo = ByteBufUtils.readUTF8String(buf);
            return bill;
        }
    }

    public static class TickHandler {

        private double counter = 0;

        @SubscribeEvent
        public void onServerTick(TickEvent.ServerTickEvent event)
        {
            counter++;

            // every 30 seconds
            if (counter > 600) {
                counter = 0;

                try {
                    ResultSet result = Minelife.SQLITE.query("SELECT * FROM Economy_Bills");
                    while (result.next()) {
                        try {
                            Bill bill = Billing.getBill(UUID.fromString(result.getString("uuid")));
                            if (bill != null) {
                                bill.billHandler.update();
                                if (bill.isDue()) {
                                    bill.setAmountDue(bill.getAmountDue() + bill.getAmount());
                                    if (bill.isAutoPay()) {
                                        bill.billHandler.pay(bill, bill.getAmount());
                                    }
                                    bill.incrementDueDate();
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Minelife.getLogger().log(Level.SEVERE, "", e);
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public static class PacketModifyBill implements IMessage {

        private Bill bill;

        public PacketModifyBill()
        {
        }

        public PacketModifyBill(Bill bill)
        {
            this.bill = bill;
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            bill = Bill.fromBytes(buf);
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            bill.toBytes(buf);
        }

        public static class Handler implements IMessageHandler<PacketModifyBill, IMessage> {

            @SideOnly(Side.SERVER)
            public IMessage onMessage(PacketModifyBill message, MessageContext ctx)
            {
                Bill bill = Billing.getBill(message.bill.getUniqueID());
                if (bill != null) {
                    bill.setAutoPay(message.bill.isAutoPay());
                }
                return null;
            }
        }
    }

    public static class PacketPayBill implements IMessage {

        private UUID bill_UUID;
        private double amount;

        public PacketPayBill()
        {
        }

        public PacketPayBill(UUID bill_UUID, double amount)
        {
            this.bill_UUID = bill_UUID;
            this.amount = amount;
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            bill_UUID = UUID.fromString(ByteBufUtils.readUTF8String(buf));
            amount = buf.readDouble();
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            ByteBufUtils.writeUTF8String(buf, bill_UUID.toString());
            buf.writeDouble(amount);
        }

        public static class Handler implements IMessageHandler<PacketPayBill, IMessage> {

            @SideOnly(Side.SERVER)
            public IMessage onMessage(PacketPayBill message, MessageContext ctx)
            {
                Bill bill = Billing.getBill(message.bill_UUID);

                if (bill != null) {
                    try {
                        if (message.amount <= ModEconomy.getBalance(ctx.getServerHandler().playerEntity.getUniqueID(), false)) {
                            bill.billHandler.pay(bill, message.amount);
                            Minelife.NETWORK.sendTo(new PacketUpdateATMGui("billpay.success"), ctx.getServerHandler().playerEntity);
                        } else {
                            System.out.println("CALLED");
                            Minelife.NETWORK.sendTo(new PacketUpdateATMGui("Insufficient funds."), ctx.getServerHandler().playerEntity);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                return null;
            }
        }
    }
}
