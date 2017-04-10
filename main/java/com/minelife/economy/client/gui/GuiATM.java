package com.minelife.economy.client.gui;

import com.minelife.Minelife;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.opengl.GL11;

import java.util.Calendar;
import java.util.Date;

public class GuiATM extends GuiScreen {

    protected int block = 0;

    protected String statusMessage = "";
    protected Date statusMessageTime = Calendar.getInstance().getTime();

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawGradientRect(0, 0, this.width, this.block, 0xFFE34335, 0xFFFFF8F5);
        this.drawGradientRect(0, this.block, this.width, this.height, 0xFFFFFAF6, 0xFFD1D6DA);

        this.drawLabel(EnumChatFormatting.BOLD + "A T M", this.width - 70, this.block / 2, 3.0, 0);

        /**
         * Make sure it isn't past the 5 second duration if we are going to draw status message
         */
        if (Calendar.getInstance().getTime().before(statusMessageTime))
            this.drawLabel(this.statusMessage, 5 + (this.fontRendererObj.getStringWidth(this.statusMessage) * 2) / 2, 12, 2.0, 0xFFFFFFFF);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void initGui() {
        super.initGui();
        this.buttonList.clear();
        this.block = this.height / 8;
    }

    /**
     * Used to draw centered/scaled strings
     */
    protected void drawLabel(String text, int x, int y, double scale, int color) {
        GL11.glPushMatrix();
        {
            int textWidth = this.fontRendererObj.getStringWidth(text);
            int textHeight = this.fontRendererObj.FONT_HEIGHT;

            GL11.glTranslatef(x - (textWidth / 2), y - (textHeight / 2), this.zLevel);
            GL11.glTranslatef(textWidth / 2, textHeight / 2, this.zLevel / 2);
            GL11.glScaled(scale, scale, scale);
            GL11.glTranslatef(-(textWidth / 2), -(textHeight / 2), -(this.zLevel / 2));
            this.fontRendererObj.drawString(text, 0, 0, color);
        }
        GL11.glPopMatrix();
    }

    /**
     * This is the buttons used in the ATM
     * The only purpose for making this class is to customize the visual appearance of the buttons
     */
    protected class ButtonATM extends GuiButton {

        private double textScale;

        public ButtonATM(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText, double textScale) {
            super(buttonId, x, y, widthIn, heightIn, buttonText);
            this.textScale = textScale;
        }

        @Override
        public void drawButton(Minecraft mc, int mouseX, int mouseY) {
            if (!this.visible) return;

            boolean hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;

            drawGradientRect(this.xPosition, this.yPosition, this.xPosition + this.width, this.yPosition + this.height, 0xFF1369CC, 0xFF004293);

            if (hovered)
                drawGradientRect(this.xPosition, this.yPosition, this.xPosition + this.width, this.yPosition + this.height, 0x88FFFFFF, 0x88FFFFFF);

            drawLabel(displayString, xPosition + (this.width / 2), yPosition + (this.height / 2), this.textScale, 0xFFFFFF);
        }



        @Override
        public void func_146113_a(SoundHandler soundHandlerIn) {
            mc.thePlayer.playSound(Minelife.MOD_ID + ":" + "gui.atm.click", 0.5F, 1.0F);
        }
    }

    /**
     * The status message that is drawn on the top right of the ATM gui
     */
    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, 5);
        this.statusMessageTime = calendar.getTime();
    }
}
