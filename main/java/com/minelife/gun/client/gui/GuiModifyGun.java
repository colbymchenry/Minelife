package com.minelife.gun.client.gui;

import com.minelife.MLKeys;
import com.minelife.gun.client.guns.ItemGunClient;
import com.minelife.gun.item.guns.ItemGun;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

public class GuiModifyGun extends GuiScreen {

    private int xPosition, yPosition;

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
        if(stack == null) {
            Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.RED + "Could not find gun."));
            Minecraft.getMinecraft().thePlayer.closeScreen();
            return;
        }

        if(!(stack.getItem() instanceof ItemGun)) {
            Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.RED + "Could not find gun."));
            Minecraft.getMinecraft().thePlayer.closeScreen();
            return;
        }

        xPosition = this.width / 2;
        yPosition = this.height / 2;
    }

    @Override
    protected void keyTyped(char p_73869_1_, int p_73869_2_) {
        super.keyTyped(p_73869_1_, p_73869_2_);
        if(p_73869_2_ == MLKeys.keyModifyGun.getKeyCode()) {
            Minecraft.getMinecraft().thePlayer.closeScreen();
        }
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        ItemGunClient.modifying = false;
    }
}
