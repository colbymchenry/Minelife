package com.minelife.realestate.client.gui;

import com.google.common.collect.Sets;
import com.minelife.Minelife;
import com.minelife.realestate.Estate;
import com.minelife.realestate.EstateProperty;
import com.minelife.realestate.PlayerPermission;
import com.minelife.realestate.network.PacketCreateEstate;
import com.minelife.realestate.network.PacketUpdateEstate;
import com.minelife.util.NumberConversions;
import com.minelife.util.client.GuiTickBox;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.nbt.NBTTagCompound;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

public class GuiModifyEstate extends GuiCreateEstate {

    private Estate estate;

    public GuiModifyEstate(Estate estate, Set<PlayerPermission> allowedPermissions, Set<EstateProperty> allowedProperties) {
        super(allowedPermissions, allowedProperties);
        this.estate = estate;
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);
        if (button.id == 0)
            mc.displayGuiScreen(new GuiMembers(estate, allowedPermissions, allowedProperties));
        else {
            content.elementClicked(1, content.createBtn.x + 2, content.createBtn.y + 2, true);
        }
    }

    @Override
    public void initGui() {
        super.initGui();
        buttonList.clear();
        buttonList.add(new GuiButton(0, guiLeft - 55, guiTop, 50, 20, "Members"));
        buttonList.get(0).enabled = allowedPermissions.contains(PlayerPermission.MODIFY_MEMBERS);
        buttonList.add(new GuiButton(1, guiLeft - 55, guiTop + 22, 50, 20, "Update"));

        // override content variable to adjust for sending modify packet and not create packet
        content = new Content(mc, guiLeft, guiTop, xSize, ySize) {
            @Override
            public void elementClicked(int index, int mouseX, int mouseY, boolean doubleClick) {
                this.tickBoxList.forEach(tickBox -> tickBox.mouseClicked(mouseX, mouseY));
                this.purchaseField.mouseClicked(mouseX, mouseY, 0);
                this.rentField.mouseClicked(mouseX, mouseY, 0);
                this.periodField.mouseClicked(mouseX, mouseY, 0);
                this.introField.mouseClicked(mouseX, mouseY, 0);
                this.outroField.mouseClicked(mouseX, mouseY, 0);

                if (this.createBtn.x < mouseX && this.createBtn.x + this.createBtn.width > mouseX
                        && this.createBtn.y < mouseY && this.createBtn.y + this.createBtn.height > mouseY) {
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

            @Override
            public int getObjectHeight(int index) {
                return super.getObjectHeight(index) - 40;
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

        this.content.createBtn.displayString = "Update";
        this.content.createBtn.visible = false;
    }
}
