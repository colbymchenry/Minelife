package com.minelife.realestate.client.gui;

import com.google.common.collect.Maps;
import com.minelife.Minelife;
import com.minelife.police.network.PacketCreateTicket;
import com.minelife.realestate.EnumPermission;
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

import java.awt.Color;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class GuiEstateCreationForm extends GuiScreen {

    private GuiContent content;
    private Set<EnumPermission> permissionsAllowedToChange;
    private GuiTextField fieldRentPrice, fieldPurchasePrice, fieldRentPeriodInDays;
    private GuiTickBox forRent;
    private Map<EnumPermission, GuiTickBox> permissionTickBoxSet, allowedToChangeTickBoxSet;
    private GuiButton btnCreate;
    private Color bgColor = new Color(108, 108, 108, 255);

    private static int width = 200, height = 200;
    private int xPosition, yPosition;

    public GuiEstateCreationForm(Set<EnumPermission> permissionsAllowedToChange)
    {
        this.permissionsAllowedToChange = permissionsAllowedToChange;
    }

    @Override
    public void drawScreen(int x, int y, float f)
    {
        drawDefaultBackground();
        GuiUtil.drawDefaultBackground(xPosition, yPosition, width, height, bgColor);
        content.draw(x, y, Mouse.getDWheel());
    }

    @Override
    protected void keyTyped(char keyChar, int keyCode)
    {
        super.keyTyped(keyChar, keyCode);
        content.keyTyped(keyChar, keyCode);
    }

    @Override
    public void initGui()
    {
        super.initGui();
        xPosition = (super.width - width) / 2;
        yPosition = (super.height - height) / 2;
        content = new GuiContent(mc, xPosition, yPosition + 3, width, height - 6);
    }

    @Override
    public void updateScreen()
    {
        super.updateScreen();
        fieldRentPrice.updateCursorCounter();
        fieldPurchasePrice.updateCursorCounter();
        fieldRentPeriodInDays.updateCursorCounter();

        fieldRentPrice.setEnabled(forRent.isChecked());
        fieldRentPeriodInDays.setEnabled(forRent.isChecked());
    }

    /**
     * All drawing is done here since the gui will have to scroll due to so many options
     */
    private class GuiContent extends GuiScrollableContent {

        private int widthOffset = 30;
        private int columnWidth = (width - widthOffset) / 3;

        public GuiContent(Minecraft mc, int xPosition, int yPosition, int width, int height)
        {
            super(mc, xPosition, yPosition, width, height);
            fieldPurchasePrice = new GuiTextField(fontRendererObj, 45, 20, width - 90, 20);
            fieldRentPrice = new GuiTextField(fontRendererObj, 45, 60, width - 90, 20);
            fieldRentPeriodInDays = new GuiTextField(fontRendererObj, 45, 100, width - 90, 20);
            fieldRentPrice.setEnabled(false);
            fieldRentPeriodInDays.setEnabled(false);
            forRent = new GuiTickBox(mc, (width / 2) + 10, 140, false);
            permissionTickBoxSet = Maps.newHashMap();
            allowedToChangeTickBoxSet = Maps.newHashMap();
            btnCreate = new GuiButton(0, (width - 50) / 2, getObjectHeight(0) - 40, 50, 20, "Create");

            /*
            * Only allow the player to modify the permissions that they are allowed to modify themselves,
            * including allowing modification to permission by new renter or buyer. So essentially, a person
            * cannot create a region that allows any permissions that their parent region does not have. And they
            * can only modify permissions that the parent region either is allowed to modify themselves or that the
            * parent region owner allows them to.
            */
            int y = 200;
            for (EnumPermission p : EnumPermission.values()) {
                permissionTickBoxSet.put(p, new GuiTickBox(mc, (columnWidth * 1) + ((columnWidth - GuiTickBox.WIDTH) / 2) + (widthOffset / 2), y, false));
                GuiTickBox tickBox = new GuiTickBox(mc, (columnWidth * 2) + ((columnWidth - GuiTickBox.WIDTH) / 2) + (widthOffset / 2), y, false);
                tickBox.enabled = permissionsAllowedToChange.contains(p);
                allowedToChangeTickBoxSet.put(p, tickBox);
                y += 30;
            }
        }

        @Override
        public int getObjectHeight(int index)
        {
            return 400;
        }

        @Override
        public void drawObject(int index, int mouseX, int mouseY, boolean isHovering)
        {
            fontRendererObj.drawString("Purchase Price", (width - fontRendererObj.getStringWidth("Purchase Price")) / 2, fieldPurchasePrice.yPosition - 10, 0xFFFFFF);
            fieldPurchasePrice.drawTextBox();
            fontRendererObj.drawString("Rent Price", (width - fontRendererObj.getStringWidth("Rent Price")) / 2, fieldRentPrice.yPosition - 10, forRent.isChecked() ? 0xFFFFFF : 0xA6A6A6);
            fieldRentPrice.drawTextBox();
            fontRendererObj.drawString("Rent Period in Days", (width - fontRendererObj.getStringWidth("Rent Period in Days")) / 2, fieldRentPeriodInDays.yPosition - 10, forRent.isChecked() ? 0xFFFFFF : 0xA6A6A6);
            fieldRentPeriodInDays.drawTextBox();
            fontRendererObj.drawString("For Rent:", (width / 2) - 40, forRent.yPosition + 5, 0xFFFFFF);
            forRent.drawTickBox();

            String permColumn = EnumChatFormatting.BOLD.toString() + EnumChatFormatting.UNDERLINE.toString() + "Permission";
            fontRendererObj.drawString(permColumn, (columnWidth * 0) + ((columnWidth - fontRendererObj.getStringWidth(permColumn)) / 2) + (widthOffset / 2), 180, 0xFFFFFF);
            String allowColumn = EnumChatFormatting.BOLD.toString() + EnumChatFormatting.UNDERLINE.toString() + "Allow";
            fontRendererObj.drawString(allowColumn, (columnWidth * 1) + ((columnWidth - fontRendererObj.getStringWidth(allowColumn)) / 2) + (widthOffset / 2), 180, 0xFFFFFF);
            String modifyColumn = EnumChatFormatting.BOLD.toString() + EnumChatFormatting.UNDERLINE.toString() + "Can Modify";
            fontRendererObj.drawString(modifyColumn, (columnWidth * 2) + ((columnWidth - fontRendererObj.getStringWidth(modifyColumn)) / 2) + (widthOffset / 2), 180, 0xFFFFFF);

            permissionTickBoxSet.forEach((p, t) -> {
                fontRendererObj.drawString(p.name(), (columnWidth * 0) + ((columnWidth - fontRendererObj.getStringWidth(p.name())) / 2) + (widthOffset / 2), t.yPosition + 5, 0xFFFFFF);
                t.drawTickBox();
            });

            allowedToChangeTickBoxSet.forEach((p, t) -> {
                t.drawTickBox();
            });

            btnCreate.drawButton(mc, mouseX, mouseY);
        }

        @Override
        public int getSize()
        {
            return 1;
        }

        @Override
        public void elementClicked(int index, int mouseX, int mouseY, boolean doubleClick)
        {
            fieldRentPrice.mouseClicked(mouseX, mouseY, 0);
            fieldPurchasePrice.mouseClicked(mouseX, mouseY, 0);
            fieldRentPeriodInDays.mouseClicked(mouseX, mouseY, 0);
            forRent.mouseClicked(mouseX, mouseY);

            permissionTickBoxSet.forEach((p, t) -> t.mouseClicked(mouseX, mouseY));
            allowedToChangeTickBoxSet.forEach((p, t) -> t.mouseClicked(mouseX, mouseY));

            if (btnCreate.mousePressed(mc, mouseX, mouseY)) {
                double purchasePrice = fieldPurchasePrice.getText().isEmpty() ? 0.0D : Double.parseDouble(fieldPurchasePrice.getText());
                double rentPrice = fieldRentPrice.getText().isEmpty() ? 0.0D : Double.parseDouble(fieldRentPrice.getText());
                int rentPeriodInDays = fieldRentPeriodInDays.getText().isEmpty() ? 0 : Integer.parseInt(fieldRentPeriodInDays.getText());

                Set<EnumPermission> permissions = new TreeSet<>();
                permissionTickBoxSet.forEach((p, t) -> {
                    if (t.isChecked()) permissions.add(p);
                });

                Set<EnumPermission> permsAllowedToChange = new TreeSet<>();
                allowedToChangeTickBoxSet.forEach((p, t) -> {
                    if (t.isChecked()) permsAllowedToChange.add(p);
                });

                Minelife.NETWORK.sendToServer(new PacketCreateEstate(permissions, permsAllowedToChange, purchasePrice, rentPrice, rentPeriodInDays, forRent.isChecked()));
            }
        }

        @Override
        public void keyTyped(char keycode, int keynum)
        {
            super.keyTyped(keycode, keynum);

            if(!NumberConversions.isInt("" + keycode) && keynum != Keyboard.KEY_BACK && keynum != Keyboard.KEY_PERIOD) return;

            if(fieldRentPrice.isFocused() && keynum == Keyboard.KEY_PERIOD && (fieldRentPrice.getText().isEmpty() || fieldRentPrice.getText().contains("."))) return;
            if(fieldPurchasePrice.isFocused() && keynum == Keyboard.KEY_PERIOD && (fieldPurchasePrice.getText().isEmpty() || fieldPurchasePrice.getText().contains("."))) return;
            if(fieldRentPeriodInDays.isFocused() && keynum == Keyboard.KEY_PERIOD && (fieldRentPeriodInDays.getText().isEmpty() || fieldRentPeriodInDays.getText().contains("."))) return;

            fieldRentPrice.textboxKeyTyped(keycode, keynum);
            fieldPurchasePrice.textboxKeyTyped(keycode, keynum);
            fieldRentPeriodInDays.textboxKeyTyped(keycode, keynum);
        }

        @Override
        public void drawSelectionBox(int index, int width, int height)
        {

        }

        @Override
        public void drawBackground()
        {

        }
    }
}
