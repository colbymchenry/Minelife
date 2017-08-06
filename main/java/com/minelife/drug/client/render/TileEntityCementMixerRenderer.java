package com.minelife.drug.client.render;

import com.minelife.Minelife;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;
import org.lwjgl.opengl.GL11;

public class TileEntityCementMixerRenderer extends TileEntitySpecialRenderer {

    ResourceLocation texture;
    ResourceLocation objModelLocation;
    IModelCustom model;

    public TileEntityCementMixerRenderer() {
        texture = new ResourceLocation(Minelife.MOD_ID, "textures/blocks/cement_mixer.png");
        objModelLocation = new ResourceLocation(Minelife.MOD_ID, "models/cement_mixer.obj");
        model = AdvancedModelLoader.loadModel(objModelLocation);
    }

    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float timeSinceLastTick) {
        bindTexture(texture);

        GL11.glDisable(GL11.GL_CULL_FACE);

        GL11.glPushMatrix();
        {
            GL11.glTranslated(x, y - 1, z);
            model.renderAll();
        }
        GL11.glPopMatrix();

        GL11.glEnable(GL11.GL_CULL_FACE);
    }

}