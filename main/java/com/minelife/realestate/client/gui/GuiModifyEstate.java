package com.minelife.realestate.client.gui;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.minelife.Minelife;
import com.minelife.realestate.Permission;
import com.minelife.realestate.EstateData;
import com.minelife.realestate.network.PacketUpdateEstate;
import com.minelife.util.NumberConversions;
import com.minelife.util.client.GuiScrollableContent;
import com.minelife.util.client.GuiTickBox;
import com.minelife.util.client.GuiUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.util.List;
import java.util.Map;

public class GuiModifyEstate extends GuiScreen {

    private int xPosition, yPosition, bgWidth = 200, bgHeight = 200;
    private EstateData estateData;
    private List<Permission> playerPermissions;
    private GuiContent content;

    public GuiModifyEstate(EstateData estateData, List<Permission> playerPermissions) {
        this.estateData = estateData;
        this.playerPermissions = playerPermissions;
    }

    @Override
    public void drawScreen(int x, int y, float f) {
        drawDefaultBackground();
        GuiUtil.drawDefaultBackground(xPosition, yPosition, bgWidth, bgHeight);
        content.draw(x, y, Mouse.getDWheel());
    }

    @Override
    protected void keyTyped(char keyChar, int keyCode) {
        super.keyTyped(keyChar, keyCode);
        content.keyTyped(keyChar, keyCode);
    }

    @Override
    public void initGui() {
        super.initGui();
        xPosition = (this.width - this.bgWidth) / 2;
        yPosition = (this.height - this.bgHeight) / 2;
        content = new GuiContent(mc, xPosition, yPosition, bgWidth, bgHeight);
    }

    @Override
    public void updateScreen() {
        content.update();
    }

    /**
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     */
    private class GuiContent extends GuiScrollableContent {

        private final int globalLabelY;
        private final int renterLabelY;
        private final int ownerLabelY;
        private final int allowedToChangeLabelY;
        private final int estateLabelY;
        private int totalHeight = 5;
        private GuiTextField purchaseField, rentField, periodField, introField, outroField;
        private Map<Permission, GuiTickBox> globalPerms, ownerPerms, renterPerms, allowedToChangePerms, estatePerms;
        private GuiButton updateBtn;

