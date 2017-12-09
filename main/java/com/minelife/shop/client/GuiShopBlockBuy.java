package com.minelife.shop.client;

import com.minelife.shop.TileEntityShopBlock;
import net.minecraft.client.gui.GuiScreen;

public class GuiShopBlockBuy extends GuiScreen {

    private TileEntityShopBlock tile_entity;

    public GuiShopBlockBuy(TileEntityShopBlock tile_entity) {
        this.tile_entity = tile_entity;
    }

    @Override
    public void drawScreen(int mouse_x, int mouse_y, float f) {
        super.drawScreen(mouse_x, mouse_y, f);
    }

    @Override
    protected void keyTyped(char key_char, int key_code) {
        super.keyTyped(key_char, key_code);
    }

    @Override
    protected void mouseClicked(int mouse_x, int mouse_y, int mouse_btn) {
        super.mouseClicked(mouse_x, mouse_y, mouse_btn);
    }

    @Override
    public void initGui() {
        super.initGui();
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
    }
}
