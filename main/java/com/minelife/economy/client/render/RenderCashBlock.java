package com.minelife.economy.client.render;

import com.minelife.economy.ModEconomy;
import com.minelife.economy.tileentity.TileEntityCash;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class RenderCashBlock extends TileEntitySpecialRenderer<TileEntityCash> {

    public RenderCashBlock() {
    }

    @Override
    public void render(TileEntityCash te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        super.render(te, x, y, z, partialTicks, destroyStage, alpha);
        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5, y + 0.5, z + 0.5);
        GlStateManager.scale(2, 2, 2);
        GlStateManager.translate(0, 0, 0);
        renderItem(Minecraft.getMinecraft(), new ItemStack(Blocks.WOODEN_PRESSURE_PLATE));
        GlStateManager.popMatrix();

        double xOffset = 0, yOffset = 0, zOffset = 0;
        for (int i = 0; i < te.getInventory().getSizeInventory(); i++) {
            if (te.getInventory().getStackInSlot(i).getItem() != Items.AIR) {
                GlStateManager.pushMatrix();

                GlStateManager.translate(x + 0.5 + xOffset, y + yOffset + 0.08, z + 0.28 + zOffset);
                GlStateManager.scale(0.8, 0.4, 0.8);

                GlStateManager.rotate(90, 1, 0, 0);


                yOffset += 0.032;
                if (yOffset >= 0.85) {
                    yOffset = 0;
                    zOffset += 0.45;
                    if (zOffset >= 0.7) {
                        zOffset = 0;
                        xOffset -= 0.45;
                    }
                }

                renderItem(Minecraft.getMinecraft(), te.getInventory().getStackInSlot(i));
                GlStateManager.popMatrix();
            }
        }
    }

    public static void renderItem(Minecraft mc, ItemStack item) {
        mc.getItemRenderer().renderItem(mc.player, item, ItemCameraTransforms.TransformType.FIXED);
    }
}