        public GuiContent(Minecraft mc, int xPosition, int yPosition, int width, int height) {
            super(mc, xPosition, yPosition, width, height);

            globalPerms = Maps.newHashMap();
            ownerPerms = Maps.newHashMap();
            renterPerms = Maps.newHashMap();
            allowedToChangePerms = Maps.newHashMap();
            estatePerms = Maps.newHashMap();

            purchaseField = new GuiTextField(fontRendererObj, (bgWidth - 100) / 2, totalHeight += 40, 100, 20);
            rentField = new GuiTextField(fontRendererObj, (bgWidth - 100) / 2, totalHeight += 60, 100, 20);
            periodField = new GuiTextField(fontRendererObj, (bgWidth - 100) / 2, totalHeight += 60, 100, 20);
            introField = new GuiTextField(fontRendererObj, (bgWidth - 100) / 2, totalHeight += 60, 100, 20);
            outroField = new GuiTextField(fontRendererObj, (bgWidth - 100) / 2, totalHeight += 60, 100, 20);

            purchaseField.setEnabled(playerPermissions.contains(Permission.MODIFY_PURCHASE_PRICE));
            rentField.setEnabled(playerPermissions.contains(Permission.MODIFY_RENT_PRICE));
            periodField.setEnabled(playerPermissions.contains(Permission.MODIFY_RENT_PERIOD));
            introField.setEnabled(playerPermissions.contains(Permission.MODIFY_INTRO));
            outroField.setEnabled(playerPermissions.contains(Permission.MODIFY_OUTRO));

            purchaseField.setText(estateData.getPurchasePrice() != -1 ? "" + estateData.getPurchasePrice() : "");
            rentField.setText(estateData.getRentPrice() != -1 ? "" + estateData.getRentPrice() : "");
            periodField.setText(estateData.getRentPeriod() != -1 ? "" + estateData.getRentPeriod() : "");
            introField.setText(estateData.getIntro() !=  null ? estateData.getIntro() : "");
            outroField.setText(estateData.getOutro() != null ? "" + estateData.getOutro() : "");

            totalHeight += 40;
            globalLabelY = totalHeight;
            for (Permission p : playerPermissions) {
                if (!p.isEstatePermission())
                    globalPerms.put(p, new GuiTickBox(mc, (bgWidth / 2) + (((bgWidth / 2) - GuiTickBox.WIDTH) / 2), totalHeight += 20, estateData.getGlobalPermissions().contains(p)));
            }
            totalHeight += 40;
            renterLabelY = totalHeight;
            for (Permission p : playerPermissions) {
                if (!p.isEstatePermission())
                    renterPerms.put(p, new GuiTickBox(mc, (bgWidth / 2) + (((bgWidth / 2) - GuiTickBox.WIDTH) / 2), totalHeight += 20, estateData.getRenterPermissions().contains(p)));
            }
            totalHeight += 40;
            ownerLabelY = totalHeight;
            for (Permission p : playerPermissions) {
                if (!p.isEstatePermission())
                    ownerPerms.put(p, new GuiTickBox(mc, (bgWidth / 2) + (((bgWidth / 2) - GuiTickBox.WIDTH) / 2), totalHeight += 20, estateData.getOwnerPermissions().contains(p)));
            }
            totalHeight += 60;
            allowedToChangeLabelY = totalHeight;
            for (Permission p : playerPermissions) {
                if (!p.isEstatePermission())
                    allowedToChangePerms.put(p, new GuiTickBox(mc, (bgWidth / 2) + (((bgWidth / 2) - GuiTickBox.WIDTH) / 2), totalHeight += 20, estateData.getGlobalPermissionsAllowedToChange().contains(p)));
            }
            totalHeight += 40;
            estateLabelY = totalHeight;
            for (Permission p : playerPermissions) {
                if (p.isEstatePermission())
                    estatePerms.put(p, new GuiTickBox(mc, (bgWidth / 2) + (((bgWidth / 2) - GuiTickBox.WIDTH) / 2), totalHeight += 20, estateData.getEstatePermissions().contains(p)));
            }

            totalHeight += 40;
            updateBtn = new GuiButton(0, (bgWidth - 50) / 2, totalHeight, 50, 20, "Update");
            totalHeight += 40;
        }

        @Override
        public int getObjectHeight(int index) {
            return totalHeight;
        }

