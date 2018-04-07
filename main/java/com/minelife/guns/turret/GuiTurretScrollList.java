package com.minelife.guns.turret;

import com.google.common.collect.Lists;
import com.minelife.util.client.GuiScrollableContent;
import net.minecraft.client.Minecraft;

import java.util.Arrays;
import java.util.List;

public class GuiTurretScrollList extends GuiScrollableContent {

    public List<String> StringList;

    public GuiTurretScrollList(Minecraft mc, int xPosition, int yPosition, int width, int height, String... objects) {
        super(mc, xPosition, yPosition, width, height);
        StringList = Lists.newArrayList();
        StringList.addAll(Arrays.asList(objects));
    }

    @Override
    public int getObjectHeight(int index) {
        return mc.fontRenderer.FONT_HEIGHT + 2;
    }

    @Override
    public void drawObject(int index, int mouseX, int mouseY, boolean isHovering) {
        if(StringList.get(index) != null)
            mc.fontRenderer.drawString(StringList.get(index), 2, 2, 0xFFFFFF);
    }

    @Override
    public int getSize() {
        return StringList.size();
    }

    @Override
    public void elementClicked(int index, int mouseX, int mouseY, boolean doubleClick) {

    }
}