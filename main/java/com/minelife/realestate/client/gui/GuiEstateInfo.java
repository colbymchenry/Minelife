package com.minelife.realestate.client.gui;

import com.google.common.collect.Maps;
import com.minelife.realestate.EnumPermission;
import com.minelife.realestate.Estate;
import com.minelife.util.client.GuiScrollableContent;
import com.minelife.util.client.GuiTickBox;
import com.minelife.util.client.GuiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.util.Map;
import java.util.Set;

public class GuiEstateInfo extends GuiScreen {

    private Estate estate;
    private Set<EnumPermission> permsAllowedToChange;
    private Set<EnumPermission> getPermsAllowedToChangeEnabled;
    private Map<EnumPermission, GuiTickBox> permissionTickBoxSet, allowedToChangeTickBoxSet;
    private int xPosition, yPosition, bgWidth = 200, bgHeight = 200;
    private GuiContent guiContent;
    private Color bgColor = new Color(108, 108, 108, 255);
    private boolean showPermsAllowedToChange;

    // Only show the "PermsAllowedToChange" tick boxs if it is inside one of their estate
    public GuiEstateInfo(Estate estate, Set<EnumPermission> permsAllowedToChange, Set<EnumPermission> getPermsAllowedToChangeEnabled, boolean showPermsAllowedToChange)
    {
        this.estate = estate;
        this.permsAllowedToChange = permsAllowedToChange;
        this.getPermsAllowedToChangeEnabled = getPermsAllowedToChangeEnabled;
        this.showPermsAllowedToChange = showPermsAllowedToChange;
    }

    @Override
    public void drawScreen(int x, int y, float f)
    {
        drawDefaultBackground();
        GuiUtil.drawDefaultBackground(xPosition, yPosition, bgWidth, bgHeight, bgColor);
        guiContent.draw(x, y, Mouse.getDWheel());
        super.drawScreen(x, y, f);
    }

    @Override
    protected void keyTyped(char keyChar, int keyCode)
    {
        super.keyTyped(keyChar, keyCode);
        this.guiContent.keyTyped(keyChar, keyCode);
    }

    @Override
    public void initGui()
    {
        super.initGui();
        xPosition = (width - bgWidth) / 2;
        yPosition = (height - bgHeight) / 2;
        guiContent = new GuiContent(mc, xPosition, yPosition, bgWidth, bgHeight);
    }

    @Override
    public void updateScreen()
    {
        super.updateScreen();
    }

    private class GuiContent extends GuiScrollableContent {

        private int widthOffset = 30;
        private int columnWidth = (width - widthOffset) / (showPermsAllowedToChange ? 3 : 2);

        public GuiContent(Minecraft mc, int xPosition, int yPosition, int width, int height)
        {
            super(mc, xPosition, yPosition, width, height);
            permissionTickBoxSet = Maps.newHashMap();
            allowedToChangeTickBoxSet = Maps.newHashMap();

            int y = 200;
            for (EnumPermission p : permsAllowedToChange) {
                permissionTickBoxSet.put(p, new GuiTickBox(mc, (columnWidth * 1) + ((columnWidth - GuiTickBox.WIDTH) / 2) + (widthOffset / 2), y, estate.getPermissions().contains(p)));
                if (showPermsAllowedToChange)
                    allowedToChangeTickBoxSet.put(p, new GuiTickBox(mc, (columnWidth * 2) + ((columnWidth - GuiTickBox.WIDTH) / 2) + (widthOffset / 2), y, getPermsAllowedToChangeEnabled.contains(p)));
                y += 30;
            }
        }

        @Override
        public int getObjectHeight(int index)
        {
            return 400;
        }

        @Override
        public void drawObject(int index, int mouseX, int mouseY, boolean isHovering)
        {
            String permColumn = EnumChatFormatting.BOLD.toString() + EnumChatFormatting.UNDERLINE.toString() + "Permission";
            fontRendererObj.drawString(permColumn, (columnWidth * 0) + ((columnWidth - fontRendererObj.getStringWidth(permColumn)) / 2) + (widthOffset / 2), 180, 0xFFFFFF);
            String allowColumn = EnumChatFormatting.BOLD.toString() + EnumChatFormatting.UNDERLINE.toString() + "Allow";
            fontRendererObj.drawString(allowColumn, (columnWidth * 1) + ((columnWidth - fontRendererObj.getStringWidth(allowColumn)) / 2) + (widthOffset / 2), 180, 0xFFFFFF);

            if (showPermsAllowedToChange) {
                String modifyColumn = EnumChatFormatting.BOLD.toString() + EnumChatFormatting.UNDERLINE.toString() + "Can Modify";
                fontRendererObj.drawString(modifyColumn, (columnWidth * 2) + ((columnWidth - fontRendererObj.getStringWidth(modifyColumn)) / 2) + (widthOffset / 2), 180, 0xFFFFFF);
            }

            permissionTickBoxSet.forEach((p, t) -> {
                fontRendererObj.drawString(p.name(), (columnWidth * 0) + ((columnWidth - fontRendererObj.getStringWidth(p.name())) / 2) + (widthOffset / 2), t.yPosition + 5, 0xFFFFFF);
                t.drawTickBox();
            });

            if (showPermsAllowedToChange) {
                allowedToChangeTickBoxSet.forEach((p, t) -> {
                    t.drawTickBox();
                });
            }
        }

        @Override
        public int getSize()
        {
            return 1;
        }

        @Override
        public void elementClicked(int index, int mouseX, int mouseY, boolean doubleClick)
        {
            permissionTickBoxSet.forEach((p, t) -> t.mouseClicked(mouseX, mouseY));
            if (showPermsAllowedToChange)
                allowedToChangeTickBoxSet.forEach((p, t) -> t.mouseClicked(mouseX, mouseY));
        }

        @Override
        public void drawBackground()
        {

        }

        @Override
        public void drawSelectionBox(int index, int width, int height)
        {

        }
    }
}
