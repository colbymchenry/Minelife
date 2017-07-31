package com.minelife.notification;

import com.minelife.util.ArrayUtil;
import net.minecraft.nbt.NBTTagCompound;

public class TextNotification extends AbstractNotification {

    private String[] text;

    public TextNotification()
    {
    }

    public TextNotification(String... text)
    {
        this.text = text;
    }

    @Override
    public void writeToNBT(NBTTagCompound tagCompound)
    {
        tagCompound.setString("text", ArrayUtil.toString(text));
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompound)
    {
        this.text = ArrayUtil.fromString(tagCompound.getString("text"));
    }

    @Override
    public Class<? extends AbstractGuiNotification> getGuiClass()
    {
        return GuiTextNotification.class;
    }

    public static class GuiTextNotification extends AbstractGuiNotification {

        private TextNotification textNotification;

        public GuiTextNotification(AbstractNotification abstractNotification)
        {
            super(abstractNotification);
            this.textNotification = (TextNotification) abstractNotification;
        }

        @Override
        protected void drawForeground()
        {
            String[] lines = this.textNotification.text;
            int y = 5;
            for (String line : lines) {
                fontRenderer.drawSplitString(line, 5, y, getWidth(), 0xFFFFFF);
                y += fontRenderer.listFormattedStringToWidth(line, getWidth()).size() * mc.fontRenderer.FONT_HEIGHT + 2;
            }
        }

        @Override
        protected void onClick(int mouseX, int mouseY)
        {

        }

        @Override
        protected int getHeight()
        {
            String[] lines = this.textNotification.text;
            int height = 0;
            for (String line : lines) {
                height += fontRenderer.listFormattedStringToWidth(line, getWidth()).size() * mc.fontRenderer.FONT_HEIGHT + 2;
            }
            return height;
        }
    }
}
