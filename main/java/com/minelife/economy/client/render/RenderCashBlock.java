package com.minelife.economy.client.render;

import com.minelife.economy.tileentity.TileEntityCash;
import com.minelife.util.client.GuiFakeInventory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class RenderCashBlock extends TileEntitySpecialRenderer<TileEntityCash> {

    private ItemStack woodenPressurePlate;

    public RenderCashBlock() {
        woodenPressurePlate = new ItemStack(Blocks.WOODEN_PRESSURE_PLATE);
    }

    @Override
    public void render(TileEntityCash te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        super.render(te, x, y, z, partialTicks, destroyStage, alpha);
        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5, y + 0.5, z + 0.5);
        GlStateManager.scale(2, 2, 2);
        GlStateManager.translate(0, 0, 0);
        GuiFakeInventory.renderItem(Minecraft.getMinecraft(), woodenPressurePlate, ItemCameraTransforms.TransformType.FIXED);
        GlStateManager.popMatrix();

        ItemStack lastStack = null;
        int hitMaxY = 0, hitMaxZ = 0;
        double xOffset = 0, yOffset = 0, zOffset = 0;
        for (int i = 0; i < te.getInventory().getSizeInventory(); i++) {
            if (te.getInventory().getStackInSlot(i).getItem() != Items.AIR) {
                yOffset += 0.032;
                if (yOffset >= 0.85) {
                    yOffset = 0;
                    zOffset += 0.45;
                    hitMaxY++;

                }

                if (zOffset >= 0.45) {
                    zOffset = 0;
                    xOffset -= 0.45;
                    hitMaxZ++;
                }

                lastStack = te.getInventory().getStackInSlot(i);
            }
        }

        if (lastStack == null) return;

        GlStateManager.pushMatrix();

        GlStateManager.translate(x + 0.5, y + 0.08, z + 0.28);
        GlStateManager.scale(0.8, (hitMaxY > 0 ? 28 : 0)+ 0.4+ (hitMaxZ > 0 ? 0 : (yOffset * 28)), 0.8);
        GlStateManager.rotate(90, 1, 0, 0);

        GuiFakeInventory.renderItem(Minecraft.getMinecraft(), lastStack, ItemCameraTransforms.TransformType.FIXED);


        GlStateManager.popMatrix();

        if(hitMaxZ > 0) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(x + 0.5, y + 0.08, z + 0.28 + 0.44);
            GlStateManager.scale(0.8, (hitMaxY > 1 ? 28 : 0) + 0.4 + (yOffset * 28), 0.8);
            GlStateManager.rotate(90, 1, 0, 0);
            GuiFakeInventory.renderItem(Minecraft.getMinecraft(), lastStack, ItemCameraTransforms.TransformType.FIXED);
            GlStateManager.popMatrix();
        }
    }

}