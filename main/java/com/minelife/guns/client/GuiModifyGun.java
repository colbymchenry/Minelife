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
import java.util.List;
import java.util.Map;

public class GuiModifyGun extends GuiScreen {

    private int gunSlot;
    private float yRot;
    private ItemStack gunStack;
    private EnumGun gunType;
    private AttachmentScrollList attachmentScrollList;
    private GuiTextField nameField;
    private int guiLeft, guiTop, xSize = 246, ySize = 186;

    // TODO: add way to remove attachment.

    public GuiModifyGun(int gunSlot) {
        this.gunSlot = gunSlot;
        this.gunStack = Minecraft.getMinecraft().player.inventory.getStackInSlot(gunSlot);
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
        GlStateManager.translate(this.guiLeft + 50, this.guiTop + 50, 0);
        GlStateManager.translate(this.gunType.width, this.gunType.height, this.gunType.length);
        GlStateManager.rotate(yRot += 1, 0, 1, 0);
        GlStateManager.rotate(30, 1, 0, 0);
        GlStateManager.scale(3, 3, 3);
        GlStateManager.translate(-this.gunType.width, -this.gunType.height, -this.gunType.length);
        GuiFakeInventory.renderItemInventory(this.gunStack, 0, 0, true);
        GlStateManager.popMatrix();
        GlStateManager.disableLighting();

        fontRenderer.drawString("Current Site: " + (ItemGun.getAttachment(gunStack) == null ? "None" : ItemGun.getAttachment(gunStack).name()),
                guiLeft + 130, guiTop + 10, 0xFFFFFF);

        this.nameField.drawTextBox();
        fontRenderer.drawString(TextFormatting.UNDERLINE + "Inventory", this.attachmentScrollList.x, this.attachmentScrollList.y - 14, 0xFFFFFF);
        fontRenderer.drawString(TextFormatting.UNDERLINE + "Name", this.nameField.x, this.nameField.y - 14, 0xFFFFFF);
        this.attachmentScrollList.draw(mouseX, mouseY, Mouse.getDWheel());
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
        if(button.id == 0) {
            Minelife.getNetwork().sendToServer(new PacketAttachment(attachmentScrollList.getSelectedSlot(), gunSlot, nameField.getText()));
        } else if (button.id == 1) {
            Minelife.getNetwork().sendToServer(new PacketAttachment(-2, gunSlot, nameField.getText()));
        }
    }

    @Override
    public void initGui() {
        super.initGui();
        this.guiLeft = (this.width - xSize) / 2;
        this.guiTop = (this.height - ySize) / 2;
        this.gunType = EnumGun.values()[gunStack.getMetadata()];
        this.attachmentScrollList = new AttachmentScrollList(mc, guiLeft + 140, guiTop + 46, 80, 80, getAttachmentsFromInventory());
        this.nameField = new GuiTextField(0, fontRenderer, this.guiLeft + 10, this.guiTop + 130, 70, 15);
        if(ItemGun.getCustomName(gunStack) != null) this.nameField.setText(ItemGun.getCustomName(gunStack));
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(0, this.attachmentScrollList.x + 25, this.attachmentScrollList.y + this.attachmentScrollList.height + 2, 50, 20, "Apply"));
        this.buttonList.add(new GuiButton(1, this.nameField.x + this.nameField.width + 3, this.nameField.y - 2, 50, 20, "Rename"));
        this.buttonList.get(0).enabled = false;
    }

    @Override
    public void updateScreen() {
        this.buttonList.get(0).enabled = this.attachmentScrollList.selected != -1;
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

        private Map<Integer, ItemStack> attachments;

        public AttachmentScrollList(Minecraft mc, int x, int y, int width, int height, Map<Integer, ItemStack> attachments) {
            super(mc, x, y, width, height);
            this.attachments = attachments;
        }

        @Override
        public int getObjectHeight(int index) {
            return 12;
        }

        @Override
        public void drawObject(int index, int mouseX, int mouseY, boolean isHovering) {
            ItemStack attachmentStack = (ItemStack) attachments.values().toArray()[index];
            fontRenderer.drawString(WordUtils.capitalizeFully(EnumAttachment.values()[attachmentStack.getMetadata()].name().replace("_", " ")), 2, 2, 0xFFFFFF);
        }

        @Override
        public int getSize() {
            return attachments.size();
        }

        @Override
        public void elementClicked(int index, int mouseX, int mouseY, boolean doubleClick) {

        }

        public int getSelectedSlot() {
            if(selected == -1) return -1;
            int slot = (int) attachments.keySet().toArray()[selected];
            return slot;
        }

    }
}
