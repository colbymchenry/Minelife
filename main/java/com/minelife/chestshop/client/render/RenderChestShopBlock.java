package com.minelife.chestshop.client.render;

import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.OBJParser;
import codechicken.lib.texture.TextureUtils;
import codechicken.lib.vec.Translation;
import com.minelife.Minelife;
import com.minelife.chestshop.TileEntityChestShop;
import com.minelife.util.NumberConversions;
import com.minelife.util.client.GuiFakeInventory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.Map;

public class RenderChestShopBlock extends TileEntitySpecialRenderer<TileEntityChestShop> {

    private static CCModel model;
    private static ResourceLocation texture = new ResourceLocation("minelife:textures/block/chest_shop.png");

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
        Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
        ccrs.startDrawing(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_TEX_NORMAL);
        model.render(ccrs);
        ccrs.draw();

        if(te.getItem() != null) {
            GlStateManager.scale(0.5, 0.5, 0.5);
            GlStateManager.rotate(90, 0, 1, 0);
            GlStateManager.translate(-0.95, 1.32, 2.05);
            GuiFakeInventory.renderItem(Minecraft.getMinecraft(), te.getItem(), ItemCameraTransforms.TransformType.FIXED);
            GlStateManager.scale(0.03, 0.03, 0.03);
            GlStateManager.rotate(180f, 1f, 0f, 0f);
            GlStateManager.translate(8, 8, -4);
            getFontRenderer().drawString("x" + te.getItem().getCount(), 0, 1, 0xFFFFFF, false);
            GlStateManager.scale(0.7, 0.7, 0.7);
            GlStateManager.translate(-35, 32, 4);
            GlStateManager.translate(24 - (getFontRenderer().getStringWidth("$" + NumberConversions.format(te.getPrice()))) / 2, 0, 0);
            getFontRenderer().drawString("$" + NumberConversions.format(te.getPrice()), 0, -1, 0xFFFFFF, false);
        }

        GlStateManager.enableCull();
        GlStateManager.popMatrix();


    }

}
