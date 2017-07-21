package com.minelife.economy;

import com.google.common.collect.Lists;
import com.minelife.Minelife;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.renderer.texture.ITickable;
import net.minecraft.entity.player.EntityPlayer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class Billing {

    public static Bill createBill(int days, long amount, UUID player, String memo, boolean autoPay) throws Exception
    {
        return new Bill(days, amount, player, memo, autoPay);
    }

    public static Bill createBill(int days, long amount, EntityPlayer player, String memo, boolean autoPay) throws Exception
    {
        return Billing.createBill(days, amount, player.getUniqueID(), memo, autoPay);
    }

    public static Bill getBill(UUID uuid)
    {
        try {
            return new Bill(uuid);
        } catch (SQLException | ParseException e) {
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

    public static class Bill {
        private static DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        private UUID uuid, player;
        private boolean autoPay;
        private Date dueDate;
        private long amount, amountDue;
        private int days;
        private String memo;

        private Bill()
        {
        }

        private Bill(UUID uuid) throws SQLException, ParseException
        {
            ResultSet result = Minelife.SQLITE.query("SELECT * FROM Economy_Bills WHERE uuid='" + uuid.toString() + "'");
            if (!result.next()) throw new SQLException("Not found.");

            this.uuid = uuid;
            this.dueDate = df.parse(result.getString("dueDate"));
            this.amount = result.getLong("amount");
            this.days = result.getInt("days");
            this.player = UUID.fromString(result.getString("player"));
            this.memo = result.getString("memo");
            this.amountDue = result.getLong("amountDue");
            this.autoPay = result.getBoolean("autoPay");
        }

        private Bill(int days, long amount, UUID player, String memo, boolean autoPay) throws Exception
        {
            if (days <= 0) throw new Exception("Days cannot be less than 1.");
            if (amount <= 0) throw new Exception("Amount cannot be less than 1.");
            if (player == null) throw new Exception("Player cannot be null.");
            if (memo == null || memo.isEmpty()) throw new Exception("Must have a memo.");

            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE, days);
            this.dueDate = calendar.getTime();
            this.amount = amount;
            this.days = days;
            this.player = player;
            this.memo = memo;
            this.amountDue = amount;
            this.autoPay = autoPay;

            Minelife.SQLITE.query("INSERT INTO Economy_Bills (uuid, dueDate, days, amount, amountDue, player, memo, autoPay) VALUES ('" + UUID.randomUUID().toString() + "', " +
                    "'" + df.format(this.dueDate) + "', '" + days + "', '" + this.amount + "', '" + amountDue + "', '" + player.toString() + "', '" + memo + "', '" + (autoPay ? 1 : 0) + "')");
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

        public long getAmount()
        {
            return amount;
        }

        public void setAmount(long amount)
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

        public long getAmountDue()
        {
            return amountDue;
        }

        public void setAmountDue(long amountDue)
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

        public void pay(long amount)
        {
            this.amountDue -= amount;
            writeToDB();
        }

        public String getDueDateAsString()
        {
            return df.format(this.dueDate);
        }

        public void writeToDB()
        {
            try {
                Minelife.SQLITE.query("UPDATE Economy_Bills SET dueDate='" + df.format(this.dueDate) + "', days='" + days + "', amount='" + amount + "', player='" + player.toString() + "', memo='" + memo + "', autoPay='" + (autoPay ? 1 : 0) + "' WHERE uuid='" + uuid.toString() + "'");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        public void toBytes(ByteBuf buf)
        {
            ByteBufUtils.writeUTF8String(buf, getUniqueID().toString());
            ByteBufUtils.writeUTF8String(buf, df.format(getDueDate()));
            buf.writeLong(getAmount());
            buf.writeLong(getAmountDue());
            buf.writeInt(getDays());
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
            bill.amount = buf.readLong();
            bill.amountDue = buf.readLong();
            bill.days = buf.readInt();
            bill.memo = ByteBufUtils.readUTF8String(buf);
            return bill;
        }
    }

    public static class TickHandler {

        private long counter = 0;

        @SubscribeEvent
        public void onServerTick(TickEvent.ServerTickEvent event)
        {
            counter++;

            // every 30 seconds
            if (counter > 600) {
                counter = 0;

                try {
                    ResultSet result = Minelife.SQLITE.query("SELECT * FROM Economy_Bills");
                    Date dueDate;
                    while (result.next()) {
                        try {
                            dueDate = Bill.df.parse(result.getString("dueDate"));
                            if (Calendar.getInstance().getTime().after(dueDate)) {
                                Bill bill = Billing.getBill(UUID.fromString(result.getString("uuid")));
                                bill.setAmountDue(bill.getAmountDue() + bill.getAmount());
                                if(bill.autoPay) {
                                    if(ModEconomy.getBalance(bill.getPlayer(), false) >= bill.amount) {
                                        bill.pay(bill.getAmount());
                                        ModEconomy.withdraw(bill.getPlayer(), bill.getAmount(), false);
                                        // TODO: Send notification to player
                                    }
                                }
                                bill.incrementDueDate();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

    }

}
