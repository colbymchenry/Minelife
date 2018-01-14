package com.minelife.gangs.client.gui;

import com.google.common.collect.Maps;
import com.minelife.gangs.Gang;
import com.minelife.util.StringHelper;
import com.minelife.util.client.GuiScrollableContent;
import com.minelife.util.client.INameReceiver;
import com.minelife.util.server.NameFetcher;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

import java.util.Map;
import java.util.UUID;

public class GuiMembersList extends GuiScrollableContent implements INameReceiver {

    private Gang gang;
    private static final Map<UUID, String> names = Maps.newHashMap();

    public GuiMembersList(Minecraft mc, int xPosition, int yPosition, int width, int height, Gang gang) {
        super(mc, xPosition, yPosition, width, height);
        this.gang = gang;
    }

    @Override
    public int getObjectHeight(int index) {
        return mc.fontRenderer.FONT_HEIGHT + 4;
    }

    @Override
    public void drawObject(int index, int mouseX, int mouseY, boolean isHovering) {
        UUID memberUUID = (UUID) gang.getMembers().toArray()[index];
        String name = names.containsKey(memberUUID) ? names.get(memberUUID) : NameFetcher.asyncFetchClient(memberUUID, this);
        String title = gang.getTitles().containsKey(memberUUID) ? gang.getTitles().get(memberUUID) : null;

        mc.fontRenderer.drawString(StringHelper.ParseFormatting((title != null? title + " " : "") + name, '&'), 3, 2, 0xFFFFFF);
    }

    @Override
    public int getSize() {
        return gang.getMembers().size();
    }

    @Override
    public void elementClicked(int index, int mouseX, int mouseY, boolean doubleClick) {

    }

    @Override
    public void nameReceived(UUID uuid, String name) {

    }
}
