package com.minelife.tdm.client.gui;

import com.minelife.tdm.Arena;
import com.minelife.tdm.Match;
import com.minelife.util.client.GuiDropDown;
import com.minelife.util.client.GuiHelper;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;

import java.io.IOException;

public class GuiLobby extends GuiScreen {

    private int guiLeft, guiTop, guiWidth = 250, guiHeight = 250;
    private GuiDropDown primaryDropDown, secondaryDropDown, primarySightDropDown, secondarySightDropDown;
    private Match match;
    private String arena;

    public GuiLobby(Match match, String arena) {
        this.match = match;
        this.arena = arena;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        GlStateManager.disableTexture2D();
        Gui.drawRect(0, 0, width, height, 0);
        GlStateManager.enableTexture2D();

        GlStateManager.color(1, 1, 1, 1);
        GuiHelper.drawDefaultBackground(guiLeft, guiTop, guiWidth, guiHeight);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        primaryDropDown.mouseClicked(mc, mouseX, mouseY);
        secondaryDropDown.mouseClicked(mc, mouseX, mouseY);
        primarySightDropDown.mouseClicked(mc, mouseX, mouseY);
        secondarySightDropDown.mouseClicked(mc, mouseX, mouseY);
    }

    @Override
    public void initGui() {
        super.initGui();
        guiLeft = (this.width - guiWidth) / 2;
        guiTop = (this.height - guiHeight) / 2;

    }
}
