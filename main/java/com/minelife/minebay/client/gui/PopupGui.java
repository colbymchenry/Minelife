package com.minelife.minebay.client.gui;

import net.minecraft.client.gui.GuiScreen;

public class PopupGui extends GuiScreen {

    private String message;
    private GuiScreen previous_gui;
    private int left, top, bg_height;
    private CustomButton ok_btn;

    public PopupGui(String message, GuiScreen previous_gui)
    {
        this.message = message;
        this.previous_gui = previous_gui;
    }

    private static int bg_width = 100;

    @Override
    public void drawScreen(int mouse_x, int mouse_y, float f)
    {
        drawDefaultBackground();
        fontRendererObj.drawSplitString(message, this.left, this.top, bg_width, 0xFFFFFF);
        this.ok_btn.drawButton(mc, mouse_x, mouse_y);
    }

    @Override
    protected void mouseClicked(int p_73864_1_, int p_73864_2_, int p_73864_3_)
    {
        super.mouseClicked(p_73864_1_, p_73864_2_, p_73864_3_);
        if (this.ok_btn.mousePressed(mc, p_73864_1_, p_73864_2_)) {
            mc.displayGuiScreen(previous_gui);
        }
    }

    @Override
    public void initGui()
    {
        super.initGui();

        this.bg_height = fontRendererObj.listFormattedStringToWidth(message, bg_width).size() * fontRendererObj.FONT_HEIGHT;

        this.left = (this.width - bg_width) / 2;
        this.top = (this.height - this.bg_height) / 2;
        this.ok_btn = new CustomButton(0, left + ((bg_width - 50) / 2), top + bg_height - 10, "Ok", fontRendererObj);
    }
}
