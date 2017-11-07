package com.minelife.realestate.client.gui;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.minelife.Minelife;
import com.minelife.realestate.Permission;
import com.minelife.realestate.network.PacketGetMembers;
import com.minelife.util.client.*;
import com.minelife.util.server.NameFetcher;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Mouse;

import java.util.*;

public class GuiMembers extends GuiScreen implements INameReceiver {

    private GuiContent content;
    private GuiLoadingAnimation loadingAnimation;
    private Set<Permission> playerPermissions;
    private Map<UUID, Set<Permission>> members;
    private Map<UUID, String> names;
    private GuiButton addBtn, removeBtn;
    private int xPosition, yPosition, bgWidth = 200, bgHeight = 200;

    public GuiMembers(int estateID) {
        Minelife.NETWORK.sendToServer(new PacketGetMembers(estateID));
    }

    public GuiMembers(Map<UUID, Set<Permission>> members, Set<Permission> playerPermissions) {
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
        addBtn.drawButton(mc, x, y);
        removeBtn.drawButton(mc, x, y);

        if (members == null) {
            loadingAnimation.drawLoadingAnimation();
            return;
        }

        content.draw(x, y, Mouse.getDWheel());
    }

    @Override
    protected void keyTyped(char keyChar, int keyCode) {
        super.keyTyped(keyChar, keyCode);

        if (members != null) content.keyTyped(keyChar, keyCode);
    }

    @Override
    protected void mouseClicked(int x, int y, int btn) {
        if(addBtn.mousePressed(mc, x, y)) {
            Minecraft.getMinecraft().displayGuiScreen(new GuiAddMember(this));
        } else if(removeBtn.mousePressed(mc, x, y)) {

        }
    }

    @Override
    public void initGui() {
        super.initGui();
        xPosition = (this.width - bgWidth) / 2;
        yPosition = (this.height - bgHeight) / 2;
        if (this.members == null)
            loadingAnimation = new GuiLoadingAnimation(xPosition + ((bgWidth - 64) / 2), yPosition + ((bgHeight - 64) / 2), 64, 64);
        else
            content = new GuiContent(mc, xPosition, yPosition, bgWidth, bgHeight);

        addBtn = new GuiButton(0, xPosition + bgWidth + 2, yPosition, 50, 20, "Add");
        removeBtn = new GuiButton(1, xPosition + bgWidth + 2, yPosition + 25, 50, 20, "Remove");
        removeBtn.enabled = false;
    }

    @Override
    public void updateScreen() {
        if(content != null) {
            removeBtn.enabled = content.selected != -1;
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
                tickBoxMap.put(uuid, tickBoxList);
            });
        }

        @Override
        public int getObjectHeight(int index) {
            return fontRendererObj.FONT_HEIGHT + members.get(members.keySet().toArray()[index]).size() * GuiTickBox.HEIGHT;
        }

        @Override
        public void drawObject(int index, int mouseX, int mouseY, boolean isHovering) {
            fontRendererObj.drawString(names.get(members.keySet().toArray()[index]), 5, 5, 0xFFFFFF);
            tickBoxMap.get(index).forEach(GuiTickBox::drawTickBox);
        }

        @Override
        public int getSize() {
            return members.size();
        }

        @Override
        public void elementClicked(int index, int mouseX, int mouseY, boolean doubleClick) {
            tickBoxMap.get(index).forEach(tB -> tB.mouseClicked(mouseX, mouseY));
        }

        @Override
        public void drawBackground() {

        }

    }
}
