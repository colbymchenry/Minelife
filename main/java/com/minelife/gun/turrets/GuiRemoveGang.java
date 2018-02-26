package com.minelife.gun.turrets;

import com.minelife.gangs.Gang;
import com.minelife.gangs.client.gui.GuiGang;
import com.minelife.util.client.GuiUtil;
import ic2.core.util.Keyboard;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import java.util.UUID;

public class GuiRemoveGang extends GuiScreen {

    private String gangName;
    private GuiScreen previousScreen;
    private int xPosition, yPosition, bgWidth = 160, bgHeight = 70;

    public GuiRemoveGang(String gangName, GuiScreen previousScreen) {
        this.gangName = gangName;
        this.previousScreen = previousScreen;
    }

    @Override
    public void drawScreen(int mouse_x, int mouse_y, float f) {
        drawDefaultBackground();
        GuiUtil.drawDefaultBackground(xPosition, yPosition, bgWidth, bgHeight, GuiGang.BackgroundColor);

        fontRendererObj.drawString("Remove gang from WhiteList?",
                (this.width - fontRendererObj.getStringWidth("Remove gang from WhiteList?")) / 2, yPosition + 15, 0xFFFFFF);
        super.drawScreen(mouse_x, mouse_y, f);
    }

    @Override
    protected void actionPerformed(GuiButton btn) {
        if (btn.id == 1) {
            ((GuiTurret) previousScreen).WhiteListGang.StringList.remove(gangName);
            UUID toRemove = null;
            for (UUID uuid : ((GuiTurret) previousScreen).GangWhiteListUUIDs.keySet()) {
                if (((GuiTurret) previousScreen).GangWhiteListUUIDs.get(uuid).equalsIgnoreCase(gangName)) {
                    toRemove = uuid;
                }
            }

            if (toRemove != null)
                ((GuiTurret) previousScreen).GangWhiteListUUIDs.remove(toRemove);
        }

        Minecraft.getMinecraft().displayGuiScreen(previousScreen);
    }

    @Override
    protected void keyTyped(char p_73869_1_, int p_73869_2_) {
        if (p_73869_2_ == org.lwjgl.input.Keyboard.KEY_ESCAPE)
            Minecraft.getMinecraft().displayGuiScreen(previousScreen);
    }

    @Override
    public void initGui() {
        this.buttonList.clear();
        this.xPosition = (width - bgWidth) / 2;
        this.yPosition = (height - bgHeight) / 2;

        int xWidth = bgWidth / 2;

        this.buttonList.add(new GuiButton(0, xPosition + ((xWidth - 30) / 2), yPosition + 35, 30, 20, "No"));
        this.buttonList.add(new GuiButton(1, xPosition + xWidth + ((xWidth - 30) / 2), yPosition + 35, 30, 20, "Yes"));
    }
}
