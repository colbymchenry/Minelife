package com.minelife.realestate.client.gui;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.minelife.Minelife;
import com.minelife.realestate.Estate;
import com.minelife.realestate.EstateProperty;
import com.minelife.realestate.PlayerPermission;
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
import java.util.UUID;

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
                    int purchasePrice = 0, rentPrice = 0, rentPeriod = 0;
                    if (NumberConversions.isInt(this.purchaseField.getText()))
                        purchasePrice = NumberConversions.toInt(this.purchaseField.getText());
                    if (NumberConversions.isInt(this.rentField.getText()))
                        rentPrice = NumberConversions.toInt(this.rentField.getText());
                    if (NumberConversions.isInt(this.periodField.getText()))
                        rentPeriod = NumberConversions.toByte((this.periodField.getText()));

                    Set<PlayerPermission> globalPermissions = Sets.newTreeSet(), renterPermissions = Sets.newTreeSet();
                    Set<EstateProperty> estateProperties = Sets.newTreeSet();

                    this.tickBoxList.forEach(guiTickBox -> {
                        String[] data = guiTickBox.key.split("\\.");
                        String key = data[0], value = data[1];
                        switch (key) {
                            case "GLOBAL":
                                if (guiTickBox.isChecked())
                                    globalPermissions.add(PlayerPermission.valueOf(value));
                                break;
                            case "RENTER":
                                if (guiTickBox.isChecked())
                                    renterPermissions.add(PlayerPermission.valueOf(value));
                                break;
                            case "PROPERTY":
                                if (guiTickBox.isChecked())
                                    estateProperties.add(EstateProperty.valueOf(value));
                                break;
                        }
                    });

                    estate.setPurchasePrice(purchasePrice);
                    estate.setRentPrice(rentPrice);
                    estate.setRentPeriod(rentPeriod);
                    estate.setIntro(this.introField.getText());
                    estate.setOutro(this.outroField.getText());
                    estate.setGlobalPermissions(globalPermissions);
                    estate.setRenterPermissions(renterPermissions);
                    estate.setProperties(estateProperties);

                    Minelife.getNetwork().sendToServer(new PacketUpdateEstate(estate));
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

        this.content.createBtn.displayString = "Purchase";
    }

    class GuiPopup extends GuiScreen {

        private Estate estate;
        private int guiLeft, guiTop, xSize = 80, ySize = 100;

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

                    break;
                case 1:
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
        }
    }
}
