package com.minelife.realestate.client.gui;

import com.google.common.collect.Lists;
import com.minelife.realestate.Estate;
import com.minelife.realestate.PlayerPermission;
import com.minelife.util.NumberConversions;
import com.minelife.util.client.GuiHelper;
import com.minelife.util.client.GuiScrollableContent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.text.TextFormatting;
import org.apache.commons.lang3.text.WordUtils;
import org.lwjgl.input.Mouse;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class GuiReceptionist extends GuiScreen {

    private Map<Estate, Set<PlayerPermission>> estates;
    private int guiLeft, guiTop, guiWidth = 200, guiHeight = 200;
    private EstateScrollList estateScrollList;

    public GuiReceptionist(Map<Estate, Set<PlayerPermission>> estates) {
        this.estates = estates;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        GuiHelper.drawDefaultBackground(guiLeft, guiTop, guiWidth, guiHeight);
        estateScrollList.draw(mouseX, mouseY, Mouse.getDWheel());
        if(estateScrollList.hoveringEstate != null) {
            Estate estate = estateScrollList.hoveringEstate;
            List<String> tip = Lists.newArrayList();
            tip.add("Rent: $" + NumberConversions.format(estate.getRentPrice()));
            tip.add("Rent Period: " + (estate.getRentPeriod() * 20) + " minutes");
            String renterPerms = estates.get(estate).isEmpty() ? "None" : "";
            for (PlayerPermission permission : estates.get(estate)) renterPerms += WordUtils.capitalizeFully(permission.name().replace("_", " ")) + "/";
            tip.add("Renter Perms: " + (renterPerms.contains("/") ? renterPerms.substring(0, renterPerms.length() - 1) : renterPerms));
            drawHoveringText(tip, mouseX, mouseY);
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void initGui() {
        super.initGui();
        this.guiLeft = (this.width - guiWidth) / 2;
        this.guiTop = (this.height - guiHeight) / 2;
        estateScrollList = new EstateScrollList(mc, guiLeft + 5, guiTop + 5, guiWidth - 10, guiHeight - 10);
    }

    class EstateScrollList extends GuiScrollableContent {

        public Estate hoveringEstate;

        public EstateScrollList(Minecraft mc, int x, int y, int width, int height) {
            super(mc, x, y, width, height);
        }

        @Override
        public int getObjectHeight(int index) {
            return 15;
        }

        boolean foundOne = false;

        @Override
        public void drawObject(int index, int mouseX, int mouseY, boolean isHovering) {
            if(index == 0) foundOne = false;
            Estate estate = (Estate) estates.keySet().toArray()[index];

            fontRenderer.drawString((estate.getRenterID() == null ? TextFormatting.GREEN : TextFormatting.RED) + estate.getIdentifier(), 3, 4, 0xFFFFFF);

            if (isHovering) {
                foundOne = true;
                hoveringEstate = estate;
            }

            if (!foundOne && index == getSize() - 1) hoveringEstate = null;
        }

        @Override
        public int getSize() {
            return estates.size();
        }

        @Override
        public void elementClicked(int index, int mouseX, int mouseY, boolean doubleClick) {
            if(doubleClick) {
                mc.displayGuiScreen(new GuiBuyEstate((Estate) estates.keySet().toArray()[index]));
            }
        }
    }
}
