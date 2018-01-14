package com.minelife.gangs.client.gui;

import com.minelife.util.client.GuiColorPicker;
import com.minelife.util.client.GuiPixelArt;
import net.minecraft.client.gui.ScaledResolution;

public class GuiDesignSymbol extends GuiGang {

    private GuiPixelArt GuiPixelArt;

    public GuiDesignSymbol(com.minelife.gangs.Gang Gang) {
        super(Gang);
    }

    @Override
    public void drawScreen(int mouse_x, int mouse_y, float f) {
        super.drawScreen(mouse_x, mouse_y, f);
        GuiPixelArt.drawScreen(mouse_x, mouse_y);
    }

    @Override
    public void mouseClicked(int mouse_x, int mouse_y, int mouse_btn) {
        super.mouseClicked(mouse_x, mouse_y, mouse_btn);
        GuiPixelArt.mouseClicked(mouse_x, mouse_y);
    }

    @Override
    public void mouseClickMove(int mouse_x, int mouse_y, int lastButtonClicked, long timeSinceMouseClick) {
        super.mouseClickMove(mouse_x, mouse_y, lastButtonClicked, timeSinceMouseClick);
        GuiPixelArt.mouseClickMove(mouse_x, mouse_y);
    }

    @Override
    public void initGui() {
        super.initGui();

        ScaledResolution scaledResolution = new ScaledResolution(mc, width, height);
        int scale = scaledResolution.getScaleFactor() + 5;

        GuiColorPicker GuiColorPicker = new GuiColorPicker(XPosition + ((Width - 100) / 2), YPosition + Height - 50, 100, 50, 0, 0, 0);
        GuiPixelArt = new GuiPixelArt(mc, XPosition, YPosition, Width, Height, scale, GuiColorPicker);
    }
}
