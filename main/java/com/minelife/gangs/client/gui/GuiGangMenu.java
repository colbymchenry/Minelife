package com.minelife.gangs.client.gui;

import com.google.common.collect.Lists;
import com.minelife.gangs.Gang;
import com.minelife.gangs.GangPermission;
import com.minelife.util.NumberConversions;
import com.minelife.util.client.GuiHelper;
import com.minelife.util.server.NameFetcher;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.Rectangle;

import java.io.IOException;
import java.util.List;

public class GuiGangMenu extends GuiScreen {

    private static int guiWidth = 240, guiHeight = 190;
    private int guiLeft, guiTop;
    private Rectangle ownerBounds;
    protected Gang gang;
    private GuiMemberList memberList;
    private long balance;
    public List<Gang> alliances;

    public GuiGangMenu(Gang gang, long balance, List<Gang> alliances) {
        this.gang = gang;
        this.balance = balance;
        this.alliances = alliances;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        GuiHelper.drawDefaultBackground(guiLeft, guiTop, guiWidth, guiHeight);

        // draw gang name
        GlStateManager.pushMatrix();
        int stringWidth = fontRenderer.getStringWidth(TextFormatting.BOLD + gang.getName());
        GlStateManager.translate((this.width - stringWidth) / 2, this.guiTop - 20, zLevel);
        GlStateManager.translate(stringWidth / 2, fontRenderer.FONT_HEIGHT / 2, 0);
        GlStateManager.scale(2, 2, 2);
        GlStateManager.translate(-stringWidth / 2, -fontRenderer.FONT_HEIGHT / 2, 0);
        fontRenderer.drawString(TextFormatting.BOLD + gang.getName(), 0, 0, 0xef8228);
        GlStateManager.popMatrix();

        // draw stash balance
        int yOffset = 10;
        fontRenderer.drawString("Stash:", guiLeft + 70, guiTop + 10 + yOffset, GuiHelper.defaultTextColor);
        fontRenderer.drawString("$" + NumberConversions.format(balance < 0 ? 0 : balance), guiLeft + 150, guiTop + 10 + yOffset, GuiHelper.defaultTextColor);
        long kills = gang.getKills().values().stream().mapToLong(Long::valueOf).sum();
        fontRenderer.drawString("Total Kills:", guiLeft + 70, guiTop + 20 + yOffset, GuiHelper.defaultTextColor);
        fontRenderer.drawString("" + kills, guiLeft + 150, guiTop + 20 + yOffset, GuiHelper.defaultTextColor);
        fontRenderer.drawString("Total Deaths:", guiLeft + 70, guiTop + 30 + yOffset, GuiHelper.defaultTextColor);
        long deaths = gang.getDeaths().values().stream().mapToLong(Long::valueOf).sum();
        fontRenderer.drawString("" + deaths, guiLeft + 150, guiTop + 30 + yOffset, GuiHelper.defaultTextColor);
        fontRenderer.drawString("Total K/D:", guiLeft + 70, guiTop + 40 + yOffset, GuiHelper.defaultTextColor);
        fontRenderer.drawString("" + (kills / (deaths == 0 ? 1 : deaths)), guiLeft + 150, guiTop + 40 + yOffset, GuiHelper.defaultTextColor);

        int dWheel = Mouse.getDWheel();

        GlStateManager.color(1, 1, 1, 1);
        fontRenderer.drawString(TextFormatting.UNDERLINE + "Owner", ownerBounds.getX() + 1, ownerBounds.getY() - 14, GuiHelper.defaultTextColor);
        GlStateManager.color(1, 1, 1, 1);
        memberList.draw(mouseX, mouseY, dWheel);

        fontRenderer.drawString(TextFormatting.UNDERLINE + "Members", memberList.x + 1, memberList.y - 12, GuiHelper.defaultTextColor);

        super.drawScreen(mouseX, mouseY, partialTicks);

        if(memberList.hoveringPlayer != null) {
            long rep = memberList.rep.containsKey(memberList.hoveringPlayer) ? memberList.rep.get(memberList.hoveringPlayer) : 0;
            kills = memberList.kills.containsKey(memberList.hoveringPlayer) ? memberList.kills.get(memberList.hoveringPlayer) : 0;
            deaths = memberList.deaths.containsKey(memberList.hoveringPlayer) ? memberList.deaths.get(memberList.hoveringPlayer) : 0;
            drawHoveringText(Lists.newArrayList(
                    TextFormatting.GOLD + "Rep: " + rep,
                    TextFormatting.RED + "Kills: " + kills,
                    TextFormatting.DARK_RED + "Deaths: " + deaths,
                    TextFormatting.YELLOW + "K/D: " + (kills / (deaths == 0 ? 1 : deaths))),
                    mouseX, mouseY);
        }

        GlStateManager.color(1, 1, 1, 1);
        GlStateManager.disableLighting();

        if (gang.getOwner() != null) {
            kills = memberList.kills.containsKey(gang.getOwner()) ? memberList.kills.get(gang.getOwner()) : 0;
            deaths = memberList.deaths.containsKey(gang.getOwner()) ? memberList.deaths.get(gang.getOwner()) : 0;
            this.mc.getTextureManager().bindTexture(mc.player.getLocationSkin());
            Gui.drawScaledCustomSizeModalRect(ownerBounds.getX(), ownerBounds.getY(), 8.0F, 8, 8, 8,
                    ownerBounds.getWidth(), ownerBounds.getHeight(), 64.0F, 64.0F);
            Gui.drawScaledCustomSizeModalRect(ownerBounds.getX(), ownerBounds.getY(), 40.0F, 8, 8, 8,
                    ownerBounds.getWidth(), ownerBounds.getHeight(), 64.0F, 64.0F);
            if(ownerBounds.contains(mouseX, mouseY)) {
                drawHoveringText(Lists.newArrayList(
                        TextFormatting.DARK_RED + TextFormatting.ITALIC.toString() + NameFetcher.asyncFetchClient(gang.getOwner()),
                        TextFormatting.RED + "Kills: " + kills,
                        TextFormatting.GOLD + "Deaths: " + deaths,
                        TextFormatting.YELLOW + "K/D: " + (kills / (deaths == 0 ? 1 : deaths))),
                        mouseX, mouseY);
            }
        }

        GlStateManager.color(1, 1, 1, 1);
        GlStateManager.disableLighting();
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);
        if(button.id == 0) {
            mc.displayGuiScreen(new GuiAddMember(this));
        } else if (button.id == 2) {
            mc.displayGuiScreen(new GuiGangAlliances(this));
        } else if(button.id == 3) {
            mc.displayGuiScreen(new GuiSetupChallenge(this));
        }
    }

    @Override
    public void initGui() {
        super.initGui();
        this.guiLeft = (this.width - guiWidth) / 2;
        this.guiTop = (this.height - guiHeight + 20) / 2;
        this.ownerBounds = new Rectangle(this.guiLeft + 15, this.guiTop + 25, 32, 32);
        this.memberList = new GuiMemberList(mc, this.guiLeft + 5, this.guiTop + guiHeight / 2 + 15, guiWidth - 10, guiHeight / 2 - 20, this);
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(0, memberList.x + memberList.width - 21, memberList.y - 22, 20, 20, "+"));
        this.buttonList.add(new GuiButton(1, guiLeft - 22, guiTop + 2, 20, 20, "<"));
        this.buttonList.add(new GuiButton(2, guiLeft + guiWidth + 2, guiTop + 2, 20, 20, ">"));
        this.buttonList.add(new GuiButton(3, memberList.x + memberList.width - 85, memberList.y - 22, 60, 20, TextFormatting.YELLOW + "Challenge"));
        this.buttonList.get(0).enabled = gang.hasPermission(mc.player.getUniqueID(), GangPermission.INVITE);
        this.buttonList.get(1).enabled = false;
    }


    @Override
    public void updateScreen() {
        super.updateScreen();
    }
}
