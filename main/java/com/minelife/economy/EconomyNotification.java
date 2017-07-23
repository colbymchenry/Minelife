package com.minelife.economy;

import com.minelife.notification.AbstractGuiNotification;
import com.minelife.notification.AbstractNotification;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;

import java.util.UUID;

public class EconomyNotification extends AbstractNotification {

    private String text;

    public EconomyNotification(){}

    public EconomyNotification(UUID player, String text) {
        super();
        this.playerUniqueID = player;
        this.text = text;
        this.writeToNBT(tagCompound);
    }

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
            fontRenderer.drawString(EnumChatFormatting.YELLOW.toString() + EnumChatFormatting.UNDERLINE.toString() + "Billing", 5, 5, 0xFFFFFF);
            fontRenderer.drawSplitString(economyNotification.text, 8, 18, getWidth(), 0xFFFFFF);
        }

        @Override
        protected void onClick(int mouseX, int mouseY)
        {
        }

        @Override
        protected int getHeight()
        {
            return fontRenderer.FONT_HEIGHT + (fontRenderer.listFormattedStringToWidth(economyNotification.text, getWidth()).size() * fontRenderer.FONT_HEIGHT + 5);
        }
    }
}
