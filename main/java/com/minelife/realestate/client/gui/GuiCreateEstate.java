package com.minelife.realestate.client.gui;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.minelife.Minelife;
import com.minelife.realestate.Estate;
import com.minelife.realestate.EstateProperty;
import com.minelife.realestate.PlayerPermission;
import com.minelife.realestate.network.PacketCreateEstate;
import com.minelife.util.NumberConversions;
import com.minelife.util.Vec2d;
import com.minelife.util.client.GuiHelper;
import com.minelife.util.client.GuiScrollableContent;
import com.minelife.util.client.GuiTickBox;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.nbt.NBTTagCompound;
import org.apache.commons.lang3.text.WordUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class GuiCreateEstate extends GuiScreen {

    protected int guiLeft, guiTop, xSize = 176, ySize = 186;
    protected Content content;
    protected Set<PlayerPermission> allowedPermissions;
    protected Set<EstateProperty> allowedProperties;

    public GuiCreateEstate(Set<PlayerPermission> allowedPermissions, Set<EstateProperty> allowedProperties) {
        this.allowedPermissions = allowedPermissions;
        this.allowedProperties = allowedProperties;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        GuiHelper.drawDefaultBackground(guiLeft - 2, guiTop - 2, xSize + 4, ySize + 4);
        GlStateManager.disableLighting();
        content.draw(mouseX, mouseY, Mouse.getDWheel());
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        content.keyTyped(typedChar, keyCode);
    }

    @Override
    public void initGui() {
        super.initGui();
        this.guiLeft = (this.width - this.xSize) / 2;
        this.guiTop = (this.height - this.ySize) / 2;
        content = new Content(mc, guiLeft, guiTop, xSize, ySize);
    }

    class Content extends GuiScrollableContent {

        private int totalHeight = 0;
        protected List<GuiTickBox> tickBoxList;
        protected Map<Vec2d, String> labelMap;
        protected GuiTextField purchaseField, rentField, periodField, introField, outroField;
        protected GuiButton createBtn;

        public Content(Minecraft mc, int x, int y, int width, int height) {
            super(mc, x, y, width, height);
            this.tickBoxList = Lists.newArrayList();
            this.labelMap = Maps.newHashMap();
            totalHeight += 5;
            labelMap.put(new Vec2d((width - fontRenderer.getStringWidth("Purchase Price")) / 2, totalHeight), "Purchase Price");
            totalHeight += 10;
            purchaseField = new GuiTextField(0, fontRenderer, (width - 70) / 2, totalHeight, 70, 15);
            totalHeight += 20;
            labelMap.put(new Vec2d((width - fontRenderer.getStringWidth("Rent Price")) / 2, totalHeight), "Rent Price");
            totalHeight += 10;
            rentField = new GuiTextField(0, fontRenderer, (width - 70) / 2, totalHeight, 70, 15);
            totalHeight += 20;
            labelMap.put(new Vec2d((width - fontRenderer.getStringWidth("Rent Period (1 = 20 Minutes)")) / 2, totalHeight), "Rent Period (1 = 20 Minutes)");
            totalHeight += 10;
            periodField = new GuiTextField(0, fontRenderer, (width - 70) / 2, totalHeight, 70, 15);
            totalHeight += 20;
            labelMap.put(new Vec2d((width - fontRenderer.getStringWidth("Intro")) / 2, totalHeight), "Intro");
            totalHeight += 10;
            introField = new GuiTextField(0, fontRenderer, (width - 140) / 2, totalHeight, 140, 15);
            totalHeight += 20;
            labelMap.put(new Vec2d((width - fontRenderer.getStringWidth("Outro")) / 2, totalHeight), "Outro");
            totalHeight += 10;
            outroField = new GuiTextField(0, fontRenderer, (width - 140) / 2, totalHeight, 140, 15);
            totalHeight += 40;

            // global
            labelMap.put(new Vec2d((width - fontRenderer.getStringWidth("Global Permissions")) / 2, totalHeight), "Global Permissions");
            totalHeight += 20;
            for (PlayerPermission permission : allowedPermissions) {
                labelMap.put(new Vec2d(10, totalHeight + 5), WordUtils.capitalizeFully(permission.name().replace("_", " ")));
                tickBoxList.add(new GuiTickBox(mc, width - 50, totalHeight, false, "GLOBAL." + permission.name()));
                totalHeight += 20;
            }
            totalHeight += 20;

            // renter
            labelMap.put(new Vec2d((width - fontRenderer.getStringWidth("Renter Permissions")) / 2, totalHeight), "Renter Permissions");
            totalHeight += 20;
            for (PlayerPermission permission : allowedPermissions) {
                labelMap.put(new Vec2d(10, totalHeight + 5), WordUtils.capitalizeFully(permission.name().replace("_", " ")));
                tickBoxList.add(new GuiTickBox(mc, width - 50, totalHeight, false, "RENTER." + permission.name()));
                totalHeight += 20;
            }

            totalHeight += 20;
            // estate
            labelMap.put(new Vec2d((width - fontRenderer.getStringWidth("Estate Properties")) / 2, totalHeight), "Estate Properties");
            totalHeight += 20;
            for (EstateProperty property : allowedProperties) {
                labelMap.put(new Vec2d(10, totalHeight + 5), WordUtils.capitalizeFully(property.name().replace("_", " ")));
                tickBoxList.add(new GuiTickBox(mc, width - 50, totalHeight, false, "PROPERTY." + property.name()));
                totalHeight += 20;
            }

            totalHeight += 20;
            createBtn = new GuiButton(0, (width - 50) / 2, totalHeight, 50, 20, "Create");
            totalHeight += 30;
        }

        @Override
        public int getObjectHeight(int index) {
            return this.totalHeight;
        }

        @Override
        public void drawObject(int index, int mouseX, int mouseY, boolean isHovering) {
            this.labelMap.forEach((vec, label) -> fontRenderer.drawString(label, (int) vec.x, (int) vec.y, 4210752));
            this.tickBoxList.forEach(GuiTickBox::drawTickBox);
            this.purchaseField.drawTextBox();
            this.rentField.drawTextBox();
            this.periodField.drawTextBox();
            this.introField.drawTextBox();
            this.outroField.drawTextBox();
            this.createBtn.drawButton(mc, mouseX, mouseY, 0);
        }

        @Override
        public void keyTyped(char keycode, int keynum) {
            super.keyTyped(keycode, keynum);

            if (keynum == Keyboard.KEY_BACK) {
                introField.textboxKeyTyped(keycode, keynum);
                outroField.textboxKeyTyped(keycode, keynum);
                this.purchaseField.textboxKeyTyped(keycode, keynum);
                this.rentField.textboxKeyTyped(keycode, keynum);
                this.periodField.textboxKeyTyped(keycode, keynum);
                return;
            }

            if (introField.isFocused() || outroField.isFocused()) {
                introField.textboxKeyTyped(keycode, keynum);
                outroField.textboxKeyTyped(keycode, keynum);
            } else {
                if (this.purchaseField.isFocused() && NumberConversions.isInt(purchaseField.getText() + keycode)
                        && NumberConversions.toInt(purchaseField.getText() + keycode) * 20 > 0)
                    this.purchaseField.textboxKeyTyped(keycode, keynum);
                if (this.rentField.isFocused() && NumberConversions.isInt(rentField.getText() + keycode)
                        && NumberConversions.toInt(rentField.getText() + keycode) * 20 > 0)
                    this.rentField.textboxKeyTyped(keycode, keynum);
                if (this.periodField.isFocused() && NumberConversions.isInt(periodField.getText() + keycode)
                        && NumberConversions.toInt(periodField.getText() + keycode) * 20 > 0)
                    this.periodField.textboxKeyTyped(keycode, keynum);
            }
        }

        @Override
        public int getSize() {
            return 1;
        }

        @Override
        public void elementClicked(int index, int mouseX, int mouseY, boolean doubleClick) {
            this.tickBoxList.forEach(tickBox -> tickBox.mouseClicked(mouseX, mouseY));
            this.purchaseField.mouseClicked(mouseX, mouseY, 0);
            this.rentField.mouseClicked(mouseX, mouseY, 0);
            this.periodField.mouseClicked(mouseX, mouseY, 0);
            this.introField.mouseClicked(mouseX, mouseY, 0);
            this.outroField.mouseClicked(mouseX, mouseY, 0);

            if (this.createBtn.mousePressed(mc, mouseX, mouseY)) {
                int purchasePrice = 0, rentPrice = 0, rentPeriod = 0;
                if (NumberConversions.isInt(this.purchaseField.getText()))
                    purchasePrice = NumberConversions.toInt(this.purchaseField.getText());
                if (NumberConversions.isInt(this.rentField.getText()))
                    rentPrice = NumberConversions.toInt(this.rentField.getText());
                if (NumberConversions.isInt(this.periodField.getText()))
                    rentPeriod = NumberConversions.toInt((this.periodField.getText()));

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

                Estate estate = new Estate(UUID.randomUUID(), new NBTTagCompound());
                estate.setPurchasePrice(purchasePrice);
                estate.setRentPrice(rentPrice);
                estate.setRentPeriod(rentPeriod);
                estate.setIntro(this.introField.getText());
                estate.setOutro(this.outroField.getText());
                estate.setGlobalPermissions(globalPermissions);
                estate.setRenterPermissions(renterPermissions);
                estate.setProperties(estateProperties);

                Minelife.getNetwork().sendToServer(new PacketCreateEstate(estate));
            }
        }

        @Override
        public void drawSelectionBox(int index, int width, int height) {

        }

        @Override
        public void drawBackground() {

        }
    }

}
