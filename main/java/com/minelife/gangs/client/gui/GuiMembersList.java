package com.minelife.gangs.client.gui;

import com.minelife.gangs.Gang;
import com.minelife.util.StringHelper;
import com.minelife.util.client.GuiScrollableContent;
import net.minecraft.client.Minecraft;

import java.util.Map;
import java.util.UUID;

public class GuiMembersList extends GuiScrollableContent {

    private Gang gang;
    public Map<UUID, String> names;
    private GuiGangMenu previousScreen;

    public GuiMembersList(Minecraft mc, int xPosition, int yPosition, int width, int height, Gang gang, Map<UUID, String> names, GuiGangMenu previousScreen) {
        super(mc, xPosition, yPosition, width, height);
        this.gang = gang;
        this.names = names;
        this.previousScreen = previousScreen;
    }

    @Override
    public int getObjectHeight(int index) {
        return mc.fontRenderer.FONT_HEIGHT + 4;
    }

    @Override
    public void drawObject(int index, int mouseX, int mouseY, boolean isHovering) {
        UUID memberUUID = (UUID) names.keySet().toArray()[index];
        String name = names.getOrDefault(memberUUID, "Fetching...");
        String title = gang.getTitles().getOrDefault(memberUUID, null);
        mc.fontRenderer.drawString(StringHelper.ParseFormatting((title != null ? title + " " : "") + (gang.getOfficers().contains(memberUUID) ? "&6" : gang.getLeader().equals(memberUUID) ? "&c" : "") + name, '&'), 3, 2, 0xFFFFFF);
    }

    @Override
    public int getSize() {
        return names.size();
    }

    @Override
    public void elementClicked(int index, int mouseX, int mouseY, boolean doubleClick) {
        if (doubleClick) {
            UUID memberUUID = (UUID) names.keySet().toArray()[index];
            String name = names.getOrDefault(memberUUID, "Fetching...");
            previousScreen.setGuiMember(name, memberUUID, gang);
        }
    }
}
