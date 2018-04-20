package com.minelife.gangs.client.gui;

import com.minelife.Minelife;
import com.minelife.gangs.network.PacketRequestAlliance;
import com.minelife.util.client.GuiHelper;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.input.Mouse;

import java.io.IOException;

public class GuiGangAlliances extends GuiScreen {

    private static int guiWidth = 240, guiHeight = 190;
    private int guiLeft, guiTop;
    private GuiTextField requestField;
    private GuiAllianceList guiAllianceList;
    private GuiGangMenu previousScreen;

    public GuiGangAlliances(GuiGangMenu previousScreen) {
        this.previousScreen = previousScreen;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        GuiHelper.drawDefaultBackground(guiLeft, guiTop, guiWidth, guiHeight);
        // draw gang name
        GlStateManager.pushMatrix();
        int stringWidth = fontRenderer.getStringWidth(TextFormatting.BOLD + previousScreen.gang.getName());
        GlStateManager.translate((this.width - stringWidth) / 2, this.guiTop - 20, zLevel);
        GlStateManager.translate(stringWidth / 2, fontRenderer.FONT_HEIGHT / 2, 0);
        GlStateManager.scale(2, 2, 2);
        GlStateManager.translate(-stringWidth / 2, -fontRenderer.FONT_HEIGHT / 2, 0);
        fontRenderer.drawString(TextFormatting.BOLD + previousScreen.gang.getName(), 0, 0, 0xef8228);
        GlStateManager.popMatrix();

        GlStateManager.pushMatrix();
        stringWidth = fontRenderer.getStringWidth(TextFormatting.UNDERLINE + "Alliances");
        GlStateManager.translate(guiLeft + 23, guiTop + 13, zLevel);
        GlStateManager.translate(stringWidth / 2, fontRenderer.FONT_HEIGHT / 2, 0);
        GlStateManager.scale(1.5, 1.5, 1.5);
        GlStateManager.translate(-stringWidth / 2, -fontRenderer.FONT_HEIGHT / 2, 0);
        fontRenderer.drawString(TextFormatting.UNDERLINE + "Alliances", 0, 0, GuiHelper.defaultTextColor);
        GlStateManager.popMatrix();

        requestField.drawTextBox();
        guiAllianceList.draw(mouseX, mouseY, Mouse.getDWheel());
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        requestField.textboxKeyTyped(typedChar, keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        requestField.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);
        if(button.id == 1) {
            mc.displayGuiScreen(previousScreen);
        } else if(button.id == 0) {
            Minelife.getNetwork().sendToServer(new PacketRequestAlliance(requestField.getText()));
        }
    }

    @Override
    public void initGui() {
        this.guiLeft = (this.width - guiWidth) / 2;
        this.guiTop = (this.height - guiHeight + 20) / 2;
        guiAllianceList = new GuiAllianceList(mc, this.guiLeft + 5, this.guiTop + 35, guiWidth - 10, guiHeight - 40, previousScreen.gang, previousScreen.alliances);
        requestField = new GuiTextField(0, fontRenderer, guiLeft + guiWidth - 150, guiTop + 8, 90, 20);
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(0, requestField.x + requestField.width + 2, requestField.y, 50, 20, "Request"));
        this.buttonList.add(new GuiButton(1, guiLeft - 22, guiTop + 2, 20, 20, "<"));
        this.buttonList.add(new GuiButton(2, guiLeft + guiWidth + 2, guiTop + 2, 20, 20, ">"));
        this.buttonList.get(2).enabled = false;
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        requestField.updateCursorCounter();
        this.buttonList.get(0).enabled = !requestField.getText().isEmpty();
    }
}
