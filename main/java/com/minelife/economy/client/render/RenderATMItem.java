package com.minelife.economy.client.render;

import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.OBJParser;
import codechicken.lib.render.item.IItemRenderer;
import codechicken.lib.texture.TextureUtils;
import codechicken.lib.util.TransformUtils;
import codechicken.lib.vec.Rotation;
import codechicken.lib.vec.Scale;
import codechicken.lib.vec.Translation;
import com.google.common.collect.Lists;
import com.minelife.Minelife;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.model.IModelState;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public class RenderATMItem implements IItemRenderer {

    private static CCModel model;

    public RenderATMItem() {
        Map<String, CCModel> map = OBJParser.parseModels(new ResourceLocation(Minelife.MOD_ID, "models/block/atm.obj"));
        model = CCModel.combine(map.values());
        model.apply(new Rotation(90.3, 0, 1, 0));
        model.apply(new Scale(0.5, 0.5, 0.5));
        model.apply(new Translation(0, -0.4, 0.55));
        model.computeNormals();
    }

    @Override
    public void renderItem(ItemStack stack, ItemCameraTransforms.TransformType transformType) {
        GlStateManager.pushAttrib();
        GlStateManager.enableDepth();
        GlStateManager.disableCull();
        RenderHelper.enableGUIStandardItemLighting();
        CCRenderState ccrs = CCRenderState.instance();
        TextureUtils.changeTexture("minelife:textures/block/atm.png");
        ccrs.startDrawing(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_TEX_NORMAL);
        model.render(ccrs);
        ccrs.draw();
        GlStateManager.enableCull();
        GlStateManager.popAttrib();
    }

    @Override
    public IModelState getTransforms() {
        return TransformUtils.DEFAULT_BLOCK;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
        return Lists.newArrayList();
    }

    @Override
    public boolean isAmbientOcclusion() {
        return true;
    }

    @Override
    public boolean isGui3d() {
        return true;
    }

    @Override
    public boolean isBuiltInRenderer() {
        return false;
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return TextureUtils.getTexture("minelife:textures/block/atm.png");
    }

    @Override
    public ItemOverrideList getOverrides() {
        return ItemOverrideList.NONE;
    }

}

