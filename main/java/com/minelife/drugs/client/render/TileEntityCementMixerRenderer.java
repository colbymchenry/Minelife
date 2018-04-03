package com.minelife.drugs.client.render;

import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.OBJParser;
import codechicken.lib.vec.Scale;
import codechicken.lib.vec.Translation;
import com.minelife.Minelife;
import com.minelife.drugs.tileentity.TileEntityCementMixer;
import com.minelife.drugs.tileentity.TileEntityLeafMulcher;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.Map;

public class TileEntityCementMixerRenderer extends TileEntitySpecialRenderer<TileEntityCementMixer> {

    private static CCModel model;
    private static ResourceLocation texture = new ResourceLocation("minelife:textures/block/cement_mixer.png");

    public TileEntityCementMixerRenderer() {
        Map<String, CCModel> map = OBJParser.parseModels(new ResourceLocation(Minelife.MOD_ID, "models/block/cement_mixer.obj"));
        model = CCModel.combine(map.values());
        model.apply(new Translation(0, -1, 0));
        model.computeNormals();
    }

    @Override
    public void render(TileEntityCementMixer te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        super.render(te, x, y, z, partialTicks, destroyStage, alpha);

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        GlStateManager.disableCull();

//        int lightc = world.getCombinedLight(flow.pipe.getHolder().getPipePos(), 0);
//        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lightc % (float) 0x1_00_00, lightc / (float) 0x1_00_00);

        RenderHelper.enableGUIStandardItemLighting();
        CCRenderState ccrs = CCRenderState.instance();
        Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
        ccrs.startDrawing(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_TEX_NORMAL);
        model.render(ccrs);
        ccrs.draw();

        GlStateManager.enableCull();
        GlStateManager.popMatrix();
    }

}
