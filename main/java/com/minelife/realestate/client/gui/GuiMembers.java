package com.minelife.realestate.client.gui;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.minelife.realestate.Permission;
import com.minelife.util.client.*;
import com.minelife.util.server.NameFetcher;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Mouse;

import java.util.*;

public class GuiMembers extends GuiScreen implements INameReceiver {

    private GuiContent content;
    private GuiLoadingAnimation loadingAnimation;
    private List<Permission> playerPermissions;
    private Map<UUID, List<Permission>> members;
    private Map<UUID, String> names;
    private int xPosition, yPosition, bgWidth = 200, bgHeight = 200;

    public GuiMembers() {
    }

    public GuiMembers(Map<UUID, List<Permission>> members, List<Permission> playerPermissions) {
        this.members = members;
        this.playerPermissions = playerPermissions;
        this.names = Maps.newHashMap();
        members.keySet().forEach(uuid -> this.names.put(uuid, NameFetcher.asyncFetchClient(uuid, this)));
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

    @Override
    public void nameReceived(UUID uuid, String name) {
        this.names.put(uuid, name);
    }

    private class GuiContent extends GuiScrollableContent {

        private Map<UUID, List<GuiTickBox>> tickBoxMap;
        private int totalHeight = 0;

        public GuiContent(Minecraft mc, int xPosition, int yPosition, int width, int height) {
            super(mc, xPosition, yPosition, width, height);
            tickBoxMap = Maps.newHashMap();
            members.forEach((uuid, permissions) -> {
                totalHeight = fontRendererObj.FONT_HEIGHT;
                List<GuiTickBox> tickBoxList = Lists.newArrayList();
                playerPermissions.forEach(p -> tickBoxList.add(new GuiTickBox(mc, 20, totalHeight += GuiTickBox.HEIGHT, permissions.contains(p), p.name())));
            });
        }

        @Override
        public int getObjectHeight(int index) {
            return fontRendererObj.FONT_HEIGHT + members.get(members.keySet().toArray()[index]).size() * GuiTickBox.HEIGHT;
        }

        @Override
        public void drawObject(int index, int mouseX, int mouseY, boolean isHovering) {
            fontRendererObj.drawString(names.get(members.keySet().toArray()[index]), 5, 5, 0xFFFFFF);

        }

        @Override
        public int getSize() {
            return members.size();
        }

        @Override
        public void elementClicked(int index, int mouseX, int mouseY, boolean doubleClick) {

        }

        @Override
        public void drawBackground() {

        }

    }
}
