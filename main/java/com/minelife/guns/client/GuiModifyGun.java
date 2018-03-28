package com.minelife.guns.client;

import com.minelife.guns.item.EnumGun;
import com.minelife.util.client.GuiFakeInventory;
import com.minelife.util.client.GuiHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;

import java.io.IOException;

public class GuiModifyGun extends GuiScreen {

    private int gunSlot;
    private float yRot;
    private ItemStack gunStack;
    private EnumGun gunType;
    private int guiLeft, guiTop, xSize = 246, ySize = 186;

    public GuiModifyGun(int gunSlot) {
        this.gunSlot = gunSlot;
        this.gunStack = Minecraft.getMinecraft().player.inventory.getStackInSlot(gunSlot);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        GuiHelper.drawDefaultBackground(guiLeft, guiTop, xSize, ySize, 0x3d3d3d);

        int middle = xSize / 2;
        int item_render_width = 60;
        int item_render_x = guiLeft + ((middle - item_render_width) / 2);

        GlStateManager.disableTexture2D();
        GlStateManager.color(64 / 255f, 0, 62 / 255f, 188f/255f);
        GuiHelper.drawRect(item_render_x, guiTop + 10, 60, 60);
        GlStateManager.enableTexture2D();


        if(this.gunStack != null) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(this.guiLeft + 54, this.guiTop + 22, 0);
            this.gunType.width =7;
            this.gunType.height = 1;
            this.gunType.length = 401;
            GlStateManager.translate(this.gunType.width, this.gunType.height, this.gunType.length);
            GlStateManager.rotate(yRot += 1, 0, 1, 0);
            GlStateManager.rotate(30, 1, 0, 0);
            GlStateManager.scale(4, 4, 4);
            GlStateManager.translate(-this.gunType.width, -this.gunType.height, -this.gunType.length);
            GuiFakeInventory.renderItemInventory(this.gunStack, 0, 0, true);
            GlStateManager.popMatrix();
        }
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);
    }

    @Override
    public void initGui() {
        super.initGui();
        this.guiLeft = (this.width - xSize) / 2;
        this.guiTop = (this.height - ySize) / 2;
        this.gunType = EnumGun.values()[gunStack.getMetadata()];
    }
}
