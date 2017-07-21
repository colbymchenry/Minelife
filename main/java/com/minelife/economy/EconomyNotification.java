package com.minelife.economy;

import com.minelife.notification.AbstractGuiNotification;
import com.minelife.notification.AbstractNotification;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;

public class EconomyNotification extends AbstractNotification {

    public EconomyNotification() {}

    private String text;

    @Override
    public void readFromNBT(NBTTagCompound tagCompound)
    {
        text = tagCompound.getString("text");
    }

    @Override
    public void writeToNBT(NBTTagCompound tagCompound)
    {
        tagCompound.setString("text", text);
    }

    @Override
    public Class<? extends AbstractGuiNotification> getGuiClass()
    {
        return GuiNotification.class;
    }

    @SideOnly(Side.CLIENT)
    public static class GuiNotification extends AbstractGuiNotification {

        private EconomyNotification economyNotification;

        public GuiNotification(AbstractNotification abstractNotification)
        {
            super(abstractNotification);
            this.economyNotification = (EconomyNotification) abstractNotification;
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
