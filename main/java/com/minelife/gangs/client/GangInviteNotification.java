package com.minelife.gangs.client;

import com.minelife.notification.AbstractGuiNotification;
import com.minelife.notification.AbstractNotification;
import com.minelife.util.StringHelper;
import net.minecraft.nbt.NBTTagCompound;

import java.util.UUID;

public class GangInviteNotification extends AbstractNotification {

    public String gangName;

    public GangInviteNotification() {}

    public GangInviteNotification(UUID playerUniqueID, String gangName) {
        super(playerUniqueID);
        this.gangName = gangName;
    }

    @Override
    public void writeToNBT(NBTTagCompound tagCompound) {
        tagCompound.setString("gangName", gangName);
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        gangName = tagCompound.getString("gangName");
    }

    @Override
    public Class<? extends AbstractGuiNotification> getGuiClass() {
        return ClientGangInviteNotification.class;
    }

    public static class ClientGangInviteNotification extends AbstractGuiNotification {

        public ClientGangInviteNotification() {
            super();
        }

        private GangInviteNotification gangNotification;

        public ClientGangInviteNotification(AbstractNotification notification) {
            super(notification);
            this.gangNotification = (GangInviteNotification) notification;
        }

        @Override
        protected void drawForeground() {
            fontRenderer.drawSplitString(StringHelper.ParseFormatting("&a&l" + gangNotification.gangName + " &6wants you to join the gang!", '&'), 10, 10, getWidth() - 20,0XFFFFFF);
            fontRenderer.drawString(StringHelper.ParseFormatting("&c/g accept &6- to accept.", '&'), 10, 36, 0xFFFFFF);
            fontRenderer.drawString(StringHelper.ParseFormatting("&c/g deny &6- to deny.", '&'), 10, 48, 0xFFFFFF);
        }

        @Override
        protected void onClick(int mouseX, int mouseY) {

        }

        @Override
        protected int getHeight() {
            return 60;
        }

        @Override
        public String getSound() {
            return "text_message";
        }
    }

}