        @Override
        public void drawObject(int index, int mouseX, int mouseY, boolean isHovering) {
            String bold = EnumChatFormatting.BOLD.toString();
            String underline = EnumChatFormatting.UNDERLINE.toString();
            fontRendererObj.drawString(bold + "Purchase Price", (bgWidth - fontRendererObj.getStringWidth(bold + "Purchase Price")) / 2, purchaseField.yPosition - 15, 0xFFFFFF);
            purchaseField.drawTextBox();
            fontRendererObj.drawString(bold + "Rent Price", (bgWidth - fontRendererObj.getStringWidth(bold + "Rent Price")) / 2, rentField.yPosition - 15, 0xFFFFFF);
            rentField.drawTextBox();
            fontRendererObj.drawString(bold + "Rent Period", (bgWidth - fontRendererObj.getStringWidth(bold + "Rent Period")) / 2, periodField.yPosition - 15, 0xFFFFFF);
            periodField.drawTextBox();
            fontRendererObj.drawString(bold + "Intro", (bgWidth - fontRendererObj.getStringWidth(bold + "Intro")) / 2, introField.yPosition - 15, 0xFFFFFF);
            introField.drawTextBox();
            fontRendererObj.drawString(bold + "Outro", (bgWidth - fontRendererObj.getStringWidth(bold + "Outro")) / 2, outroField.yPosition - 15, 0xFFFFFF);
            outroField.drawTextBox();

            fontRendererObj.drawString(bold + underline + "Global Permissions",
                    (bgWidth - fontRendererObj.getStringWidth(bold + underline + "Global Permissions")) / 2,
                    globalLabelY, 0xFFFFFF);

            globalPerms.forEach((p, tB) -> {
                fontRendererObj.drawString(bold + p.name(), 5 + (((bgWidth + 40) / 2) - fontRendererObj.getStringWidth(bold + p.name())) / 2, tB.yPosition + 5, 0xFFFFFF);
                tB.drawTickBox();
            });

            fontRendererObj.drawString(bold + underline + "Renter Permissions",
                    (bgWidth - fontRendererObj.getStringWidth(bold + underline + "Renter Permissions")) / 2,
                    renterLabelY, 0xFFFFFF);

            renterPerms.forEach((p, tB) -> {
                fontRendererObj.drawString(bold + p.name(), 5 + (((bgWidth + 40) / 2) - fontRendererObj.getStringWidth(bold + p.name())) / 2, tB.yPosition + 5, 0xFFFFFF);
                tB.drawTickBox();
            });

            fontRendererObj.drawString(bold + underline + "Owner Permissions",
                    (bgWidth - fontRendererObj.getStringWidth(bold + underline + "Owner Permissions")) / 2,
                    ownerLabelY, 0xFFFFFF);

            ownerPerms.forEach((p, tB) -> {
                fontRendererObj.drawString(bold + p.name(), 5 + (((bgWidth + 40) / 2) - fontRendererObj.getStringWidth(bold + p.name())) / 2, tB.yPosition + 5, 0xFFFFFF);
                tB.drawTickBox();
            });

            fontRendererObj.drawString(bold + underline + "Global Permissions",
                    (bgWidth - fontRendererObj.getStringWidth(bold + underline + "Global Permissions")) / 2,
                    allowedToChangeLabelY - 25, 0xFFFFFF);
            fontRendererObj.drawString(bold + underline + "Allowed to Change",
                    (bgWidth - fontRendererObj.getStringWidth(bold + underline + "Allowed to Change")) / 2,
                    allowedToChangeLabelY - 10, 0xFFFFFF);

            allowedToChangePerms.forEach((p, tB) -> {
                fontRendererObj.drawString(bold + p.name(), 5 + (((bgWidth + 40) / 2) - fontRendererObj.getStringWidth(bold + p.name())) / 2, tB.yPosition + 5, 0xFFFFFF);
                tB.drawTickBox();
            });

            fontRendererObj.drawString(bold + underline + "Estate Permissions",
                    (bgWidth - fontRendererObj.getStringWidth(bold + underline + "Estate Permissions")) / 2,
                    estateLabelY, 0xFFFFFF);

            if (estatePerms.isEmpty()) {
                fontRendererObj.drawString(bold + EnumChatFormatting.RED.toString() + "Must be OP to modify",
                        (bgWidth - fontRendererObj.getStringWidth(bold + underline + "Must be OP to modify")) / 2,
                        estateLabelY + 10, 0xFFFFFF);
            } else {
                estatePerms.forEach((p, tB) -> {
                    fontRendererObj.drawString(bold + p.name(), 5 + (((bgWidth + 40) / 2) - fontRendererObj.getStringWidth(bold + p.name())) / 2, tB.yPosition + 5, 0xFFFFFF);
                    tB.drawTickBox();
                });
            }

            updateBtn.drawButton(mc, mouseX, mouseY);
        }

        @Override
        public int getSize() {
            return 1;
        }

