package com.minelife.realestate.client.gui;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.minelife.Minelife;
import com.minelife.realestate.Permission;
import com.minelife.realestate.network.PacketCreateEstate;
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

// TODO: Check if they can create an estate with the current seleciton before opening this GUI
public class GuiCreateEstate extends GuiScreen {

    private GuiContent content;
    private List<Permission> playerPermissions;
    private int bgWidth = 200, bgHeight = 200;

    public GuiCreateEstate(List<Permission> playerPermissions) {
        this.playerPermissions = playerPermissions;
    }

    @Override
    public void drawScreen(int x, int y, float f) {
        drawDefaultBackground();
        GuiUtil.drawDefaultBackground((this.width - bgWidth) / 2, (this.height - bgHeight) / 2, bgWidth, bgHeight);
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
        content = new GuiContent(mc, (this.width - bgWidth) / 2, 5 + (this.height - bgHeight) / 2, bgWidth, bgHeight - 10);
    }

    @Override
    public void updateScreen() {
        content.update();
    }

    private class GuiContent extends GuiScrollableContent {
        private GuiTextField purchaseField, rentField, rentPeriodField, introField, outroField;
        private Map<Permission, GuiTickBox> globalPermissions, renterPermissions, ownerPermissions, allowedToChangePermissions, estatePermissions;
        private GuiButton createBtn;
        private int totalHeight = 0;

        private int globalLabelY, renterLabelY, ownerLabelY, allowedToChangeLabelY, estateLabelY;

        public GuiContent(Minecraft mc, int xPosition, int yPosition, int width, int height) {
            super(mc, xPosition, yPosition, width, height);

            globalPermissions = Maps.newHashMap();
            renterPermissions = Maps.newHashMap();
            ownerPermissions = Maps.newHashMap();
            allowedToChangePermissions = Maps.newHashMap();
            estatePermissions = Maps.newHashMap();

            purchaseField = new GuiTextField(fontRendererObj, (bgWidth - 100) / 2, totalHeight += 40, 100, 20);
            rentField = new GuiTextField(fontRendererObj, (bgWidth - 100) / 2, totalHeight += 60, 100, 20);
            rentPeriodField = new GuiTextField(fontRendererObj, (bgWidth - 100) / 2, totalHeight += 60, 100, 20);
            introField = new GuiTextField(fontRendererObj, (bgWidth - 100) / 2, totalHeight += 60, 100, 20);
            outroField = new GuiTextField(fontRendererObj, (bgWidth - 100) / 2, totalHeight += 60, 100, 20);
            totalHeight += 40;
            globalLabelY = totalHeight;
            for (Permission p : playerPermissions) {
                if (!p.isEstatePermission())
                    globalPermissions.put(p, new GuiTickBox(mc, (bgWidth / 2) + (((bgWidth / 2) - GuiTickBox.WIDTH) / 2), totalHeight += 20, false));
            }
            totalHeight += 40;
            renterLabelY = totalHeight;
            for (Permission p : playerPermissions) {
                if (!p.isEstatePermission())
                    renterPermissions.put(p, new GuiTickBox(mc, (bgWidth / 2) + (((bgWidth / 2) - GuiTickBox.WIDTH) / 2), totalHeight += 20, false));
            }
            totalHeight += 40;
            ownerLabelY = totalHeight;
            for (Permission p : playerPermissions) {
                if (!p.isEstatePermission())
                    ownerPermissions.put(p, new GuiTickBox(mc, (bgWidth / 2) + (((bgWidth / 2) - GuiTickBox.WIDTH) / 2), totalHeight += 20, false));
            }
            totalHeight += 60;
            allowedToChangeLabelY = totalHeight;
            for (Permission p : playerPermissions) {
                if (!p.isEstatePermission())
                    allowedToChangePermissions.put(p, new GuiTickBox(mc, (bgWidth / 2) + (((bgWidth / 2) - GuiTickBox.WIDTH) / 2), totalHeight += 20, false));
            }
            totalHeight += 40;
            estateLabelY = totalHeight;
            for (Permission p : playerPermissions) {
                if (p.isEstatePermission())
                    estatePermissions.put(p, new GuiTickBox(mc, (bgWidth / 2) + (((bgWidth / 2) - GuiTickBox.WIDTH) / 2), totalHeight += 20, true));
            }

            totalHeight += 40;
            createBtn = new GuiButton(0, (bgWidth - 50) / 2, totalHeight, 50, 20, "Create");
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
            fontRendererObj.drawString(bold + "Rent Period", (bgWidth - fontRendererObj.getStringWidth(bold + "Rent Period")) / 2, rentPeriodField.yPosition - 15, 0xFFFFFF);
            rentPeriodField.drawTextBox();
            fontRendererObj.drawString(bold + "Intro", (bgWidth - fontRendererObj.getStringWidth(bold + "Intro")) / 2, introField.yPosition - 15, 0xFFFFFF);
            introField.drawTextBox();
            fontRendererObj.drawString(bold + "Outro", (bgWidth - fontRendererObj.getStringWidth(bold + "Outro")) / 2, outroField.yPosition - 15, 0xFFFFFF);
            outroField.drawTextBox();

            fontRendererObj.drawString(bold + underline + "Global Permissions",
                    (bgWidth - fontRendererObj.getStringWidth(bold + underline + "Global Permissions")) / 2,
                    globalLabelY, 0xFFFFFF);

            globalPermissions.forEach((p, tB) -> {
                fontRendererObj.drawString(bold + p.name(), 5 + (((bgWidth + 40) / 2) - fontRendererObj.getStringWidth(bold + p.name())) / 2, tB.yPosition + 5, 0xFFFFFF);
                tB.drawTickBox();
            });

            fontRendererObj.drawString(bold + underline + "Renter Permissions",
                    (bgWidth - fontRendererObj.getStringWidth(bold + underline + "Renter Permissions")) / 2,
                    renterLabelY, 0xFFFFFF);

            renterPermissions.forEach((p, tB) -> {
                fontRendererObj.drawString(bold + p.name(), 5 + (((bgWidth + 40) / 2) - fontRendererObj.getStringWidth(bold + p.name())) / 2, tB.yPosition + 5, 0xFFFFFF);
                tB.drawTickBox();
            });

            fontRendererObj.drawString(bold + underline + "Owner Permissions",
                    (bgWidth - fontRendererObj.getStringWidth(bold + underline + "Owner Permissions")) / 2,
                    ownerLabelY, 0xFFFFFF);

            ownerPermissions.forEach((p, tB) -> {
                fontRendererObj.drawString(bold + p.name(), 5 + (((bgWidth + 40) / 2) - fontRendererObj.getStringWidth(bold + p.name())) / 2, tB.yPosition + 5, 0xFFFFFF);
                tB.drawTickBox();
            });

            fontRendererObj.drawString(bold + underline + "Global Permissions",
                    (bgWidth - fontRendererObj.getStringWidth(bold + underline + "Global Permissions")) / 2,
                    allowedToChangeLabelY - 25, 0xFFFFFF);
            fontRendererObj.drawString(bold + underline + "Allowed to Change",
                    (bgWidth - fontRendererObj.getStringWidth(bold + underline + "Allowed to Change")) / 2,
                    allowedToChangeLabelY - 10, 0xFFFFFF);

            allowedToChangePermissions.forEach((p, tB) -> {
                fontRendererObj.drawString(bold + p.name(), 5 + (((bgWidth + 40) / 2) - fontRendererObj.getStringWidth(bold + p.name())) / 2, tB.yPosition + 5, 0xFFFFFF);
                tB.drawTickBox();
            });

            fontRendererObj.drawString(bold + underline + "Estate Permissions",
                    (bgWidth - fontRendererObj.getStringWidth(bold + underline + "Estate Permissions")) / 2,
                    estateLabelY, 0xFFFFFF);

            if (estatePermissions.isEmpty()) {
                fontRendererObj.drawString(bold + EnumChatFormatting.RED.toString() + "Must be OP to modify",
                        (bgWidth - fontRendererObj.getStringWidth(bold + underline + "Must be OP to modify")) / 2,
                        estateLabelY + 10, 0xFFFFFF);
            } else {
                estatePermissions.forEach((p, tB) -> {
                    fontRendererObj.drawString(bold + p.name(), 5 + (((bgWidth + 40) / 2) - fontRendererObj.getStringWidth(bold + p.name())) / 2, tB.yPosition + 5, 0xFFFFFF);
                    tB.drawTickBox();
                });
            }

            createBtn.drawButton(mc, mouseX, mouseY);
        }

