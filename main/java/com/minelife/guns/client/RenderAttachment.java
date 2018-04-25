package com.minelife.guns.client;

import codechicken.lib.model.bakedmodels.WrappedItemModel;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.item.IItemRenderer;
import codechicken.lib.texture.TextureUtils;
import codechicken.lib.util.TransformUtils;
import codechicken.lib.vec.Rotation;
import codechicken.lib.vec.Scale;
import codechicken.lib.vec.Transformation;
import codechicken.lib.vec.Translation;
import com.google.common.collect.Lists;
import com.minelife.guns.item.EnumAttachment;
import com.minelife.guns.item.EnumGun;
import com.minelife.guns.item.ItemAttachment;
import com.minelife.guns.item.ItemGun;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.model.IModelState;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;

import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;

public class RenderAttachment extends WrappedItemModel implements IItemRenderer {

    public static RenderAttachment INSTANCE;

    public RenderAttachment(Supplier<ModelResourceLocation> wrappedModel) {
        super(wrappedModel);
        INSTANCE = this;
    }

    // TODO: Scale, Translate, Rotate in that order
    // TODO: Guns and attachments render slightly yellow on bullet fire
    @Override
    public void renderItem(ItemStack stack, ItemCameraTransforms.TransformType transformType) {
        renderItem(stack, transformType, null);
    }

    public void renderItem(ItemStack stack, ItemCameraTransforms.TransformType transformType, EnumGun gun) {
        EnumAttachment attachment = EnumAttachment.values()[stack.getMetadata()];

        GlStateManager.pushMatrix();

        GlStateManager.translate(0.5, 0.5, 0.5);
        GlStateManager.scale(4, 4, 4);

        if (gun != null)
            attachment.transformations.get(gun).transformations.forEach(Transformation::glApply);

//        if (transformType == ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND) {
//            attachment.firstPersonTransformations.forEach(Transformation::glApply);
//        }
//
//        if (transformType == ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND) {
//            attachment.thirdPersonTransformations.forEach(Transformation::glApply);
//        }
//
//        if (transformType == ItemCameraTransforms.TransformType.GUI)
//            attachment.guiTransformations.forEach(Transformation::glApply);

        GlStateManager.translate(-0.5, -0.5, -0.5);
        GlStateManager.color(1, 1, 1, 1);
        renderWrapped(stack);

        if(transformType == ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND) {
            GlStateManager.pushMatrix();
            double scale = 0.005;
            double w = (70 * scale), h = (86 * scale), l = (111.2 * scale);
            GlStateManager.translate(w, h, l);
            attachment.reticleTransformations.forEach(Transformation::glApply);
            GlStateManager.translate(-w, -h, -l);
            GlStateManager.disableLighting();
            GlStateManager.blendFunc(GL11.GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
            Minecraft.getMinecraft().getTextureManager().bindTexture(attachment.textureReticle);
            GlStateManager.color(1, 1, 1, 90f / 255f);
            GlStateManager.enableBlend();
            for (int i = 0; i < 8; i++)
                Gui.drawModalRectWithCustomSizedTexture(0, 0, 0, 0, 64, 64, 64, 64);
            GlStateManager.enableLighting();
            GlStateManager.color(1, 1, 1, 1);
            GlStateManager.popMatrix();
        }

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
        return false;
    }

    @Override
    public boolean isGui3d() {
        return false;
    }

    @Override
    public boolean isBuiltInRenderer() {
        return true;
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return TextureUtils.getTexture("minelife:textures/item/guns/AK47.png");
    }

    @Override
    public ItemOverrideList getOverrides() {
        return ItemOverrideList.NONE;
    }

}
