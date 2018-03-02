package com.minelife.gangs.client.gui;

import com.minelife.gangs.Gang;
import com.minelife.util.client.GuiUtil;
import net.minecraft.client.gui.GuiScreen;

import java.awt.*;

public class GuiGang extends GuiScreen {

    public static Color BackgroundColor = new Color(77, 77, 77, 255);
    protected static int Width = 256, Height = 200;
    protected int XPosition, YPosition;
    public Gang Gang;

    public GuiGang(Gang Gang) {
        this.Gang = Gang;
    }

    @Override
    public void drawScreen(int mouse_x, int mouse_y, float f) {
        super.drawScreen(mouse_x, mouse_y, f);
        GuiUtil.drawDefaultBackground(XPosition, YPosition, Width, Height, BackgroundColor);
    }

    @Override
    public void initGui() {
        super.initGui();
        XPosition = (this.width - Width) / 2;
        YPosition = (this.height - Height) / 2;
    }


}
