package com.minelife.realestate.client.gui;

import com.minelife.realestate.EnumPermission;
import com.minelife.realestate.Member;
import com.minelife.util.client.GuiScrollableContent;
import com.minelife.util.client.GuiTickBox;
import com.minelife.util.client.GuiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class GuiMembers extends GuiScreen {

    private Color bgColor = new Color(108, 108, 108, 255);
    private int xPosition, yPosition, bgWidth = 200, beHeight = 256;
    private Set<Member> members;
    private Set<EnumPermission> permsAllowedToChange;
    private UUID estateUUID;
    private GuiContent guiContent;
    private GuiPermissions guiPermissions;

    private GuiMembers(Set<Member> members, UUID estateUUID, Set<EnumPermission> permsAllowedToChange)
    {
        this.members = members;
        this.estateUUID = estateUUID;
        this.permsAllowedToChange = permsAllowedToChange;
    }

    @Override
    public void drawScreen(int x, int y, float f)
    {
        drawDefaultBackground();
        GuiUtil.drawDefaultBackground(xPosition, yPosition, bgWidth, beHeight, bgColor);
        int dWheel = Mouse.getDWheel();
        guiContent.draw(x, y, dWheel);
        guiPermissions.draw(x, y, dWheel);
        super.drawScreen(x, y, f);
    }

    @Override
    protected void keyTyped(char keyChar, int keyCode)
    {
        super.keyTyped(keyChar, keyCode);
        guiContent.keyTyped(keyChar, keyCode);
        guiPermissions.keyTyped(keyChar, keyCode);
    }

    @Override
    public void initGui()
    {
        super.initGui();
        this.xPosition = (super.width - bgWidth) / 2;
        this.yPosition = (super.height - beHeight) / 2;
        guiContent = new GuiContent(mc, xPosition - (bgWidth / 2), yPosition, bgWidth, beHeight);
        guiPermissions = new GuiPermissions(mc, xPosition + (bgWidth / 2), yPosition, bgWidth, beHeight);
    }

    @Override
    public void updateScreen()
    {
        super.updateScreen();
    }

    private class GuiContent extends GuiScrollableContent {

        public GuiContent(Minecraft mc, int xPosition, int yPosition, int width, int height)
        {
            super(mc, xPosition, yPosition, width, height);
        }

        @Override
        public int getObjectHeight(int index)
        {
            return fontRendererObj.FONT_HEIGHT;
        }

        @Override
        public void drawObject(int index, int mouseX, int mouseY, boolean isHovering)
        {
            fontRendererObj.drawString(((Member) members.toArray()[index]).playerName, 0, 0, 0xFFFFFF);
        }

        @Override
        public int getSize()
        {
            return members.size();
        }

        @Override
        public void elementClicked(int index, int mouseX, int mouseY, boolean doubleClick)
        {
            guiPermissions.memberSelected((Member) members.toArray()[index]);
        }

        @Override
        public void drawBackground()
        {

        }
    }

    private class GuiPermissions extends GuiScrollableContent {

        private Member selectedMember;
        private Map<EnumPermission, GuiTickBox> guiTickBoxMap;

        public GuiPermissions(Minecraft mc, int xPosition, int yPosition, int width, int height)
        {
            super(mc, xPosition, yPosition, width, height);
        }

        @Override
        public int getObjectHeight(int index)
        {
            if (selectedMember == null) return beHeight;
            return guiTickBoxMap.size() * 40;
        }

        @Override
        public void drawObject(int index, int mouseX, int mouseY, boolean isHovering)
        {
            guiTickBoxMap.forEach((p, t) -> t.drawTickBox());
        }

        @Override
        public int getSize()
        {
            return 1;
        }

        @Override
        public void elementClicked(int index, int mouseX, int mouseY, boolean doubleClick)
        {
            guiTickBoxMap.forEach((p, t) -> t.mouseClicked(mouseX, mouseY));
        }

        @Override
        public void drawSelectionBox(int index, int width, int height)
        {

        }

        @Override
        public void drawBackground()
        {

        }

        public void memberSelected(Member member)
        {
            this.guiTickBoxMap.clear();
            this.selectedMember = member;
            Set<EnumPermission> memberPerms = member.permissions;
            int y = 0;
            for (EnumPermission p : permsAllowedToChange) {
                this.guiTickBoxMap.put(p, new GuiTickBox(mc, 0, y += 40, memberPerms.contains(p)));
            }
        }
    }
}
