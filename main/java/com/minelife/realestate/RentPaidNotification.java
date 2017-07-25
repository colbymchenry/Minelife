package com.minelife.realestate;

import com.minelife.notification.AbstractGuiNotification;
import com.minelife.notification.AbstractNotification;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;

import java.sql.SQLException;
import java.util.UUID;

public class RentPaidNotification extends AbstractNotification {

    private String title;
    private String message;
    private UUID zoneUUID;
    private int x, y, z;

    public RentPaidNotification()
    {
    }

    public RentPaidNotification(UUID playerUniqueID, String title, String message, UUID zoneUUID, int x, int y, int z) throws SQLException
    {
        super(playerUniqueID);
        setTitle(title);
        setMessage(message);
        setZoneUUID(zoneUUID);
        setX(x);
        setY(y);
        setZ(z);
    }

    @Override
    public void writeToNBT(NBTTagCompound tagCompound)
    {
        tagCompound.setString("title", title);
        tagCompound.setString("message", message);
        tagCompound.setString("zoneUUID", zoneUUID.toString());
        tagCompound.setInteger("x", x);
        tagCompound.setInteger("y", y);
        tagCompound.setInteger("z", z);
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompound)
    {
        title = tagCompound.getString("title");
        message = tagCompound.getString("message");
        zoneUUID = UUID.fromString(tagCompound.getString("zoneUUID"));
        x = tagCompound.getInteger("x");
        y = tagCompound.getInteger("y");
        z = tagCompound.getInteger("z");
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public String getTitle()
    {
        return title;
    }

    public String getMessage()
    {
        return message;
    }

    public void setZoneUUID(UUID zoneUUID)
    {
        this.zoneUUID = zoneUUID;
    }

    public UUID getZoneUUID()
    {
        return zoneUUID;
    }

    public void setX(int x)
    {
        this.x = x;
    }

    public int getX()
    {
        return x;
    }

    public void setY(int y)
    {
        this.y = y;
    }

    public int getY()
    {
        return y;
    }

    public void setZ(int z)
    {
        this.z = z;
    }

    public int getZ()
    {
        return z;
    }

    @Override
    public Class<? extends AbstractGuiNotification> getGuiClass()
    {
        return GuiRentPaidNotification.class;
    }

    public static class GuiRentPaidNotification extends AbstractGuiNotification {

        private RentPaidNotification rentPaidNotification;

        public GuiRentPaidNotification(AbstractNotification abstractNotification)
        {
            super(abstractNotification);
            this.rentPaidNotification = (RentPaidNotification) abstractNotification;
        }

        @Override
        public String getSound()
        {
            return "cha_ching";
        }

        @Override
        protected void drawForeground()
        {
            int height = 5;
            int xOffset = 7;
            fontRenderer.drawString(EnumChatFormatting.YELLOW.toString() + EnumChatFormatting.UNDERLINE.toString() + rentPaidNotification.getTitle(), xOffset, height, 0xFFFFFF);
            height += fontRenderer.FONT_HEIGHT + 2;
            fontRenderer.drawSplitString(rentPaidNotification.getMessage(), xOffset, height, getWidth(), 0xFFFFFF);
            height += fontRenderer.listFormattedStringToWidth(rentPaidNotification.getMessage(), getWidth()).size() * fontRenderer.FONT_HEIGHT + 2;
            fontRenderer.drawString("x: " + rentPaidNotification.getX(), xOffset, height, 0xFFFFFF);
            height += fontRenderer.FONT_HEIGHT + 2;
            fontRenderer.drawString("y: " + rentPaidNotification.getY(), xOffset, height, 0xFFFFFF);
            height += fontRenderer.FONT_HEIGHT + 2;
            fontRenderer.drawString("z: " + rentPaidNotification.getZ(), xOffset, height, 0xFFFFFF);
        }

        @Override
        protected void onClick(int mouseX, int mouseY)
        {

        }

        @Override
        protected int getHeight()
        {
            int height = 5;
            height += fontRenderer.FONT_HEIGHT + 2;
            height += fontRenderer.listFormattedStringToWidth(rentPaidNotification.getMessage(), getWidth()).size() * fontRenderer.FONT_HEIGHT + 2;
            height += fontRenderer.FONT_HEIGHT + 2;
            height += fontRenderer.FONT_HEIGHT + 2;
            return height + 4;
        }
    }
}
