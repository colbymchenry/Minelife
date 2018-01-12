package com.minelife.notification;

import com.minelife.Minelife;
import com.minelife.util.NBTUtil;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public abstract class AbstractNotification {

    protected UUID uniqueID;
    protected UUID playerUniqueID;
    public NBTTagCompound tagCompound = new NBTTagCompound();

    public AbstractNotification() {}

    public AbstractNotification(UUID playerUniqueID)
    {
        this.uniqueID = UUID.randomUUID();
        this.playerUniqueID = playerUniqueID;
    }

    public AbstractNotification(UUID uniqueID, UUID playerUniqueID) throws SQLException
    {
        this.uniqueID = uniqueID;
        this.playerUniqueID = playerUniqueID;
        ResultSet result = Minelife.SQLITE.query("SELECT * FROM notifications WHERE uuid='" + uniqueID.toString() + "' AND player='" + playerUniqueID.toString() + "'");
        if(result.next()) {
            this.playerUniqueID = UUID.fromString(result.getString("player"));
            writeToNBT(tagCompound);
        }
    }

    public abstract void writeToNBT(NBTTagCompound tagCompound);

    public abstract void readFromNBT(NBTTagCompound tagCompound);

    public abstract Class<? extends AbstractGuiNotification> getGuiClass();

    public UUID getUniqueID()
    {
        return uniqueID;
    }

    public UUID getPlayerUniqueID()
    {
        return playerUniqueID;
    }

    @SideOnly(Side.SERVER)
    public void writeToDB() throws SQLException
    {
        writeToNBT(tagCompound);
        ResultSet result = Minelife.SQLITE.query("SELECT * FROM notifications WHERE uuid='" + uniqueID.toString() + "'");
        if (!result.next())
            Minelife.SQLITE.query("INSERT INTO notifications (uuid, player, clazz, tagCompound) VALUES ('" + uniqueID.toString() + "', '" + playerUniqueID.toString() + "', '" + getClass().getName() + "', '" + tagCompound.toString() + "')");
    }

    @SideOnly(Side.SERVER)
    public void readFromDB() throws SQLException
    {
        ResultSet result = Minelife.SQLITE.query("SELECT * FROM notifications WHERE uuid='" + uniqueID.toString() + "'");
        if (result.next()) {
            playerUniqueID = UUID.fromString(result.getString("player"));
            readFromNBT(NBTUtil.fromString(result.getString("tagCompound")));
        }
    }

    public void toBytes(ByteBuf buf)
    {
        ByteBufUtils.writeUTF8String(buf, uniqueID.toString());
        ByteBufUtils.writeUTF8String(buf, playerUniqueID.toString());
        writeToNBT(tagCompound);
        ByteBufUtils.writeTag(buf, tagCompound);
    }

    public void fromBytes(ByteBuf buf)
    {
        uniqueID = UUID.fromString(ByteBufUtils.readUTF8String(buf));
        playerUniqueID = UUID.fromString(ByteBufUtils.readUTF8String(buf));
        tagCompound = ByteBufUtils.readTag(buf);
        readFromNBT(tagCompound);
    }

    @SideOnly(Side.SERVER)
    public void sendTo(EntityPlayerMP player) {
        Minelife.NETWORK.sendTo(new PacketSendNotification(this), player);
    }

}
