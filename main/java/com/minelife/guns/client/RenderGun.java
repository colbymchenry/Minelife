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
import com.minelife.util.client.GuiHelper;
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
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;

public class RenderGun extends WrappedItemModel implements IItemRenderer {

    public static boolean recoilProcess = false;
    public static float recoilYaw, recoilPitch;

    public RenderGun(Supplier<ModelResourceLocation> wrappedModel) {
        super(wrappedModel);
    }

    @Override
    public void renderItem(ItemStack itemStack, ItemCameraTransforms.TransformType transformType) {
        EnumGun gun = EnumGun.values()[itemStack.getMetadata()];
        EnumAttachment attachment = ItemGun.getAttachment(itemStack);
        GlStateManager.pushMatrix();

        if (transformType == ItemCameraTransforms.TransformType.GUI) {
            GlStateManager.scale(1.5, 1.5, 1.5);
            GlStateManager.translate(-0.2, -0.1, -0.2);
        }

        if (transformType == ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND) {
            gun.shotAnimation.animate();

            boolean aimingDownSight = Mouse.isButtonDown(1) && Minecraft.getMinecraft().currentScreen == null;

            if (aimingDownSight) {
                gun.adsTransformations.forEach(Transformation::glApply);
                if (attachment != null && attachment.gunADSTransformation.contains(gun))
                    attachment.gunADSTransformation.get(gun).transformations.forEach(Transformation::glApply);
            } else {
                gun.firstPersonTransformations.forEach(Transformation::glApply);
            }

            GlStateManager.translate(gun.shotAnimation.posX(), gun.shotAnimation.posY(), gun.shotAnimation.posZ());
            GlStateManager.rotate(gun.shotAnimation.rotX(), 1, 0, 0);
            GlStateManager.rotate(gun.shotAnimation.rotY(), 0, 1, 0);
            GlStateManager.rotate(gun.shotAnimation.rotZ(), 0, 0, 1);

            if (aimingDownSight && (gun == EnumGun.AWP || gun == EnumGun.BARRETT)) {
                GlStateManager.popMatrix();
                return;
            }
        }

        if (transformType == ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND) {
            gun.thirdPersonTransformations.forEach(Transformation::glApply);
        }

        if (transformType == ItemCameraTransforms.TransformType.FIXED) {
            GlStateManager.scale(3, 3, 3);
            GlStateManager.translate(-0.3, -0.3, -0.3);
        }

        if(transformType == ItemCameraTransforms.TransformType.GROUND) {
            GlStateManager.scale(3, 3, 3);
            GlStateManager.translate(-0.3, -0.3, -0.3);
        }

        renderWrapped(itemStack);

        if (attachment != null && attachment.transformations.contains(gun))
            RenderAttachment.INSTANCE.renderItem(attachment.stack, transformType, gun);

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

    public static void handleRecoil(EnumGun gunType) {
        if (recoilProcess) {
            float reboundSpeed = gunType.getReboundSpeed();
            float yaw = Minecraft.getMinecraft().player.rotationYaw;
            float yawDiff = Math.abs(recoilYaw - yaw);
            float yawInc = recoilYaw > yaw ? (yawDiff / reboundSpeed) : recoilYaw < yaw ? -(yawDiff / reboundSpeed) : 0.0F;
            if (yawDiff < 0.2) yawInc = 0.0F;
            float pitch = Minecraft.getMinecraft().player.rotationPitch;
            float pitchDiff = Math.abs(recoilPitch - pitch);
            float pitchInc = recoilPitch > pitch ? -(pitchDiff / reboundSpeed) : recoilPitch < pitch ? (pitchDiff / reboundSpeed) : 0.0F;
            if (pitchDiff < 0.2) pitchInc = 0.0F;
            if (yawInc == 0 && pitchInc == 0) recoilProcess = false;
            Minecraft.getMinecraft().player.turn(yawInc, pitchInc);
        }

        if (Math.abs(Mouse.getEventDX()) > 1 || Math.abs(Mouse.getEventDY()) > 1) {
            recoilProcess = false;
            recoilYaw = Minecraft.getMinecraft().player.rotationYaw;
            recoilPitch = Minecraft.getMinecraft().player.rotationPitch;
        }
    }

}
