package com.minelife.util.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Keyboard;

import java.io.IOException;

public class GuiPopup extends GuiScreen {

    private int guiLeft, guiTop, xSize = 120, ySize;
    private GuiScreen previousScreen;
    private String message;
    private int bgColor, txtColor;

    public GuiPopup(GuiScreen previousScreen, String message, int bgColor, int txtColor) {
        this.previousScreen = previousScreen;
        this.message = message;
        this.bgColor = bgColor;
        this.txtColor = txtColor;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        GuiHelper.drawDefaultBackground(this.guiLeft, this.guiTop, this.xSize, this.ySize, this.bgColor);
        GlStateManager.disableLighting();
        this.fontRenderer.drawSplitString(this.message, this.guiLeft + 5, this.guiTop + 5, this.xSize - 10, this.txtColor);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);
        if(previousScreen != null) {
            previousScreen.initGui();
            Minecraft.getMinecraft().displayGuiScreen(previousScreen);
        } else {
            Minecraft.getMinecraft().player.closeScreen();
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        if(keyCode == Keyboard.KEY_ESCAPE) {
            if (previousScreen != null) {
                previousScreen.initGui();
                Minecraft.getMinecraft().displayGuiScreen(previousScreen);
            } else {
                Minecraft.getMinecraft().player.closeScreen();
            }
        }
    }

    @Override
    public void initGui() {
        super.initGui();
        this.ySize = 40 + this.fontRenderer.listFormattedStringToWidth(message, xSize - 10).size() * this.fontRenderer.FONT_HEIGHT;
        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(0, (this.width - 30) / 2, this.guiTop + this.ySize - 25, 30, 20, "OK"));
    }
}
