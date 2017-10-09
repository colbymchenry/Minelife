package com.minelife.realestate.client.gui;

import com.minelife.realestate.Permission;
import com.minelife.util.client.GuiLoadingAnimation;
import com.minelife.util.client.GuiScrollableContent;
import com.minelife.util.client.GuiTickBox;
import com.minelife.util.client.GuiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Mouse;

import java.util.*;

public class GuiMembers extends GuiScreen {

    private GuiContent content;
    private GuiLoadingAnimation loadingAnimation;
    private Map<UUID, List<Permission>> members;
    private int xPosition, yPosition, bgWidth = 200, bgHeight = 200;

    public GuiMembers() {
    }

    public GuiMembers(Map<UUID, List<Permission>> members) {
        this.members = members;
    }

    @Override
    public void drawScreen(int x, int y, float f) {
        super.drawScreen(x, y, f);
        drawDefaultBackground();
        GuiUtil.drawDefaultBackground(xPosition, yPosition, bgWidth, bgHeight);
        if(members == null) {
            loadingAnimation.drawLoadingAnimation();
            return;
        }

        content.draw(x, y, Mouse.getDWheel());
    }

    @Override
    protected void keyTyped(char keyChar, int keyCode) {
        super.keyTyped(keyChar, keyCode);

        if(members != null) content.keyTyped(keyChar, keyCode);
    }

    @Override
    public void initGui() {
        super.initGui();
        if(this.members == null) {
            loadingAnimation = new GuiLoadingAnimation(this.width / 2, this.height / 2, 64, 64);
        } else {
            xPosition = (this.width - bgWidth) / 2;
            yPosition = (this.height - bgHeight) / 2;
            content = new GuiContent(mc, xPosition, yPosition, bgWidth, bgHeight);
        }
    }

    private class GuiContent extends GuiScrollableContent {

        public GuiContent(Minecraft mc, int xPosition, int yPosition, int width, int height) {
            super(mc, xPosition, yPosition, width, height);
        }

        @Override
        public int getObjectHeight(int index) {
            return 25 + ((List<Permission>)Arrays.asList(members.values().toArray()).get(index)).size() * GuiTickBox.HEIGHT;
        }

        @Override
        public void drawObject(int index, int mouseX, int mouseY, boolean isHovering) {

        }

        @Override
        public int getSize() {
            return 0;
        }

        @Override
        public void elementClicked(int index, int mouseX, int mouseY, boolean doubleClick) {

        }

        @Override
        public void drawBackground() {

        }

    }
}
