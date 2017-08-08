package com.minelife.drug.client.render;

import com.minelife.MLItems;
import com.minelife.Minelife;
import com.minelife.drug.tileentity.TileEntityDryingRack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.opengl.GL11;

import java.util.Map;

// TODO: make model and texture and render leaves on the rack
public class TileEntityDryingRackRenderer extends TileEntitySpecialRenderer {

    ResourceLocation texture;
    ResourceLocation objModelLocation;
    IModelCustom model;

    public TileEntityDryingRackRenderer() {
        texture = new ResourceLocation(Minelife.MOD_ID, "textures/blocks/drying_rack.png");
        objModelLocation = new ResourceLocation(Minelife.MOD_ID, "models/drying_rack.obj");
        model = AdvancedModelLoader.loadModel(objModelLocation);
    }

    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float timeSinceLastTick) {
        bindTexture(texture);

        GL11.glDisable(GL11.GL_CULL_FACE);

        GL11.glPushMatrix();
        {
            GL11.glTranslated(x, y, z);
            model.renderAll();
        }
        GL11.glPopMatrix();

        TileEntityDryingRack drying_rack = (TileEntityDryingRack) tileEntity;
        Map<Integer, ItemStack> leaves = drying_rack.get_leaves();

        double xOffset = 0.41;
        double zOffset = 0.2;
        int count = 0;
        int row = 0;
        for(Integer slot : leaves.keySet()) {
            if(leaves.get(slot) != null) {
                GL11.glPushMatrix();
                {
                    GL11.glTranslated(x + xOffset - 0.18, y + 0.9, z + zOffset - 0.3);
                    EntityItem entItem = new EntityItem(Minecraft.getMinecraft().theWorld, 0D, 0D, 0D, leaves.get(slot));
                    entItem.hoverStart = 0.0F;
                    RenderItem.renderInFrame = true;
                    if(row == 1) {
                        GL11.glRotatef(90f, 0, 1, 0);
                        GL11.glTranslatef(-0.35f, 0f, -0.33f);
                    }
                    GL11.glRotatef(135f, 1, 0, 0);

                    RenderManager.instance.renderEntityWithPosYaw(entItem, 0.0D, 0.0D, 0.0D, 0.0F, 0.0F);
                    RenderItem.renderInFrame = false;
                }
                GL11.glPopMatrix();
            }
            count++;
            zOffset += 0.3;
            if(count > 2) {
                xOffset += 0.22;
                zOffset = 0.2;
                count = 0;
                row++;
            }
        }


        GL11.glEnable(GL11.GL_CULL_FACE);
    }

}
