package com.minelife.economy.client.render;

import codechicken.lib.model.ModelRegistryHelper;
import codechicken.lib.model.bakedmodels.WrappedItemModel;
import codechicken.lib.render.item.CCRenderItem;
import codechicken.lib.render.item.IItemRenderer;
import codechicken.lib.texture.TextureUtils;
import codechicken.lib.util.TransformUtils;
import com.google.common.collect.Lists;
import com.minelife.economy.ModEconomy;
import com.minelife.util.fireworks.DyeColor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
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

public class RenderWalletItem extends WrappedItemModel implements IItemRenderer {

    private ModelResourceLocation wrappedModel1 = new ModelResourceLocation("minelife:wallet_bills", "inventory");
    private IBakedModel cashFirstModel, cashRenderModel;
    private ItemStack cashStack;

    public RenderWalletItem(Supplier<ModelResourceLocation> wrappedModel) {
        super(wrappedModel);
        cashStack = new ItemStack(ModEconomy.itemWallet, 1, DyeColor.values().length + 1);
        ModelRegistryHelper.registerPreBakeCallback(modelRegistry -> cashFirstModel = modelRegistry.getObject(wrappedModel1));
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

        if (cashRenderModel == null) {
            cashRenderModel = cashFirstModel.getOverrides().handleItemState(cashFirstModel, cashStack, world, Minecraft.getMinecraft().player);
        } else {
            CCRenderItem.getOverridenRenderItem().renderItem(cashStack, cashRenderModel);
        }

        GlStateManager.translate(-0.5, -0.5, -0.5);
        int color = new Color(DyeColor.values()[stack.getMetadata()].getColor().asRGB()).getRGB();

        IBakedModel model = wrapped.getOverrides().handleItemState(wrapped, stack, world, entity);
        renderModel(model, color, stack);

        GlStateManager.popMatrix();
    }

    @Override
    public IModelState getTransforms() {
        return TransformUtils.DEFAULT_HANDHELD_ROD;
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
}