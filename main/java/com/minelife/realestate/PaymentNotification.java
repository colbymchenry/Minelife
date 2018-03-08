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

public class PaymentNotification extends AbstractNotification{

    private static final ResourceLocation houseIcon = new ResourceLocation(Minelife.MOD_ID, "textures/gui/house-icon.png");

    private boolean rent;
    private double amount;
    private int estateID;

    public PaymentNotification() {}

    public PaymentNotification(UUID playerUUID, double amount, int estateID, boolean rent) {
        super(playerUUID);
        this.amount = amount;
        this.estateID = estateID;
        this.rent = rent;
    }

    @Override
    public void writeToNBT(NBTTagCompound tagCompound) {
        tagCompound.setDouble("amount", amount);
        tagCompound.setInteger("estateID", estateID);
        tagCompound.setBoolean("rent", rent);
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        amount = tagCompound.getDouble("amount");
        estateID = tagCompound.getInteger("estateID");
        rent = tagCompound.getBoolean("rent");
    }

    @Override
    public Class<? extends AbstractGuiNotification> getGuiClass() {
        return GuiPurchaseNotification.class;
    }

    public static class GuiPurchaseNotification extends AbstractGuiNotification {

        private PaymentNotification notification;

        public GuiPurchaseNotification() {}

        public GuiPurchaseNotification(AbstractNotification notification) {
            super(notification);
            this.notification = (PaymentNotification) notification;
        }

        @Override
        protected void drawForeground() {
            mc.getTextureManager().bindTexture(houseIcon);
            GuiUtil.drawImage(5, 5, 16, 16);
            fontRenderer.drawString(EnumChatFormatting.ITALIC + (notification.rent ? "Rent" : "Purchase"), 28, 9, 0xFFFFFF);
            fontRenderer.drawString("Estate: #" + notification.estateID, 5, 5 + 12 + 5 + 5, 0xFFFFFF);
            fontRenderer.drawString("Amount: $" + NumberConversions.formatter.format(notification.amount), 5, 5 + 12 + 12 + 5 + 5, 0xFFFFFF);
            fontRenderer.drawString("Was deposited into your checking account.", 5, 5 + 12 + 12 + 5 + 5 + 10, 0xFFFFFF);
        }

        @Override
        protected void onClick(int mouseX, int mouseY) {

        }

        @Override
        protected int getHeight() {
            return 55;
        }

        @Override
        public String getSound() {
            return "cha_ching";
        }
    }
}
