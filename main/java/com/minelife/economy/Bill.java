package com.minelife.economy;

import com.minelife.util.DateHelper;
import com.minelife.util.NBTHelper;
import io.netty.buffer.ByteBuf;
import lib.PatPeter.SQLibrary.Database;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.network.ByteBufUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.UUID;

public final class Bill implements Comparable<Bill> {

    private NBTTagCompound tagCompound;
    private String memo;
    private UUID uniqueID;
    private UUID player;
    private int amountDue;
    private Date dueDate;

    public Bill(UUID uniqueID, UUID player, String memo, int amountDue, Date dueDate, NBTTagCompound tagCompound) {
        this.uniqueID = uniqueID;
        this.player = player;
        this.memo = memo;
        this.amountDue = amountDue;
        this.dueDate = dueDate;
        this.tagCompound = tagCompound;
    }

    public Bill(UUID uniqueID) throws Exception {
        this.uniqueID = uniqueID;
        ResultSet result = ModEconomy.getDatabase().query("SELECT * FROM bills WHERE uuid='" + uniqueID.toString() + "'");
        if(!result.next()) throw new Exception("Bill not found.");
        this.player = UUID.fromString(result.getString("player"));
        this.memo = result.getString("memo");
        this.amountDue = result.getInt("amountDue");
        this.dueDate = DateHelper.stringToDate(result.getString("dueDate"));
        this.tagCompound = NBTHelper.fromString(result.getString("tagCompound"));
    }

    public NBTTagCompound getTagCompound() {
        return this.tagCompound;
    }

    public UUID getPlayer() {
        return this.player;
    }

    public void setPlayer(UUID player) {
        this.player = player;
    }

    public int getAmountDue() {
        return this.amountDue;
    }

    public void setAmountDue(int amountDue) {
        this.amountDue = amountDue;
    }

    public Date getDueDate() {
        return this.dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public UUID getUniqueID() {
        return this.uniqueID;
    }

    public String getMemo() {
        return this.memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public void save() throws SQLException {
        ResultSet result = ModEconomy.getDatabase().query("SELECT * FROM bills WHERE uuid='" + this.getUniqueID().toString() + "'");
        if(result.next()) {
            ModEconomy.getDatabase().query("UPDATE bills SET uuid='', player='" + this.getPlayer().toString() + "', " +
                    "memo='" + this.getMemo() + "', amountDue='" + this.getAmountDue() + "', " +
                    "dueDate='" + DateHelper.dateToString(this.getDueDate()) + "', " +
                    "tagCompound='" + this.getTagCompound().toString() + "' WHERE uuid='" + this.getUniqueID().toString() + "'");
        } else {
            ModEconomy.getDatabase().query("INSERT INTO bills (uuid, player, memo, amountDue, dueDate, tagCompound) VALUES " +
                    "('" + this.getUniqueID().toString() + "', '" + this.getPlayer().toString() + "', " +
                    "'" + this.getMemo() + "', '" + this.getAmountDue() + "', " +
                    "'" + DateHelper.dateToString(this.getDueDate()) + "', '" + this.getTagCompound().toString() + "')");
        }
    }

    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, this.getUniqueID().toString());
        ByteBufUtils.writeUTF8String(buf, this.getPlayer().toString());
        ByteBufUtils.writeUTF8String(buf, this.getMemo());
        buf.writeInt(this.getAmountDue());
        ByteBufUtils.writeUTF8String(buf, DateHelper.dateToString(this.getDueDate()));
        ByteBufUtils.writeTag(buf, this.getTagCompound());
    }

    public static Bill fromBytes(ByteBuf buf) {
        UUID uuid = UUID.fromString(ByteBufUtils.readUTF8String(buf));
        UUID player = UUID.fromString(ByteBufUtils.readUTF8String(buf));
        String memo = ByteBufUtils.readUTF8String(buf);
        int amountDue = buf.readInt();
        Date dueDate = DateHelper.stringToDate(ByteBufUtils.readUTF8String(buf));
        NBTTagCompound tagCompound = ByteBufUtils.readTag(buf);
        return new Bill(uuid, player, memo, amountDue, dueDate, tagCompound);
    }

    @Override
    public int compareTo(Bill o) {
        return o.getUniqueID().compareTo(this.getUniqueID());
    }
}
