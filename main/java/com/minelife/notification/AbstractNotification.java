package com.minelife.notification;

import com.minelife.Minelife;
import com.minelife.util.NBTUtil;
import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public abstract class AbstractNotification {

    private UUID uniqueID;
    public NBTTagCompound tagCompound = new NBTTagCompound();

    public AbstractNotification()
    {
    }

    public AbstractNotification(UUID uniqueID)
    {
        this.uniqueID = uniqueID;
        writeToNBT(tagCompound);
    }

    public abstract void writeToNBT(NBTTagCompound tagCompound);

    public abstract void readFromNBT(NBTTagCompound tagCompound);

    public abstract Class<? extends AbstractGuiNotification> getGuiClass();

    public UUID getUniqueID()
    {
        return uniqueID;
    }

    public void writeToDB() throws SQLException
    {
        writeToNBT(tagCompound);
        ResultSet result = Minelife.SQLITE.query("SELECT * FROM notifications WHERE uuid='" + uniqueID.toString() + "'");
        if (!result.next())
            Minelife.SQLITE.query("INSERT INTO notifications (uuid, tagCompound) VALUES ('" + uniqueID.toString() + "', '" + tagCompound.toString() + "')");
    }

    public void readFromDB() throws SQLException
    {
        ResultSet result = Minelife.SQLITE.query("SELECT * FROM notifications WHERE uuid='" + uniqueID.toString() + "'");
        if (result.next()) readFromNBT(NBTUtil.fromString(result.getString("tagCompound")));
    }

    public void toBytes(ByteBuf buf)
    {
        writeToNBT(tagCompound);
        ByteBufUtils.writeUTF8String(buf, uniqueID.toString());
        ByteBufUtils.writeTag(buf, tagCompound);
    }

    public void fromBytes(ByteBuf buf)
    {
        uniqueID = UUID.fromString(ByteBufUtils.readUTF8String(buf));
        tagCompound = ByteBufUtils.readTag(buf);
        readFromNBT(tagCompound);
    }

}
