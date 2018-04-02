package com.minelife.economy.client.render;

import codechicken.lib.render.item.IItemRenderer;
import codechicken.lib.texture.TextureUtils;
import codechicken.lib.util.TransformUtils;
import com.google.common.collect.Lists;
import com.minelife.util.client.GuiHelper;
import mezz.jei.render.ItemStackFastRenderer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.model.IModelState;

import javax.annotation.Nullable;
import java.util.List;

public class RenderCashItem implements IItemRenderer {

    private static final ItemStack pressurePlate = new ItemStack(Blocks.WOODEN_PRESSURE_PLATE);

    @Override
    public void renderItem(ItemStack stack, ItemCameraTransforms.TransformType transformType) {
        GlStateManager.pushMatrix();

        if(transformType == ItemCameraTransforms.TransformType.GUI) {
            GlStateManager.translate(0.5, 0.7, 0.5);
            GlStateManager.rotate(40, 0, 1, 0);
            GlStateManager.rotate(-30, 1, 0, 0);
            GlStateManager.scale(1.8, 1.8, 1.8);
        }

        if(transformType == ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND ||
                transformType == ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND) {
            GlStateManager.translate(0.8, 0.5, 0.7);
            GlStateManager.rotate(40, 0, 1, 0);
            GlStateManager.rotate(-70, 1, 0, 0);
            GlStateManager.scale(3, 3, 3);
        }

        if(transformType == ItemCameraTransforms.TransformType.GROUND) {
            GlStateManager.translate(0, 0, 0.9);
            GlStateManager.rotate(-40, 1, 0, 1);
            GlStateManager.scale(6, 6, 6);
        }

        if(transformType == ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND ||
                transformType == ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND) {
            GlStateManager.translate(0.5, 0.5, 0.5);
            GlStateManager.rotate(40, 0, 1, 0);
            GlStateManager.rotate(-5, 1, 0, 0);
            GlStateManager.scale(3, 3, 3);
        }

        GuiHelper.renderItem(pressurePlate, transformType);
        GlStateManager.popMatrix();
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
        return true;
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return TextureUtils.getTexture("minelife:textures/item/cash_1.png");
    }

    @Override
    public ItemOverrideList getOverrides() {
        return ItemOverrideList.NONE;
    }

}
