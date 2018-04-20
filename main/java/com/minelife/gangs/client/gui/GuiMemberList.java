package com.minelife.gangs.client.gui;

import com.minelife.gangs.GangRole;
import com.minelife.util.SkinHelper;
import com.minelife.util.client.GuiHelper;
import com.minelife.util.client.GuiScrollableContent;
import com.minelife.util.server.NameFetcher;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import org.apache.commons.lang3.text.WordUtils;

import java.util.Map;
import java.util.UUID;

public class GuiMemberList extends GuiScrollableContent {

    private GuiGangMenu previousScreen;
    public Map<UUID, GangRole> members;
    public Map<UUID, Long> kills, deaths, rep;
    public UUID hoveringPlayer;

    public GuiMemberList(Minecraft mc, int x, int y, int width, int height, GuiGangMenu previousScreen) {
        super(mc, x, y, width, height);
        this.previousScreen = previousScreen;
        this.members = previousScreen.gang.getMembers();
        this.kills = previousScreen.gang.getKills();
        this.deaths = previousScreen.gang.getDeaths();
        this.rep = previousScreen.gang.getRep();
    }

    @Override
    public int getObjectHeight(int index) {
        return 16;
    }

    boolean foundOne = false;

    @Override
    public void drawObject(int index, int mouseX, int mouseY, boolean isHovering) {
        if(index == 0) foundOne = false;

        GlStateManager.color(175f / 255f, 175f / 255f, 175f / 255f, 1f);
        GlStateManager.disableTexture2D();
        GuiHelper.drawRect(0, 0, width, getObjectHeight(index));
        GlStateManager.color(0, 0, 0, 1);
        GuiHelper.drawRect(1, 1, width - 7, getObjectHeight(index) - 2);
        GlStateManager.color(1, 1, 1, 1);
        GlStateManager.enableTexture2D();

        UUID playerID = (UUID) members.keySet().toArray()[index];
        GangRole playerRole = members.get(playerID);
        ResourceLocation playerSkin = SkinHelper.loadSkin(playerID);
        String name = NameFetcher.asyncFetchClient(playerID);
        name = name == null ? "null" : name;

        mc.getTextureManager().bindTexture(playerSkin);
        Gui.drawScaledCustomSizeModalRect(4, 4, 8.0F, 8, 8, 8, 8, 8, 64.0F, 64.0F);
        Gui.drawScaledCustomSizeModalRect(4, 4, 40.0F, 8, 8, 8, 8, 8, 64.0F, 64.0F);

        mc.fontRenderer.drawString(!name.contains("Fetching...") ? playerRole.color + "[" + WordUtils.capitalizeFully(playerRole.name()) + playerRole.color + "] " + TextFormatting.GRAY + name : name, 18, 4, 0xFFFFFF);

        if (isHovering) {
            foundOne = true;
            hoveringPlayer = playerID;
        }

        if (!foundOne && index == getSize() - 1) hoveringPlayer = null;
    }

    @Override
    public int getSize() {
        return members.size();
    }

    @Override
    public void elementClicked(int index, int mouseX, int mouseY, boolean doubleClick) {
        if(doubleClick) {
            mc.displayGuiScreen(new GuiManageMember((UUID) members.keySet().toArray()[index], previousScreen));
        }
    }
}
