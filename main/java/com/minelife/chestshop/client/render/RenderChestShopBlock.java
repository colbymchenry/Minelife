package com.minelife.chestshop.client.render;

import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.OBJParser;
import codechicken.lib.texture.TextureUtils;
import codechicken.lib.vec.Translation;
import com.minelife.Minelife;
import com.minelife.chestshop.TileEntityChestShop;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.Map;

public class RenderChestShopBlock extends TileEntitySpecialRenderer<TileEntityChestShop> {

    private static CCModel model;

    public RenderChestShopBlock() {
        Map<String, CCModel> map = OBJParser.parseModels(new ResourceLocation(Minelife.MOD_ID, "models/block/chest_shop.obj"));
        model = CCModel.combine(map.values());
        model.apply(new Translation(0, 0, 0.1));
        model.computeNormals();
    }

    @Override
    public void render(TileEntityChestShop te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
        super.render(te, x, y, z, partialTicks, destroyStage, alpha);

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);

        if (te.getFacing() != null) {
            GlStateManager.translate(0.5, 0.5, 0.5);
            switch (te.getFacing()) {
                case NORTH:
                    GlStateManager.rotate(90, 0, 1, 0);
                    break;
                case SOUTH:
                    GlStateManager.rotate(270, 0, 1, 0);
                    break;
                case WEST:
                    GlStateManager.rotate(180, 0, 1, 0);
                    break;
            }
            GlStateManager.translate(-0.5, -0.5, -0.5);
        }

        GlStateManager.disableCull();
        RenderHelper.enableGUIStandardItemLighting();
        CCRenderState ccrs = CCRenderState.instance();
        TextureUtils.changeTexture("minelife:textures/block/chest_shop.png");
        ccrs.startDrawing(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_TEX_NORMAL);
        model.render(ccrs);
        ccrs.draw();
        GlStateManager.enableCull();
        GlStateManager.popMatrix();
    }
}
