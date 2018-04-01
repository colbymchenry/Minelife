package com.minelife.drugs.client.render;

import codechicken.lib.model.bakedmodels.WrappedItemModel;
import codechicken.lib.render.item.IItemRenderer;
import codechicken.lib.texture.TextureUtils;
import codechicken.lib.util.TransformUtils;
import com.google.common.collect.Lists;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.model.IModelState;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.List;
import java.util.function.Supplier;

public class ItemCocaLeafRenderer  extends WrappedItemModel implements IItemRenderer {

    public ItemCocaLeafRenderer(Supplier<ModelResourceLocation> wrappedModel) {
        super(wrappedModel);
    }

    @Override
    public void renderItem(ItemStack stack, ItemCameraTransforms.TransformType transformType) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(0.5, 0.5, 0.5);
        if (transformType == ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND ||
                transformType == ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND) {
            GlStateManager.scale(0.5, 0.5, 0.5);
            GlStateManager.translate(-0.3, -0.3, 0);
        }

        if(transformType == ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND)
            GlStateManager.rotate(180f, 0, 1, 0);


        GlStateManager.translate(-0.5, -0.5, -0.5);

        Color FAR = new Color(237, 234, 0);
        Color CLOSE = new Color(0, 237, 21);
        int[] transition_color = transition(FAR, CLOSE, stack.getItemDamage() / 80.0);

        int color = new Color(transition_color[0] > 255 ? 255 : transition_color[0], transition_color[1] > 255 ? 255 : transition_color[1], transition_color[2] > 255 ? 255 : transition_color[2]).getRGB();

        IBakedModel model = wrapped.getOverrides().handleItemState(wrapped, stack, world, entity);
        renderModel(model, color, stack);

        GlStateManager.popMatrix();
    }

    @Override
    public IModelState getTransforms() {
        return TransformUtils.DEFAULT_HANDHELD_ROD;
    }

    @Override
    public java.util.List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side, long rand) {
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
        return TextureUtils.getTexture("minelife:textures/item/wallet_empty.png");
    }

    @Override
    public ItemOverrideList getOverrides() {
        return ItemOverrideList.NONE;
    }

    private void renderModel(IBakedModel model, int color, ItemStack stack) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.ITEM);

        for (EnumFacing enumfacing : EnumFacing.values()) {
            this.renderQuads(bufferbuilder, model.getQuads((IBlockState) null, enumfacing, 0L), color, stack);
        }

        this.renderQuads(bufferbuilder, model.getQuads((IBlockState) null, (EnumFacing) null, 0L), color, stack);
        tessellator.draw();
    }

    private void renderQuads(BufferBuilder renderer, List<BakedQuad> quads, int color, ItemStack stack) {
        boolean flag = color == -1 && !stack.isEmpty();
        int i = 0;

        for (int j = quads.size(); i < j; ++i) {
            BakedQuad bakedquad = quads.get(i);
            int k = color;

            if (flag && bakedquad.hasTintIndex()) {
                k = Minecraft.getMinecraft().getItemColors().colorMultiplier(stack, bakedquad.getTintIndex());

                if (EntityRenderer.anaglyphEnable) {
                    k = TextureUtil.anaglyphColor(k);
                }

                k = k | -16777216;
            }

            net.minecraftforge.client.model.pipeline.LightUtil.renderQuadColor(renderer, bakedquad, k);
        }
    }


    private int[] transition(Color far, Color close, double ratio) {
        int red = (int) Math.abs((ratio * far.getRed()) + ((1 - ratio) * close.getRed()));
        int green = (int) Math.abs((ratio * far.getGreen()) + ((1 - ratio) * close.getGreen()));
        int blue = (int) Math.abs((ratio * far.getBlue()) + ((1 - ratio) * close.getBlue()));
        return new int[]{red, green, blue};
    }

}