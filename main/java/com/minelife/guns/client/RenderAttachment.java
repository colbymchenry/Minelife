package com.minelife.guns.client;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.item.IItemRenderer;
import codechicken.lib.texture.TextureUtils;
import codechicken.lib.util.TransformUtils;
import codechicken.lib.vec.Scale;
import codechicken.lib.vec.Transformation;
import codechicken.lib.vec.Translation;
import com.google.common.collect.Lists;
import com.minelife.guns.item.EnumAttachment;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.model.IModelState;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.util.List;

import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;

public class RenderAttachment implements IItemRenderer {

    public RenderAttachment() {
    }

    // TODO: Guns and attachments render slightly yellow on bullet fire
    @Override
    public void renderItem(ItemStack stack, ItemCameraTransforms.TransformType transformType) {
        EnumAttachment attachment = EnumAttachment.values()[stack.getMetadata()];

        GlStateManager.pushMatrix();
        GlStateManager.pushAttrib();

        if (transformType == ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND) {
            RenderHelper.enableGUIStandardItemLighting();
            attachment.firstPersonTransformations.forEach(Transformation::glApply);
        }

        if (transformType == ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND)
            attachment.thirdPersonTransformations.forEach(Transformation::glApply);

        if (transformType == ItemCameraTransforms.TransformType.GUI)
            attachment.guiTransformations.forEach(Transformation::glApply);

        GlStateManager.disableCull();
        CCRenderState ccrs = CCRenderState.instance();
        Minecraft.getMinecraft().getTextureManager().bindTexture(attachment.textureModel);
        ccrs.startDrawing(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_TEX_NORMAL);
        GlStateManager.color(1, 1, 1, 1);
        GlStateManager.disableBlend();
        attachment.model.render(ccrs);
        ccrs.draw();

        if(transformType == ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND) {
            GlStateManager.disableLighting();
            GlStateManager.blendFunc(GL11.GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
            Minecraft.getMinecraft().getTextureManager().bindTexture(attachment.textureReticle);
            GlStateManager.color(1, 1, 1, 90f / 255f);
            GlStateManager.enableBlend();
            attachment.reticleTransformations.forEach(Transformation::glApply);
            for(int i = 0; i < 8; i++)
                Gui.drawModalRectWithCustomSizedTexture(0, 0, 0, 0, 64, 64, 64, 64);
            GlStateManager.enableLighting();
            GlStateManager.color(1, 1, 1, 1);
        }

        GlStateManager.enableCull();
        GlStateManager.popAttrib();
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
        return TextureUtils.getTexture("minelife:textures/item/guns/AK47.png");
    }

    @Override
    public ItemOverrideList getOverrides() {
        return ItemOverrideList.NONE;
    }

}
