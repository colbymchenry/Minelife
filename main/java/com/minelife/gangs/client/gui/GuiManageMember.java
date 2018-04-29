package com.minelife.gangs.client.gui;

import com.google.common.collect.Lists;
import com.minelife.Minelife;
import com.minelife.gangs.GangPermission;
import com.minelife.gangs.GangRole;
import com.minelife.gangs.network.PacketRemoveMember;
import com.minelife.gangs.network.PacketSetMemberRole;
import com.minelife.util.client.GuiHelper;
import com.minelife.util.server.NameFetcher;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.text.TextFormatting;
import org.apache.commons.lang3.text.WordUtils;
import org.lwjgl.input.Keyboard;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class GuiManageMember extends GuiScreen {

    private int guiLeft, guiTop, guiWidth = 120, guiHeight = 165;
    private GuiGangMenu previousScreen;
    private UUID memberID;

    public GuiManageMember(UUID memberID, GuiGangMenu previousScreen) {
        this.memberID = memberID;
        this.previousScreen = previousScreen;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        GuiHelper.drawDefaultBackground(guiLeft, guiTop, guiWidth, guiHeight);

        // draw player name
        GlStateManager.pushMatrix();
        int stringWidth = fontRenderer.getStringWidth(TextFormatting.BOLD + NameFetcher.asyncFetchClient(memberID));
        GlStateManager.translate((this.width - stringWidth) / 2, this.guiTop - 20, zLevel);
        GlStateManager.translate(stringWidth / 2, fontRenderer.FONT_HEIGHT / 2, 0);
        GlStateManager.scale(2, 2, 2);
        GlStateManager.translate(-stringWidth / 2, -fontRenderer.FONT_HEIGHT / 2, 0);
        fontRenderer.drawString(TextFormatting.BOLD + NameFetcher.asyncFetchClient(memberID), 0, 0, 0xef8228);
        GlStateManager.popMatrix();

        super.drawScreen(mouseX, mouseY, partialTicks);

        buttonList.forEach(btn -> {
            if(btn.isMouseOver() && !btn.displayString.equalsIgnoreCase("Kick")) {
                GangRole role = GangRole.valueOf(btn.displayString.toUpperCase());
                List<String> lines = Lists.newArrayList();
                lines.add("Permissions:");
                role.permissions.forEach(perm -> lines.add("   - " + WordUtils.capitalizeFully(perm.name().replace("_", " "))));
                drawHoveringText(lines, mouseX, mouseY);
            }
        });

    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);
        if(button.id == -1) {
            Minelife.getNetwork().sendToServer(new PacketRemoveMember(memberID));
        } else {
            Minelife.getNetwork().sendToServer(new PacketSetMemberRole(memberID, GangRole.values()[button.id]));
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        if(keyCode == Keyboard.KEY_ESCAPE) mc.displayGuiScreen(previousScreen);
    }

    @Override
    public void initGui() {
        super.initGui();
        this.guiLeft = (this.width - guiWidth) / 2;
        this.guiTop = (this.height - guiHeight + 10) / 2;

        buttonList.clear();
        for (int i = 0; i < GangRole.values().length - 1; i++) {
            buttonList.add(new GuiButton(i, (this.width - 75) / 2, guiTop + (25 * i) + 10, 75, 20, WordUtils.capitalizeFully(GangRole.values()[i].name())));
        }

        buttonList.add(new GuiButton(-1, (this.width - 75) / 2, guiTop + (25 * (GangRole.values().length - 1)) + 10, 75, 20, "Kick"));
        buttonList.get(GangRole.values().length - 1).enabled = previousScreen.gang.hasPermission(mc.player.getUniqueID(), GangPermission.KICK_MEMBERS);
    }
}
