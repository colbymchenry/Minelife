package com.minelife.gun.client.gui;

import com.minelife.gun.client.guns.ItemGunClient;
import com.minelife.gun.item.guns.ItemGun;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;

public class GuiModifyGun extends GuiScreen {

    private ItemStack stack;

    public GuiModifyGun(ItemStack stack) {
        this.stack = stack;
        ItemGunClient.modifying = true;
    }

    @Override
    public void drawScreen(int mouse_x, int mouse_y, float f) {
        super.drawScreen(mouse_x, mouse_y, f);
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
    public void onGuiClosed() {
        super.onGuiClosed();
        ItemGunClient.modifying = false;
    }
}
