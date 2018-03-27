package com.minelife.guns.client;

import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.item.IItemRenderer;
import codechicken.lib.texture.TextureUtils;
import codechicken.lib.util.TransformUtils;
import codechicken.lib.vec.Rotation;
import codechicken.lib.vec.Scale;
import codechicken.lib.vec.Transformation;
import codechicken.lib.vec.Translation;
import com.google.common.collect.Lists;
import com.minelife.guns.item.EnumGunType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
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
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.util.List;

public class RenderGun implements IItemRenderer {

    public RenderGun() {
    }

    @Override
    public void renderItem(ItemStack stack, ItemCameraTransforms.TransformType transformType) {
        EnumGunType gunType = EnumGunType.values()[stack.getMetadata()];

        GlStateManager.pushMatrix();
        GlStateManager.pushAttrib();

        if (transformType == ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND) {

            gunType.shotAnimation.animate();
            GlStateManager.translate(gunType.shotAnimation.posX(), gunType.shotAnimation.posY(), gunType.shotAnimation.posZ());
            GlStateManager.rotate(gunType.shotAnimation.rotX(), 1, 0, 0);
            GlStateManager.rotate(gunType.shotAnimation.rotY(), 0, 1, 0);
            GlStateManager.rotate(gunType.shotAnimation.rotZ(), 0, 0, 1);

            if (Mouse.isButtonDown(1))
                gunType.adsTransformations.forEach(Transformation::glApply);
            else
                gunType.firstPersonTransformations.forEach(Transformation::glApply);
        }

        if (transformType == ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND)
            gunType.thirdPersonTransformations.forEach(Transformation::glApply);

        GlStateManager.disableCull();
        RenderHelper.enableGUIStandardItemLighting();
        CCRenderState ccrs = CCRenderState.instance();
        Minecraft.getMinecraft().getTextureManager().bindTexture(gunType.texture);
        ccrs.startDrawing(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_TEX_NORMAL);
        gunType.model.render(ccrs);
        ccrs.draw();
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
        return false;
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
