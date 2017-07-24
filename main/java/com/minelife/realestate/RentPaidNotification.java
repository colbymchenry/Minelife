package com.minelife.realestate;

import com.minelife.notification.AbstractGuiNotification;
import com.minelife.notification.AbstractNotification;
import net.minecraft.nbt.NBTTagCompound;

public class RentPaidNotification extends AbstractNotification {

    private String title;
    private String message;

    @Override
    public void writeToNBT(NBTTagCompound tagCompound)
    {

    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompound)
    {

    }

    @Override
    public Class<? extends AbstractGuiNotification> getGuiClass()
    {
        return GuiRentPaidNotification.class;
    }

    public static class GuiRentPaidNotification extends AbstractGuiNotification {

        public GuiRentPaidNotification(AbstractNotification abstractNotification)
        {
            super(abstractNotification);
        }

        @Override
        protected void drawForeground()
        {

        }

        @Override
        protected void onClick(int mouseX, int mouseY)
        {

        }

        @Override
        protected int getHeight()
        {
            return 0;
        }
    }
}
