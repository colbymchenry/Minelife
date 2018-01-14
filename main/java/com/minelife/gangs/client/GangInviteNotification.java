package com.minelife.gangs.client;

import com.minelife.notification.AbstractGuiNotification;
import com.minelife.notification.AbstractNotification;
import net.minecraft.nbt.NBTTagCompound;

public class GangInviteNotification extends AbstractNotification {

    @Override
    public void writeToNBT(NBTTagCompound tagCompound) {

    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {

    }

    @Override
    public Class<? extends AbstractGuiNotification> getGuiClass() {
        return null;
    }

    public static class ClientGangInviteNotification extends AbstractGuiNotification {

        public ClientGangInviteNotification() {

        }

        private GangInviteNotification gangNotification;

        public ClientGangInviteNotification(AbstractNotification notification) {
            super(notification);
            this.gangNotification = (GangInviteNotification) notification;
        }

        @Override
        protected void drawForeground() {

        }

        @Override
        protected void onClick(int mouseX, int mouseY) {

        }

        @Override
        protected int getHeight() {
            return 0;
        }
    }

}
