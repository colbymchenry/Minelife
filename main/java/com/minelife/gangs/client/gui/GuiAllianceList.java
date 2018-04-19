package com.minelife.gangs.client.gui;

import com.minelife.gangs.Gang;
import com.minelife.util.client.GuiScrollableContent;
import net.minecraft.client.Minecraft;

import java.util.List;

public class GuiAllianceList extends GuiScrollableContent {

    private List<Gang> alliances;

    public GuiAllianceList(Minecraft mc, int x, int y, int width, int height, List<Gang> alliances) {
        super(mc, x, y, width, height);
        this.alliances = alliances;
    }

    @Override
    public int getObjectHeight(int index) {
        return 16;
    }

    @Override
    public void drawObject(int index, int mouseX, int mouseY, boolean isHovering) {
        mc.fontRenderer.drawString(alliances.get(index).getName(), 4, 4, 0xFFFFFF);
    }

    @Override
    public int getSize() {
        return alliances.size();
    }

    @Override
    public void elementClicked(int index, int mouseX, int mouseY, boolean doubleClick) {

    }
}
