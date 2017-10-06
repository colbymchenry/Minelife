package com.minelife.realestate;

import com.minelife.notification.AbstractGuiNotification;
import com.minelife.notification.AbstractNotification;
import com.minelife.util.NumberConversions;
import net.minecraft.nbt.NBTTagCompound;

import java.util.UUID;

// TODO
public class PurchaseNotification extends AbstractNotification{

    private double amount;
    private int estateID;

    public PurchaseNotification() {}

    public PurchaseNotification(UUID playerUUID) {
        super(playerUUID);
    }

    @Override
    public void writeToNBT(NBTTagCompound tagCompound) {
        tagCompound.setDouble("amount", amount);
        tagCompound.setInteger("estateID", estateID);
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        amount = tagCompound.getDouble("amount");
        estateID = tagCompound.getInteger("estateID");
    }

    @Override
    public Class<? extends AbstractGuiNotification> getGuiClass() {
        return GuiPurchaseNotification.class;
    }

    public class GuiPurchaseNotification extends AbstractGuiNotification {

        private PurchaseNotification notification;

        public GuiPurchaseNotification() {}

        public GuiPurchaseNotification(PurchaseNotification notification) {
            this.notification = notification;
        }

        @Override
        protected void drawForeground() {
            fontRenderer.drawString("Estate: #" + notification.estateID, 5, 5, 0xFFFFFF);
            fontRenderer.drawString("Amount: $" + NumberConversions.formatter.format(notification.amount), 5, 5 + 12, 0xFFFFFF);
        }

        @Override
        protected void onClick(int mouseX, int mouseY) {

        }

        @Override
        protected int getHeight() {
            return 20;
        }

        @Override
        public String getSound() {
            return "cha_ching";
        }
    }
}
