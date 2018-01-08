package com.minelife.welfare;

import com.minelife.notification.AbstractGuiNotification;
import com.minelife.notification.AbstractNotification;
import com.minelife.util.NumberConversions;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;

import java.util.UUID;

public class WelfareNotification extends AbstractNotification {

    private double amount;

    public WelfareNotification() {}

    public WelfareNotification(UUID player_uuid, double amount) {
        super(player_uuid);
        this.amount = amount;
    }

    @Override
    public void writeToNBT(NBTTagCompound tagCompound) {
        tagCompound.setDouble("amount", amount);
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        amount = tagCompound.getDouble("amount");
    }

    @Override
    public Class<? extends AbstractGuiNotification> getGuiClass() {
        return GuiWelfareNotification.class;
    }

    public static class GuiWelfareNotification extends AbstractGuiNotification {

        private WelfareNotification notification;

        public GuiWelfareNotification() {}

        public GuiWelfareNotification(AbstractNotification notification) {
            super(notification);
            this.notification = (WelfareNotification) notification;
        }

        @Override
        protected void drawForeground() {
            mc.fontRenderer.drawSplitString("Welfare deposited into your checking account:", 5, 6, getWidth() - 5, 0xFFFFFF);
            mc.fontRenderer.drawSplitString(EnumChatFormatting.GREEN + "+$" + NumberConversions.formatter.format(notification.amount), 7,
                    7 + mc.fontRenderer.listFormattedStringToWidth("Welfare deposited into your checking account:", getWidth() - 5).size() * mc.fontRenderer.FONT_HEIGHT, getWidth() - 67, 0xFFFFFF);
        }

        @Override
        protected void onClick(int mouseX, int mouseY) {

        }

        @Override
        protected int getHeight() {
            return 2 +
                    (mc.fontRenderer.listFormattedStringToWidth("Welfare deposited into your checking account:", getWidth() - 5).size() * mc.fontRenderer.FONT_HEIGHT) +
                    (mc.fontRenderer.listFormattedStringToWidth("$" + NumberConversions.formatter.format(notification.amount), getWidth() - 7).size() * mc.fontRenderer.FONT_HEIGHT) +
                    2;
        }

        @Override
        public String getSound() {
            return "cha_ching";
        }
    }

}