        @Override
        public void elementClicked(int index, int mouseX, int mouseY, boolean doubleClick) {
            purchaseField.mouseClicked(mouseX, mouseY, 0);
            rentField.mouseClicked(mouseX, mouseY, 0);
            periodField.mouseClicked(mouseX, mouseY, 0);
            introField.mouseClicked(mouseX, mouseY, 0);
            outroField.mouseClicked(mouseX, mouseY, 0);
            globalPerms.forEach((p, tB) -> tB.mouseClicked(mouseX, mouseY));
            renterPerms.forEach((p, tB) -> tB.mouseClicked(mouseX, mouseY));
            ownerPerms.forEach((p, tB) -> tB.mouseClicked(mouseX, mouseY));
            allowedToChangePerms.forEach((p, tB) -> tB.mouseClicked(mouseX, mouseY));
            estatePerms.forEach((p, tB) -> tB.mouseClicked(mouseX, mouseY));

            if (updateBtn.mousePressed(mc, mouseX, mouseY)) {
                List<Permission> globalPerms = Lists.newArrayList(), renterPerms = Lists.newArrayList(),
                        ownerPerms = Lists.newArrayList(), estatePerms = Lists.newArrayList(), globalAllowedToChangePerms = Lists.newArrayList();
                this.globalPerms.forEach((p, tB) -> {
                    if (tB.isChecked()) globalPerms.add(p);
                });
                this.renterPerms.forEach((p, tB) -> {
                    if (tB.isChecked()) renterPerms.add(p);
                });
                this.ownerPerms.forEach((p, tB) -> {
                    if (tB.isChecked()) ownerPerms.add(p);
                });
                this.estatePerms.forEach((p, tB) -> {
                    if (tB.isChecked()) estatePerms.add(p);
                });
                this.allowedToChangePerms.forEach((p, tB) -> {
                    if (tB.isChecked()) globalAllowedToChangePerms.add(p);
                });

                double purchasePrice = purchaseField.getText().isEmpty() ? -1.0D : Double.parseDouble(purchaseField.getText());
                double rentPrice = rentField.getText().isEmpty() ? -1.0D : Double.parseDouble(rentField.getText());
                int rentPeriod = periodField.getText().isEmpty() ? -1 : Integer.parseInt(periodField.getText());

                estateData.setPurchasePrice(purchasePrice);
                estateData.setRentPrice(rentPrice);
                estateData.setRentPeriod(rentPeriod);
                estateData.setIntro(introField.getText());
                estateData.setOutro(outroField.getText());
                estateData.setOwnerPermissions(ownerPerms);
                estateData.setRenterPermissions(renterPerms);
                estateData.setGlobalPermissions(globalPerms);
                estateData.setPermissionsAllowedToChange(globalAllowedToChangePerms);
                estateData.setEstatePermissions(estatePerms);

                // TODO: Update
                Minelife.NETWORK.sendToServer(new PacketUpdateEstate(estateData));
            }
        }

        @Override
        public void keyTyped(char keycode, int keynum) {
            super.keyTyped(keycode, keynum);
            if ((NumberConversions.isInt(String.valueOf(keycode)) && keynum != Keyboard.KEY_BACK) || keycode == '.' || keynum == Keyboard.KEY_BACK) {
                if (purchaseField.isFocused()) {
                    if (keycode == '.') {
                        if (!purchaseField.getText().contains("."))
                            purchaseField.textboxKeyTyped(keycode, keynum);
                    } else {
                        purchaseField.textboxKeyTyped(keycode, keynum);
                    }
                }
                if (rentField.isFocused()) {
                    if (keycode == '.') {
                        if (!rentField.getText().contains("."))
                            rentField.textboxKeyTyped(keycode, keynum);
                    } else {
                        rentField.textboxKeyTyped(keycode, keynum);
                    }
                }

                if (keycode != '.' || keynum == Keyboard.KEY_BACK)
                    periodField.textboxKeyTyped(keycode, keynum);
            }
            introField.textboxKeyTyped(keycode, keynum);
            outroField.textboxKeyTyped(keycode, keynum);
        }

        @Override
        public void drawSelectionBox(int index, int width, int height) {

        }

        @Override
        public void drawBackground() {

        }

        public void update() {
            purchaseField.updateCursorCounter();
            rentField.updateCursorCounter();
            periodField.updateCursorCounter();
            introField.updateCursorCounter();
            outroField.updateCursorCounter();
        }
    }


}