package com.minelife.police.client;

import com.minelife.police.Charge;
import com.minelife.util.NumberConversions;
import com.minelife.util.client.GuiScrollableContent;
import net.minecraft.util.EnumChatFormatting;

import java.util.List;

public class GuiChargeList extends GuiScrollableContent {

    public List<Charge> chargeList;
    public boolean unicodeFlag = false;

    public GuiChargeList(int xPosition, int yPosition, int width, int height, List<Charge> charges) {
        super(xPosition, yPosition, width, height);
        this.chargeList = charges;
    }

    @Override
    public int getObjectHeight(int index) {
        if(unicodeFlag) mc.fontRenderer.setUnicodeFlag(true);
        int line1 =  mc.fontRenderer.listFormattedStringToWidth(getText(chargeList.get(index))[0], width - 5).size() * mc.fontRenderer.FONT_HEIGHT;
        int line2 =  mc.fontRenderer.listFormattedStringToWidth(getText(chargeList.get(index))[1], width - 5).size() * mc.fontRenderer.FONT_HEIGHT;
        int line3 =  mc.fontRenderer.listFormattedStringToWidth(getText(chargeList.get(index))[2], width - 5).size() * mc.fontRenderer.FONT_HEIGHT;
        if(unicodeFlag) mc.fontRenderer.setUnicodeFlag(false);
        return line1 + line2 + line3;
    }

    @Override
    public void drawObject(int index, int mouseX, int mouseY, boolean isHovering) {
        if(unicodeFlag) mc.fontRenderer.setUnicodeFlag(true);
        int line1 =  mc.fontRenderer.listFormattedStringToWidth(getText(chargeList.get(index))[0], width - 5).size() * mc.fontRenderer.FONT_HEIGHT;
        int line2 =  mc.fontRenderer.listFormattedStringToWidth(getText(chargeList.get(index))[1], width - 5).size() * mc.fontRenderer.FONT_HEIGHT;

        mc.fontRenderer.drawSplitString(getText(chargeList.get(index))[0], 2, 0, width - 5, 0xFFFFFF);
        mc.fontRenderer.drawSplitString(getText(chargeList.get(index))[1], 2, line1, width - 5, 0xFFFFFF);
        mc.fontRenderer.drawSplitString(getText(chargeList.get(index))[2], 2, line1 + line2, width - 5, 0xFFFFFF);

        if(unicodeFlag) mc.fontRenderer.setUnicodeFlag(false);
    }

    @Override
    public int getSize() {
        return chargeList.size();
    }

    @Override
    public void elementClicked(int index, int mouseX, int mouseY, boolean doubleClick) {

    }

    private String[] getText(Charge charge) {
        String[] lines = new String[]{charge.counts + " count(s) of " + charge.description + ".", EnumChatFormatting.BOLD.toString() + EnumChatFormatting.GREEN.toString() + " Bail: $" + NumberConversions.formatter.format(charge.bail), EnumChatFormatting.GOLD + " Jail Time: " + (charge.jailTime / 20) + " mc days."};
        return lines;
    }
}
