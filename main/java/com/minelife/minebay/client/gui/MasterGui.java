package com.minelife.minebay.client.gui;

import com.minelife.Minelife;
import com.minelife.util.client.GuiUtil;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public abstract class MasterGui extends GuiScreen {

    private static final ResourceLocation bg_texture = new ResourceLocation(Minelife.MOD_ID, "textures/gui/minebay/background.png");
    private static final ResourceLocation logo_texture = new ResourceLocation(Minelife.MOD_ID, "textures/gui/minebay/logo_lg.png");
    protected final int bg_width = 248, bg_height = 169, logo_width = 354, logo_height = 104;
    protected int left, top;

    @Override
    public void drawScreen(int mouse_x, int mouse_y, float f)
    {
        super.drawScreen(mouse_x, mouse_y, f);
        this.drawDefaultBackground();
//        this.mc.getTextureManager().bindTexture(bg_texture);
//        this.drawTexturedModalRect(this.left, this.top, 0, 0, this.bg_width, this.bg_height);
        GuiUtil.drawDefaultBackground(this.left - 5, this.top - 5, this.bg_width + 7, this.bg_height + 7, new Color(0x70006e));
        this.mc.getTextureManager().bindTexture(logo_texture);
        GuiUtil.drawImage(this.left + (bg_width - (logo_width / 2)) / 2, this.top - (this.logo_height / 2) - 3, this.logo_width / 2, this.logo_height / 2);
    }

    @Override
    protected void mouseClicked(int mouse_x, int mouse_y, int mouse_btn)
    {
        super.mouseClicked(mouse_x, mouse_y, mouse_btn);
    }

    @Override
    protected void keyTyped(char c, int i)
    {
        super.keyTyped(c, i);
    }

    @Override
    public void initGui()
    {
        super.initGui();
        this.left = (this.width - this.bg_width) / 2;
        this.top = (this.height - this.bg_height) / 2;
        this.top += 20;
    }
}
