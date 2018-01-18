package com.minelife.gun.turrets;

import com.minelife.util.client.GuiScrollableContent;
import net.minecraft.client.Minecraft;

public class GuiTurretScrollList extends GuiScrollableContent {

    String[] StringArray;

    public GuiTurretScrollList(Minecraft mc, int xPosition, int yPosition, int width, int height, String... objects) {
        super(mc, xPosition, yPosition, width, height);
        StringArray = objects;
    }

    @Override
    public int getObjectHeight(int index) {
        return mc.fontRenderer.FONT_HEIGHT;
    }

    @Override
    public void drawObject(int index, int mouseX, int mouseY, boolean isHovering) {
        if(StringArray[index] != null)
            mc.fontRenderer.drawString(StringArray[index], 0, 0, 0xFFFFFF);
    }

    @Override
    public int getSize() {
        return StringArray.length;
    }

    @Override
    public void elementClicked(int index, int mouseX, int mouseY, boolean doubleClick) {

    }
}
