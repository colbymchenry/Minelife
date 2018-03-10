package com.minelife.realestate.client.gui;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.minelife.Minelife;
import com.minelife.realestate.Permission;
import com.minelife.realestate.network.PacketGetMembers;
import com.minelife.realestate.network.PacketModifyMember;
import com.minelife.realestate.network.PacketRemoveMember;
import com.minelife.util.client.*;
import com.minelife.util.server.NameFetcher;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.util.*;

public class GuiMembers extends GuiScreen implements INameReceiver {

    public GuiModifyEstate parentScreen;
    private GuiContent content;
    private GuiLoadingAnimation loadingAnimation;
    private Set<Permission> playerPermissions;
    private Map<UUID, Set<Permission>> members;
    private Map<UUID, String> names;
    private GuiButton addBtn;
    private int estateID;
    private int xPosition, yPosition, bgWidth = 200, bgHeight = 200;
    private static ResourceLocation deleteTex = new ResourceLocation(Minelife.MOD_ID, "textures/gui/x.png");

    public GuiMembers(int estateID, GuiModifyEstate parentScreen) {
        Minelife.NETWORK.sendToServer(new PacketGetMembers(estateID));
        this.estateID = estateID;
        this.parentScreen = parentScreen;
    }

    public GuiMembers(Map<UUID, Set<Permission>> members, Set<Permission> playerPermissions, int estateID, GuiModifyEstate parentScreen) {
        this.members = members;
        this.members.remove(Minecraft.getMinecraft().thePlayer.getUniqueID());
        this.playerPermissions = playerPermissions;
        this.names = Maps.newHashMap();
        members.keySet().forEach(uuid -> this.names.put(uuid, NameFetcher.asyncFetchClient(uuid, this)));
        this.estateID = estateID;
        this.parentScreen = parentScreen;
    }

    @Override
    public void drawScreen(int x, int y, float f) {
        super.drawScreen(x, y, f);
        drawDefaultBackground();
        GuiUtil.drawDefaultBackground(xPosition, yPosition, bgWidth, bgHeight);
        addBtn.drawButton(mc, x, y);

        if (members == null) {
            loadingAnimation.drawLoadingAnimation();
            return;
        }

        content.draw(x, y, Mouse.getDWheel());
    }

    @Override
    protected void keyTyped(char keyChar, int keyCode) {
        if (keyCode == Keyboard.KEY_ESCAPE) Minecraft.getMinecraft().displayGuiScreen(parentScreen);

        if (members != null) content.keyTyped(keyChar, keyCode);
    }

    @Override
    protected void mouseClicked(int x, int y, int btn) {
        if (addBtn.mousePressed(mc, x, y)) {
            Minecraft.getMinecraft().displayGuiScreen(new GuiAddMember(this, estateID));
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
            content = new GuiContent(mc, xPosition, yPosition + 4, bgWidth, bgHeight - 6);

        addBtn = new GuiButton(0, xPosition + bgWidth + 2, yPosition, 50, 20, "Add");
    }

    @Override
    public void nameReceived(UUID uuid, String name) {
        this.names.put(uuid, name);
    }

    private class GuiContent extends GuiScrollableContent {

        private List messageBox = Lists.newArrayList();
        private Map<UUID, List<GuiTickBox>> tickBoxMap;
        private int totalHeight = 0;

        public GuiContent(Minecraft mc, int xPosition, int yPosition, int width, int height) {
            super(mc, xPosition, yPosition, width, height);
            this.drawScrollbarOnTop = false;
            messageBox.add("Remove?");
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
            return fontRendererObj.FONT_HEIGHT + 20 + ((List<GuiTickBox>) tickBoxMap.values().toArray()[index]).size() * GuiTickBox.HEIGHT;
        }

        @Override
        public void drawObject(int index, int mouseX, int mouseY, boolean isHovering) {
            fontRendererObj.drawStringWithShadow(names.get(members.keySet().toArray()[index]), 5, 5, 0xFFFFFF);

            mc.getTextureManager().bindTexture(deleteTex);
            GuiUtil.drawImage(this.width - 25, 2, 16, 16);

            if (mouseX >= this.width - 25 && mouseX <= (this.width - 25) + 16 && mouseY >= 2 && mouseY <= 2 + 16) {
                GL11.glDisable(GL11.GL_SCISSOR_TEST);
                drawHoveringText(messageBox, mouseX, mouseY, fontRendererObj);
                RenderHelper.disableStandardItemLighting();
                GL11.glEnable(GL11.GL_SCISSOR_TEST);
            }

            ((List<GuiTickBox>) tickBoxMap.values().toArray()[index]).forEach(GuiTickBox::drawTickBox);
            ((List<GuiTickBox>) tickBoxMap.values().toArray()[index]).forEach(tickBox -> fontRendererObj.drawString(tickBox.key, 60, tickBox.yPosition + 5, 0x232323));
        }

        @Override
        public int getSize() {
            return members.size();
        }

        @Override
        public void elementClicked(int index, int mouseX, int mouseY, boolean doubleClick) {
            ((List<GuiTickBox>) tickBoxMap.values().toArray()[index]).forEach(tB -> {
                if (tB.mouseClicked(mouseX, mouseY)) {
                    UUID memberUUID = (UUID) members.keySet().toArray()[index];
                    updatePlayer(memberUUID);
                }
            });

            if (mouseX >= this.width - 25 && mouseX <= (this.width - 25) + 16 && mouseY >= 2 && mouseY <= 2 + 16) {
                Minelife.NETWORK.sendToServer(new PacketRemoveMember((UUID) members.keySet().toArray()[index], estateID));
            }
        }

        @Override
        public void drawBackground() {

        }

        @Override
        public void drawSelectionBox(int index, int width, int height) {

        }

        private void updatePlayer(UUID player) {
            Set<Permission> permissions = Sets.newTreeSet();
            tickBoxMap.get(player).forEach(tB -> {
                if (tB.isChecked()) permissions.add(Permission.valueOf(tB.key));
            });
            Minelife.NETWORK.sendToServer(new PacketModifyMember(estateID, player, permissions));
        }
    }
}
