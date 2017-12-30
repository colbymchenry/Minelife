package com.minelife.gun.client.gui;

import com.minelife.MLKeys;
import com.minelife.Minelife;
import com.minelife.gun.client.guns.ItemGunClient;
import com.minelife.gun.item.guns.ItemGun;
import com.minelife.gun.packet.PacketSetSite;
import com.minelife.util.client.GuiDropDown;
import com.minelife.util.client.GuiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.opengl.GL11;

public class GuiModifyGun extends GuiScreen {

    private int xPosition, yPosition;

    private ItemStack stack;
    private ItemGunClient client;
    private GuiDropDown siteDropDown;

    public GuiModifyGun(ItemStack stack) {
        this.stack = stack;
        ItemGunClient.modifying = true;
    }

    @Override
    public void drawScreen(int mouse_x, int mouse_y, float f) {
        super.drawScreen(mouse_x, mouse_y, f);
        siteDropDown.draw(mc, mouse_x, mouse_y);
    }

    @Override
    protected void mouseClicked(int mouse_x, int mouse_y, int mouse_btn) {
        super.mouseClicked(mouse_x, mouse_y, mouse_btn);
        int selected = siteDropDown.selected;
        siteDropDown.mouseClicked(mc, mouse_x, mouse_y);
        if(selected != siteDropDown.selected) {
            Minelife.NETWORK.sendToServer(new PacketSetSite(0));
        }
    }

    @Override
    public void initGui() {
        super.initGui();
        if (stack == null) {
            Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.RED + "Could not find gun."));
            Minecraft.getMinecraft().thePlayer.closeScreen();
            return;
        }

        if (!(stack.getItem() instanceof ItemGun)) {
            Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.RED + "Could not find gun."));
            Minecraft.getMinecraft().thePlayer.closeScreen();
            return;
        }

        xPosition = this.width / 2;
        yPosition = this.height / 2;
        client = ((ItemGun) stack.getItem()).getClientHandler();
        siteDropDown = new GuiDropDown(xPosition + client.getScopeXOffsetForGui(), yPosition + client.getScopeYOffsetForGui(), 80, 12,
                "Holographic", "Red Dot", "2X Scope", "4X Scope", "8X Scope");
    }

    @Override
    protected void keyTyped(char p_73869_1_, int p_73869_2_) {
        super.keyTyped(p_73869_1_, p_73869_2_);
        if (p_73869_2_ == MLKeys.keyModifyGun.getKeyCode()) {
            Minecraft.getMinecraft().thePlayer.closeScreen();
        }
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        ItemGunClient.modifying = false;
    }
}
