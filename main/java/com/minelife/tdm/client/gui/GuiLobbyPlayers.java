package com.minelife.tdm.client.gui;

import com.google.common.collect.Sets;
import com.minelife.util.SkinHelper;
import com.minelife.util.client.GuiHelper;
import com.minelife.util.client.GuiScrollableContent;
import com.minelife.util.server.NameFetcher;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

public class GuiLobbyPlayers extends GuiScreen {

    private int guiLeft, guiTop, guiWidth = 250, guiHeight = 230;
    public static Set<UUID> team1 = Sets.newTreeSet(), team2 = Sets.newTreeSet();
    private GuiPlayerList team1List, team2List;
    public static int secondsTillStart;
    private GuiLobby previousScreen;

    public GuiLobbyPlayers(GuiLobby previousScreen) {
        this.previousScreen = previousScreen;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        GuiHelper.drawDefaultBackground(guiLeft, guiTop, guiWidth, guiHeight);
        int dWheel = Mouse.getDWheel();
        team1List.draw(mouseX, mouseY, dWheel);
        team2List.draw(mouseX, mouseY, dWheel);
        fontRenderer.drawString(TextFormatting.UNDERLINE + "Team 1", team1List.x + 35, team1List.y - 12, GuiHelper.defaultTextColor);
        fontRenderer.drawString(TextFormatting.UNDERLINE + "Team 2", team2List.x + 35, team2List.y - 12, GuiHelper.defaultTextColor);
        drawCenteredString(fontRenderer, TextFormatting.RED + "Game starting in " + secondsTillStart + " seconds!", this.width / 2, this.guiTop + this.guiHeight - 20, 0xFFFFFF);
         super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if(keyCode == Keyboard.KEY_ESCAPE) {
            mc.displayGuiScreen(previousScreen);
        }
    }

    @Override
    public void initGui() {
        super.initGui();
        this.guiLeft = (this.width - guiWidth) / 2;
        this.guiTop = (this.height - guiHeight) / 2;
        this.team1List = new GuiPlayerList(mc, this.guiLeft + 5, this.guiTop + 5 + 25, 115, this.guiHeight - 60, team1);
        this.team2List = new GuiPlayerList(mc, this.guiLeft + 5 + (this.guiWidth / 2), this.guiTop + 5 + 25, 115, this.guiHeight - 60, team2);
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
    }

    public class GuiPlayerList extends GuiScrollableContent {

        public Set<UUID> players;

        public GuiPlayerList(Minecraft mc, int x, int y, int width, int height, Set<UUID> team) {
            super(mc, x, y, width, height);
            this.players = team;
        }

        @Override
        public int getObjectHeight(int index) {
            return 15;
        }

        @Override
        public void drawObject(int index, int mouseX, int mouseY, boolean isHovering) {
            UUID playerID = (UUID) players.toArray()[index];
            ResourceLocation playerSkin = SkinHelper.loadSkin(playerID);
            String name = NameFetcher.asyncFetchClient(playerID);
            name = name == null ? "Fetching..." : name;

            mc.getTextureManager().bindTexture(playerSkin);
            Gui.drawScaledCustomSizeModalRect(4, 4, 8.0F, 8, 8, 8, 8, 8, 64.0F, 64.0F);
            Gui.drawScaledCustomSizeModalRect(4, 4, 40.0F, 8, 8, 8, 8, 8, 64.0F, 64.0F);

            mc.fontRenderer.drawString(name, 18, 4, 0xFFFFFF);

        }

        @Override
        public int getSize() {
            return this.players.size();
        }

        @Override
        public void elementClicked(int index, int mouseX, int mouseY, boolean doubleClick) {

        }

        @Override
        public void drawSelectionBox(int index, int width, int height) {
//            super.drawSelectionBox(index, width, height);
        }
    }

}
