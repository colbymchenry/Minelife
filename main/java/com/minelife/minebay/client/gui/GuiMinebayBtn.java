package com.minelife.minebay.client.gui;

import com.minelife.util.client.GuiHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;

public class GuiMinebayBtn extends GuiButton {

    private FontRenderer fontRenderer;

    public GuiMinebayBtn(int id, int x, int y, String displayString, FontRenderer fontRenderer) {
        super(id, x, y, displayString);
        this.fontRenderer = fontRenderer;
        this.width = fontRenderer.getStringWidth(displayString) + 15;
        this.height = 20;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        boolean hovering = mousePressed(mc, mouseX, mouseY);
        GlStateManager.color(1, 1, 1, 1);
        GuiHelper.drawDefaultBackground(this.x, this.y, this.width, this.height, !this.enabled ? 0x8b8b8d : hovering ? 0xe600e4 : 0xcb00cb);
        fontRenderer.drawStringWithShadow(this.displayString, x + 1 + (this.width - fontRenderer.getStringWidth(displayString)) / 2, this.y + 1 + (this.height - this.fontRenderer.FONT_HEIGHT) / 2, hovering ? 16777120 : 0xFFFFFF);
    }
}
