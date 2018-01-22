package com.minelife.economy.cash;

import com.minelife.util.client.GuiUtil;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.tileentity.TileEntity;
import org.lwjgl.opengl.GL11;

public class TileEntityCashRenderer extends TileEntitySpecialRenderer {

    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float partialTicks) {

            TileEntityCash TileCash = (TileEntityCash) tileEntity;
            double xOffset = 0, yOffset = 0, zOffset = 0;
            for (int i = 0; i < TileCash.getSizeInventory(); i++) {
                if(TileCash.getStackInSlot(i) != null) {
                    GL11.glPushMatrix();
                    {

                    GL11.glTranslated(x + 0.75 + xOffset, y + yOffset, z + 0.03 + zOffset);
                    GL11.glScalef(0.5f, 0.5f, 0.5f);
                    GL11.glRotatef(90f, 1f, 0f, 0f);

                        yOffset += 0.038;
                        if(yOffset >= 0.31) {
                            yOffset = 0;
                            zOffset += 0.25;
                            if(zOffset >= 0.7) {
                                zOffset = 0;
                                xOffset -= 0.45;
                            }
                        }

                    GuiUtil.render_item_in_world(Minecraft.getMinecraft(), TileCash.getStackInSlot(i));
                    }
                    GL11.glPopMatrix();
                }
            }

    }
}
