package com.minelife.realestate;

import com.minelife.Minelife;
import com.minelife.notification.AbstractGuiNotification;
import com.minelife.notification.AbstractNotification;
import com.minelife.util.NumberConversions;
import com.minelife.util.client.GuiUtil;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;

import java.util.UUID;

public class RentDueNotification extends AbstractNotification {

    private static final ResourceLocation houseIcon = new ResourceLocation(Minelife.MOD_ID, "textures/gui/house-icon.png");

    private int daysTillDue;
    private int estateID;
    private double amount;

    public RentDueNotification() {}

    public RentDueNotification(UUID playerUUID, int daysTillDue, int estateID, double amount) {
        super(playerUUID);
        this.daysTillDue = daysTillDue;
        this.estateID = estateID;
        this.amount = amount;
    }

    @Override
    public void writeToNBT(NBTTagCompound tagCompound) {
        tagCompound.setInteger("daysTillDue", daysTillDue);
        tagCompound.setInteger("estateID", estateID);
        tagCompound.setDouble("amount", amount);
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        daysTillDue = tagCompound.getInteger("daysTillDue");
        estateID = tagCompound.getInteger("estateID");
        amount = tagCompound.getDouble("amount");
    }

    @Override
    public Class<? extends AbstractGuiNotification> getGuiClass() {
        return GuiRentDueNotification.class;
    }

    public static class GuiRentDueNotification extends AbstractGuiNotification {

        public RentDueNotification notification;

        public GuiRentDueNotification() {}

        public GuiRentDueNotification(AbstractNotification notification) {
            super(notification);
            this.notification = (RentDueNotification) notification;
        }

        @Override
        protected void drawForeground() {
            mc.getTextureManager().bindTexture(houseIcon);
            GuiUtil.drawImage(5, 5, 16, 16);
            fontRenderer.drawString(EnumChatFormatting.ITALIC + "Rent Due in " + notification.daysTillDue + " day(s)", 28, 9, 0xFFFFFF);
            fontRenderer.drawString("Estate: #" + notification.estateID, 5, 5 + 12 + 5 + 5, 0xFFFFFF);
            fontRenderer.drawString("Amount: $" + NumberConversions.formatter.format(notification.amount), 5, 5 + 12 + 12 + 5 + 5, 0xFFFFFF);
        }

        @Override
        protected void onClick(int mouseX, int mouseY) {

        }

        @Override
        protected int getHeight() {
            return 45;
        }
    }

}
