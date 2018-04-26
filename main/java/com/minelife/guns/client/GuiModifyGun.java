package com.minelife.guns.client;

import codechicken.lib.vec.Vertex5;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.minelife.Minelife;
import com.minelife.guns.ModGuns;
import com.minelife.guns.item.EnumAttachment;
import com.minelife.guns.item.EnumGun;
import com.minelife.guns.item.ItemGun;
import com.minelife.guns.packet.PacketAttachment;
import com.minelife.guns.packet.PacketChangeSkin;
import com.minelife.util.client.GuiFakeInventory;
import com.minelife.util.client.GuiHelper;
import com.minelife.util.client.GuiScrollableContent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import org.apache.commons.lang3.text.WordUtils;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class GuiModifyGun extends GuiScreen {

    // TODO: Right clicking stack cuts in half and duplicates
    // TODO: May not be able to replicate, wallet shift click cash into it dissapears

    private int gunSlot;
    public ItemStack gunStack;
    public EnumGun gunType;
    private AttachmentScrollList attachmentScrollList;
    private SkinScrollList skinScrollList;
    private GuiTextField nameField;
    private int guiLeft, guiTop, xSize = 246, ySize = 186;
    private List<EnumGun> availableSkins;

    public GuiModifyGun(int gunSlot, List<EnumGun> availableSkins) {
        this.gunSlot = gunSlot;
        this.gunStack = Minecraft.getMinecraft().player.inventory.getStackInSlot(gunSlot);
        this.gunType = EnumGun.values()[gunStack.getMetadata()];
        this.availableSkins = availableSkins;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        GuiHelper.drawDefaultBackground(guiLeft, guiTop, xSize, ySize, 0x3d3d3d);

        GlStateManager.disableTexture2D();
        GlStateManager.color(204 / 255f, 204 / 255f, 204 / 255f, 188f / 255f);
        GuiHelper.drawRect(guiLeft + 10, guiTop + 10, 100, 100);
        GlStateManager.color(1, 1, 1, 1);
        GlStateManager.enableTexture2D();

        if (this.gunStack == null) return;

        GlStateManager.pushMatrix();
        GlStateManager.translate(this.guiLeft + 45, this.guiTop + 60, zLevel - 2200);
        GlStateManager.translate(8, 8, 0);
        double scale = 8;
        GlStateManager.scale(scale, scale, scale);
        GlStateManager.translate(-8, -8, 0);
        GuiFakeInventory.renderItemInventory(this.gunStack, 0, 0, true);
        GlStateManager.popMatrix();
        GlStateManager.disableLighting();

        this.nameField.drawTextBox();
        fontRenderer.drawString(TextFormatting.UNDERLINE + "Available Sites", this.attachmentScrollList.x, this.attachmentScrollList.y - 14, 0xFFFFFF);
        fontRenderer.drawString(TextFormatting.UNDERLINE + "Available Skins", this.skinScrollList.x, this.skinScrollList.y - 14, 0xFFFFFF);
        fontRenderer.drawString(TextFormatting.UNDERLINE + "Name", this.nameField.x, this.nameField.y - 14, 0xFFFFFF);
        int dWheel = Mouse.getDWheel();
        this.attachmentScrollList.draw(mouseX, mouseY, dWheel);
        this.skinScrollList.draw(mouseX, mouseY, dWheel);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        this.nameField.textboxKeyTyped(typedChar, keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.nameField.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);
        if (button.id == 1) {
            Minelife.getNetwork().sendToServer(new PacketAttachment(-2, gunSlot, nameField.getText()));
        } else if (button.id == 2) {
            ItemGun.setAttachment(gunStack, null);
            Minelife.getNetwork().sendToServer(new PacketAttachment(-1, gunSlot, nameField.getText()));
        }
    }

    @Override
    public void initGui() {
        super.initGui();
        this.guiLeft = (this.width - xSize) / 2;
        this.guiTop = (this.height - ySize) / 2;
        this.attachmentScrollList = new AttachmentScrollList(mc, guiLeft + 120, guiTop + 24, 80, 80);
        this.skinScrollList = new SkinScrollList(mc, guiLeft + 145, guiTop + 136, 90, 40);
        this.nameField = new GuiTextField(0, fontRenderer, this.guiLeft + 10, this.guiTop + 130, 70, 15);
        if (ItemGun.getCustomName(gunStack) != null) this.nameField.setText(ItemGun.getCustomName(gunStack));
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(1, this.nameField.x + this.nameField.width + 3, this.nameField.y - 2, 50, 20, "Rename"));
        this.buttonList.add(new GuiButton(2, this.guiLeft + 9, this.guiTop + ySize - 30, 120, 20, "Remove Current Site"));
        this.buttonList.get(1).enabled = false;
    }

    @Override
    public void updateScreen() {
        this.buttonList.get(1).enabled = gunStack != null && ItemGun.getAttachment(gunStack) != null;
        this.nameField.updateCursorCounter();
    }

    private Map<Integer, ItemStack> getAttachmentsFromInventory() {
        Map<Integer, ItemStack> map = Maps.newHashMap();
        for (int i = 0; i < mc.player.inventory.getSizeInventory(); i++) {
            if (mc.player.inventory.getStackInSlot(i).getItem() == ModGuns.itemAttachment)
                map.put(i, mc.player.inventory.getStackInSlot(i));
        }
        return map;
    }

    class AttachmentScrollList extends GuiScrollableContent {

        public AttachmentScrollList(Minecraft mc, int x, int y, int width, int height) {
            super(mc, x, y, width, height);
        }

        @Override
        public int getObjectHeight(int index) {
            return 12;
        }

        @Override
        public void drawObject(int index, int mouseX, int mouseY, boolean isHovering) {
            ItemStack attachmentStack = (ItemStack) getAttachmentsFromInventory().values().toArray()[index];
            fontRenderer.drawString(WordUtils.capitalizeFully(EnumAttachment.values()[attachmentStack.getMetadata()].name().replace("_", " ")), 2, 2, 0xFFFFFF);
        }

        @Override
        public int getSize() {
            return getAttachmentsFromInventory().size();
        }

        @Override
        public void elementClicked(int index, int mouseX, int mouseY, boolean doubleClick) {
            if (doubleClick) {
                if (mc.player.inventory.getStackInSlot(getSelectedSlot()).getItem() == ModGuns.itemAttachment) {
                    ItemGun.setAttachment(gunStack, EnumAttachment.values()[mc.player.inventory.getStackInSlot(getSelectedSlot()).getMetadata()]);
                    Minelife.getNetwork().sendToServer(new PacketAttachment(getSelectedSlot(), gunSlot, nameField.getText()));
                }
            }
        }

        public int getSelectedSlot() {
            if (selected == -1) return -1;
            int slot = (int) getAttachmentsFromInventory().keySet().toArray()[selected];
            return slot;
        }

    }

    class SkinScrollList extends GuiScrollableContent {

        public SkinScrollList(Minecraft mc, int x, int y, int width, int height) {
            super(mc, x, y, width, height);
        }

        @Override
        public int getObjectHeight(int index) {
            return 12;
        }

        @Override
        public void drawObject(int index, int mouseX, int mouseY, boolean isHovering) {
            fontRenderer.drawString(WordUtils.capitalizeFully(availableSkins.get(index).name().replace("_", " ")), 2, 2, 0xFFFFFF);
        }

        @Override
        public int getSize() {
            return availableSkins.size();
        }

        @Override
        public void elementClicked(int index, int mouseX, int mouseY, boolean doubleClick) {
            if (doubleClick) {
                Minelife.getNetwork().sendToServer(new PacketChangeSkin(availableSkins.get(index)));
            }
        }

    }
}
