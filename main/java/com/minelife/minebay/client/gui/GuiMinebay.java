package com.minelife.minebay.client.gui;

import com.minelife.Minelife;
import com.minelife.util.client.GuiHelper;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;

public class GuiMinebay extends GuiScreen {


    private static final ResourceLocation texLogo = new ResourceLocation(Minelife.MOD_ID, "textures/gui/minebay/logo_lg.png");
    private GuiTextField searchField;
    protected int guiLeft, guiTop, xSize = 308, ySize = 169;
    private int logoWidth = 354, logoHeight = 104;


    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.drawDefaultBackground();
        GuiHelper.drawDefaultBackground(this.guiLeft - 5, this.guiTop - 5, this.xSize + 7, this.ySize + 7, 0x8F008D);
        this.mc.getTextureManager().bindTexture(texLogo);
        Gui.drawModalRectWithCustomSizedTexture(this.guiLeft + (this.xSize - (this.logoWidth / 2)) / 2, this.guiTop - (this.logoHeight / 2) - 3, 0, 0, this.logoWidth / 2, this.logoHeight / 2, this.logoWidth / 2, this.logoHeight / 2);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void initGui() {
        super.initGui();
        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;
        this.guiTop += 20;
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
    }
}
