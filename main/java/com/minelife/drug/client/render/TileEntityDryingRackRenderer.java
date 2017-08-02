package com.minelife.drug.client.render;

import com.minelife.Minelife;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;
import org.lwjgl.opengl.GL11;

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

        GL11.glEnable(GL11.GL_CULL_FACE);
    }

}