        @Override
        public int getSize() {
            return 1;
        }

        @Override
        public void elementClicked(int index, int mouseX, int mouseY, boolean doubleClick) {
            purchaseField.mouseClicked(mouseX, mouseY, 0);
            rentField.mouseClicked(mouseX, mouseY, 0);
            rentPeriodField.mouseClicked(mouseX, mouseY, 0);
            introField.mouseClicked(mouseX, mouseY, 0);
            outroField.mouseClicked(mouseX, mouseY, 0);
            globalPermissions.forEach((p, tB) -> tB.mouseClicked(mouseX, mouseY));
            renterPermissions.forEach((p, tB) -> tB.mouseClicked(mouseX, mouseY));
            ownerPermissions.forEach((p, tB) -> tB.mouseClicked(mouseX, mouseY));
            allowedToChangePermissions.forEach((p, tB) -> tB.mouseClicked(mouseX, mouseY));
            estatePermissions.forEach((p, tB) -> tB.mouseClicked(mouseX, mouseY));

            if (createBtn.mousePressed(mc, mouseX, mouseY)) {
                List<Permission> globalPerms = Lists.newArrayList(), renterPerms = Lists.newArrayList(),
                        ownerPerms = Lists.newArrayList(), estatePerms = Lists.newArrayList(), allowedToChangePerms = Lists.newArrayList();
                globalPermissions.forEach((p, tB) -> {
                    if (tB.isChecked()) globalPerms.add(p);
                });
                renterPermissions.forEach((p, tB) -> {
                    if (tB.isChecked()) renterPerms.add(p);
                });
                ownerPermissions.forEach((p, tB) -> {
                    if (tB.isChecked()) ownerPerms.add(p);
                });
                estatePermissions.forEach((p, tB) -> {
                    if (tB.isChecked()) estatePerms.add(p);
                });
                allowedToChangePermissions.forEach((p, tB) -> {
                    if (tB.isChecked()) allowedToChangePerms.add(p);
                });

                double purchasePrice = purchaseField.getText().isEmpty() ? -1.0D : Double.parseDouble(purchaseField.getText());
                double rentPrice = rentField.getText().isEmpty() ? -1.0D : Double.parseDouble(rentField.getText());
                int rentPeriod = rentPeriodField.getText().isEmpty() ? -1 : Integer.parseInt(rentPeriodField.getText());

                Minelife.NETWORK.sendToServer(new PacketCreateEstate(globalPerms, ownerPerms, renterPerms, estatePerms, allowedToChangePerms, purchasePrice,
                        rentPrice, rentPeriod, introField.getText(), outroField.getText()));
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
                    rentPeriodField.textboxKeyTyped(keycode, keynum);
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
            rentPeriodField.updateCursorCounter();
            introField.updateCursorCounter();
            outroField.updateCursorCounter();
        }
    }

}