package com.minelife.economy.client.gui.atm;

import com.minelife.Minelife;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.opengl.GL11;

public class GuiATMBase extends GuiScreen {

    private long messageTime;
    private String message;

    protected int block = 0;

    public int guiLeft, guiTop, xSize = 176, ySize = 186;
    public int balance;

    public GuiATMBase(int balance) {
        this.balance = balance;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawGradientRect(0, 0, this.width, this.block, 0xFFE34335, 0xFFFFF8F5);
        this.drawGradientRect(0, this.block, this.width, this.height, 0xFFFFFAF6, 0xFFD1D6DA);

        this.drawLabel(TextFormatting.BOLD + "A T M", this.width - 70, this.block / 2, 3.0, 0, true);

        if(System.currentTimeMillis() < messageTime) this.drawLabel(message, 5, 5, 2.0, 0xFFFFFF, false);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void initGui() {
        super.initGui();
        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;
        this.buttonList.clear();
        this.block = this.height / 8;
    }

    protected void submitMessage(String message, int seconds) {
        this.messageTime = System.currentTimeMillis() + (seconds * 1000L);
        this.message = message;
    }

    /**
     * Used to draw centered/scaled strings
     */
    protected void drawLabel(String text, int x, int y, double scale, int color, boolean center) {
        GL11.glPushMatrix();
        {
            int textWidth = this.fontRenderer.getStringWidth(text);
            int textHeight = this.fontRenderer.FONT_HEIGHT;

            if(center) {
                GlStateManager.translate(x - (textWidth / 2), y - (textHeight / 2), this.zLevel);
                GlStateManager.translate(textWidth / 2, textHeight / 2, 0);
                GlStateManager.scale(scale, scale, scale);
                GlStateManager.translate(-(textWidth / 2), -(textHeight / 2), 0);
            } else {
                GlStateManager.translate(x, y, this.zLevel);
                GlStateManager.scale(scale, scale, scale);
            }
            this.fontRenderer.drawString(text, 0, 0, color);
        }
        GL11.glPopMatrix();
    }

    protected void playKeyTypeSound() {
        mc.player.playSound(new SoundEvent(new ResourceLocation(Minelife.MOD_ID, "gui.atm.key_stroke")), 0.5F, 1.0F);
    }

    protected void playErrorSound() {
        mc.player.playSound(new SoundEvent(new ResourceLocation(Minelife.MOD_ID, "gui.atm.error")), 0.5F, 1.0F);
    }


    protected class ButtonATM extends GuiButton {

        private double textScale;

        public ButtonATM(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText, double textScale) {
            super(buttonId, x, y, widthIn, heightIn, buttonText);
            this.textScale = textScale;
        }

        @Override
        public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
            if (!this.visible) return;

            boolean hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;

            drawGradientRect(this.x, this.y, this.x + this.width, this.y + this.height, 0xFF1369CC, 0xFF004293);

            if (hovered)
                drawGradientRect(this.x, this.y, this.x + this.width, this.y + this.height, 0x88FFFFFF, 0x88FFFFFF);

            drawLabel(displayString, x + (this.width / 2), y + (this.height / 2), this.textScale, 0xFFFFFF, true);
        }


        @Override
        public void playPressSound(SoundHandler soundHandlerIn) {
            mc.player.playSound(new SoundEvent(new ResourceLocation(Minelife.MOD_ID, "gui.atm.click")), 0.5F, 1.0F);
        }
    }

}
