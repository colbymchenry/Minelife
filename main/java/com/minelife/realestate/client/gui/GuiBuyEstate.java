package com.minelife.realestate.client.gui;

import com.google.common.collect.Sets;
import com.minelife.Minelife;
import com.minelife.realestate.Estate;
import com.minelife.realestate.EstateProperty;
import com.minelife.realestate.PlayerPermission;
import com.minelife.realestate.network.PacketPurchaseEstate;
import com.minelife.realestate.network.PacketUpdateEstate;
import com.minelife.util.NumberConversions;
import com.minelife.util.client.GuiHelper;
import com.minelife.util.client.GuiTickBox;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;

public class GuiBuyEstate extends GuiCreateEstate {

    private Estate estate;

    public GuiBuyEstate(Estate estate) {
        super(Sets.newTreeSet(Arrays.asList(PlayerPermission.values())), Sets.newTreeSet(Arrays.asList(EstateProperty.values())));
        this.estate = estate;
    }

    @Override
    public void initGui() {
        super.initGui();

        // override content variable to adjust for sending modify packet and not create packet
        content = new GuiCreateEstate.Content(mc, guiLeft, guiTop, xSize, ySize) {
            @Override
            public void elementClicked(int index, int mouseX, int mouseY, boolean doubleClick) {
                if (this.createBtn.mousePressed(mc, mouseX, mouseY)) {
                    mc.displayGuiScreen(new GuiPopup(estate));
                }
            }
        };


        // fill in values
        if (estate.getPurchasePrice() > 0)
            this.content.purchaseField.setText(String.valueOf(estate.getPurchasePrice()));
        if (estate.getRentPrice() > 0) this.content.rentField.setText(String.valueOf(estate.getRentPrice()));
        if (estate.getRentPeriod() > 0) this.content.periodField.setText(String.valueOf(estate.getRentPeriod()));
        if (!estate.getIntro().isEmpty()) this.content.introField.setText(estate.getIntro());
        if (!estate.getOutro().isEmpty()) this.content.outroField.setText(estate.getOutro());

        for (GuiTickBox guiTickBox : this.content.tickBoxList) {
            if (guiTickBox.key.startsWith("GLOBAL")) {
                guiTickBox.setChecked(estate.getGlobalPermissions().contains(PlayerPermission.valueOf(guiTickBox.key.split("\\.")[1])));
            } else if (guiTickBox.key.startsWith("RENTER")) {
                guiTickBox.setChecked(estate.getRenterPermissions().contains(PlayerPermission.valueOf(guiTickBox.key.split("\\.")[1])));
            } else if (guiTickBox.key.startsWith("PROPERTY")) {
                guiTickBox.setChecked(estate.getProperties().contains(EstateProperty.valueOf(guiTickBox.key.split("\\.")[1])));
            }
        }

        this.content.createBtn.displayString = "Next";
    }

    class GuiPopup extends GuiScreen {

        private Estate estate;
        private int guiLeft, guiTop, xSize = 80, ySize = 80;

        public GuiPopup(Estate estate) {
            this.estate = estate;
        }

        @Override
        public void drawScreen(int mouseX, int mouseY, float partialTicks) {
            drawDefaultBackground();
            GuiHelper.drawDefaultBackground(this.guiLeft, this.guiTop, this.xSize, this.ySize);
            GlStateManager.disableLighting();
            super.drawScreen(mouseX, mouseY, partialTicks);
        }

        @Override
        protected void actionPerformed(GuiButton button) throws IOException {
            super.actionPerformed(button);
            switch (button.id) {
                case 0:
                    Minelife.getNetwork().sendToServer(new PacketPurchaseEstate(estate.getUniqueID(), false));
                    break;
                case 1:
                    Minelife.getNetwork().sendToServer(new PacketPurchaseEstate(estate.getUniqueID(), true));
                    break;
                case 2:
                    Minecraft.getMinecraft().displayGuiScreen(new GuiBuyEstate(this.estate));
                    break;
            }
        }

        @Override
        public void initGui() {
            super.initGui();
            this.guiLeft = (this.width - this.xSize) / 2;
            this.guiTop = (this.height - this.ySize) / 2;
            buttonList.clear();
            buttonList.add(new GuiButton(0, (this.width - 40) / 2, this.guiTop + 5, 40, 20, "Buy"));
            buttonList.add(new GuiButton(1, (this.width - 40) / 2, buttonList.get(0).y + 25, 40, 20, "Rent"));
            buttonList.add(new GuiButton(2, (this.width - 40) / 2, buttonList.get(1).y + 25, 40, 20, "Cancel"));
            this.buttonList.get(0).enabled = this.estate.getPurchasePrice() > 0;
            this.buttonList.get(1).enabled = this.estate.getRentPrice() > 0 && this.estate.getRentPeriod() > 0;
        }
    }
}
