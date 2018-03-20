package com.minelife.util.client;

import com.google.common.collect.Maps;
import com.mrcrayfish.device.util.Vec2d;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import java.awt.*;
import java.util.Map;

import static com.minelife.util.client.GuiHelper.drawImage;

public class GuiFakeInventory extends Gui {

    private GuiScreen parentScreen;

    public int x, y;
    private int slotWidth, slotHeight;
    private Map<Integer, Vec2d> slots;

    public GuiFakeInventory(int x, int y, int slotWidth, int slotHeight, GuiScreen parentScreen) {
        this.parentScreen = parentScreen;
        this.x = x;
        this.y = y;
        this.slotWidth = slotWidth;
        this.slotHeight = slotHeight;

        slots = Maps.newHashMap();

        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                slots.put(j + i * 3, new Vec2d(30 + j * slotWidth, 17 + i * slotHeight));
            }
        }

        for (int k = 0; k < 3; ++k) {
            for (int i1 = 0; i1 < 9; ++i1) {
                slots.put(i1 + k * 9 + 9, new Vec2d(8 + i1 * slotWidth, 84 + k * slotHeight));
            }
        }

        for (int l = 0; l < 9; ++l) {
            slots.put(l, new Vec2d(8 + l * slotWidth, 142));
        }
    }

    public void drawInventory(Minecraft mc, int mouseX, int mouseY) {
        slots.forEach((slot, pos) -> {
            drawSlot(x + (int) pos.x - 1, y + (int) pos.y - 1, slotWidth - 1, slotHeight - 1, 0x8B8B8B);
            if (mc.player.inventory.getStackInSlot(slot) != null && mc.player.inventory.getStackInSlot(slot).getItem() != Items.AIR)
                renderItemInventory(mc.player.inventory.getStackInSlot(slot), x + (int) pos.x, y + (int) pos.y, true);
            drawSlotHover(x + (int) pos.x - 1, y + (int) pos.y - 1, slotWidth - 1, slotHeight - 1, mouseX, mouseY);
        });

        slots.forEach((slot, pos) -> {
            if (mc.player.inventory.getStackInSlot(slot) != null && mc.player.inventory.getStackInSlot(slot).getItem() != Items.AIR) {
                if (mouseX >= x + pos.x - 1 && mouseX <= x + pos.x - 1 + slotWidth - 1 &&
                        mouseY >= y + pos.y - 1 && mouseY <= y + pos.y - 1 + slotHeight - 1)
                    renderToolTip(mc.player.inventory.getStackInSlot(slot), mouseX, mouseY);
            }
        });
    }

    public int mouseClicked(int mouseX, int mouseY) {
        for (Integer integer : slots.keySet()) {
            Vec2d pos = slots.get(integer);
            if (mouseX >= x + pos.x - 1 && mouseX <= x + pos.x - 1 + slotWidth - 1 &&
                    mouseY >= y + pos.y - 1 && mouseY <= y + pos.y - 1 + slotHeight - 1)
                return integer;
        }
        return -1;
    }

    private static final RenderItem itemRenderer = Minecraft.getMinecraft().getRenderItem();

    public static void renderItem3D(ItemStack itemStack) {
        GlStateManager.pushMatrix();
        GlStateManager.scale(0.5f, 0.5f, 0.5f);
        GlStateManager.pushAttrib();
        RenderHelper.enableStandardItemLighting();
        itemRenderer.renderItem(itemStack, ItemCameraTransforms.TransformType.FIXED);
        RenderHelper.disableStandardItemLighting();
        GlStateManager.popAttrib();
        GlStateManager.popMatrix();
    }

    public static void renderItemInventory(ItemStack itemStack, int x, int y, boolean overlay) {
        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.0F, 0.0F, 32.0F);
        itemRenderer.zLevel = 200.0F;
        net.minecraft.client.gui.FontRenderer font = itemStack.getItem().getFontRenderer(itemStack);
        if (font == null) font = Minecraft.getMinecraft().fontRenderer;
        itemRenderer.renderItemAndEffectIntoGUI(itemStack, x, y);
        if (overlay) itemRenderer.renderItemOverlayIntoGUI(font, itemStack, x, y, null);
        itemRenderer.zLevel = 0.0F;
        GlStateManager.popMatrix();
        RenderHelper.disableStandardItemLighting();
    }

    public void renderToolTip(ItemStack itemStack, int x, int y) {
        net.minecraftforge.fml.client.config.GuiUtils.preItemToolTip(itemStack);
        parentScreen.drawHoveringText(parentScreen.getItemToolTip(itemStack), x, y);
        net.minecraftforge.fml.client.config.GuiUtils.postItemToolTip();
    }

    public static void drawEntityOnScreen(int x, int y, int scale, float mouseX, float mouseY, EntityLivingBase entity) {
        GuiInventory.drawEntityOnScreen(x, y, scale, mouseX, mouseY, entity);
    }

    public static final void drawSlot(int x, int y, int width, int height, int colorRGB) {
        Color color = new Color(colorRGB);
        Color bottomBorder = color;
        Color topColor = color.brighter().brighter();
        Color border = color.darker().darker().darker();

        GlStateManager.disableLighting();
        GlStateManager.disableTexture2D();

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, 0f);

        GlStateManager.color(border.getRed() / 255f, border.getGreen() / 255f, border.getBlue() / 255f, 1f);
        drawImage(0, 0, width, 1);
        drawImage(0, 0, 1, height);

        GlStateManager.color(topColor.getRed() / 255f, topColor.getGreen() / 255f, topColor.getBlue() / 255f, 1f);
        drawImage(1, height, width, 1);
        drawImage(width, 1, 1, height - 1);

        GlStateManager.color(bottomBorder.getRed() / 255f, bottomBorder.getGreen() / 255f, bottomBorder.getBlue() / 255f, 1f);
        drawImage(width, 0, 1, 1);
        drawImage(0, height, 1, 1);
        drawImage(1, 1, width - 1, height - 1);

        GlStateManager.popMatrix();
        GlStateManager.enableTexture2D();
        GlStateManager.color(1, 1, 1, 1);
    }

    public static void drawSlot(int x, int y, int width, int height) {
        drawSlot(x, y, width, height, 0x8B8B8B);
    }

    public static void drawSlotHover(int x, int y, int width, int height, int mouseX, int mouseY) {
        if (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height) {
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            GlStateManager.colorMask(true, true, true, false);
            Gui.drawRect(x + 1, y + 1, x + width, y + height, -2130706433);
            GlStateManager.colorMask(true, true, true, true);
            GlStateManager.enableLighting();
            GlStateManager.enableDepth();
        }
    }


}
