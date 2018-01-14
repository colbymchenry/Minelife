package com.minelife.gangs.client.gui;

import com.minelife.gangs.Gang;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.input.Mouse;

public class GuiGangMenu extends GuiGang {

    private GuiMembersList GuiMembersList;

    public GuiGangMenu(Gang gang) {
        super(gang);
    }

    @Override
    public void drawScreen(int mouse_x, int mouse_y, float f) {
        super.drawScreen(mouse_x, mouse_y, f);
        mc.fontRenderer.setUnicodeFlag(true);
        GuiMembersList.draw(mouse_x, mouse_y, Mouse.getDWheel());
        mc.fontRenderer.drawString(EnumChatFormatting.UNDERLINE + "Members",
                GuiMembersList.xPosition + (GuiMembersList.width - mc.fontRenderer.getStringWidth("Members")) / 2,
                GuiMembersList.yPosition - 13, 0xFFFFFF);

        mc.fontRenderer.drawString(EnumChatFormatting.GREEN + "Gang Balance: ",
                GuiMembersList.xPosition + (GuiMembersList.width - mc.fontRenderer.getStringWidth("Members")) / 2,
                GuiMembersList.yPosition - 13, 0xFFFFFF);
        mc.fontRenderer.setUnicodeFlag(false);


    }

    @Override
    protected void keyTyped(char Char, int Code) {
        super.keyTyped(Char, Code);
        GuiMembersList.keyTyped(Char, Code);
    }

    @Override
    protected void mouseClicked(int mouse_x, int mouse_y, int mouse_btn) {
        super.mouseClicked(mouse_x, mouse_y, mouse_btn);
    }

    @Override
    protected void actionPerformed(GuiButton btn) {
        super.actionPerformed(btn);
    }

    @Override
    public void initGui() {
        super.initGui();
        GuiMembersList = new GuiMembersList(mc, XPosition + (Width / 2) - 5, YPosition + 20, Width / 2, Height / 2, Gang);
    }
}
