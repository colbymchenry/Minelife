package com.minelife.economy.client.render;

import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.OBJParser;
import codechicken.lib.texture.TextureUtils;
import codechicken.lib.vec.Scale;
import codechicken.lib.vec.Translation;
import com.minelife.Minelife;
import com.minelife.economy.tileentity.TileEntityATM;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.Map;

public class RenderATMBlock extends TileEntitySpecialRenderer<TileEntityATM> {

    private static ResourceLocation texture = new ResourceLocation("minelife:textures/block/atm.png");
    private static CCModel model;

    public RenderATMBlock() {
        Map<String, CCModel> map = OBJParser.parseModels(new ResourceLocation(Minelife.MOD_ID, "models/block/atm.obj"));
        model = CCModel.combine(map.values());
        model.apply(new Scale(0.6, 0.8, 0.6));
        model.apply(new Translation(0.8, 0.005, 0.5));
        model.computeNormals();
    }

    @Override
    public void render(TileEntityATM te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        super.render(te, x, y, z, partialTicks, destroyStage, alpha);

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);

        if (te.getFacing() != null) {
            GlStateManager.translate(0.5, 0.5, 0.5);
            switch (te.getFacing()) {
                case EAST:
                    GlStateManager.rotate(90, 0, 1, 0);
                    break;
                case WEST:
                    GlStateManager.rotate(270, 0, 1, 0);
                    break;
                case NORTH:
                    GlStateManager.rotate(180, 0, 1, 0);
                    break;
            }
            GlStateManager.translate(-0.5, -0.5, -0.5);
        }

        GlStateManager.disableCull();
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