package com.minelife.util.client.render;

import com.google.common.collect.Maps;
import com.minelife.Minelife;
import com.minelife.cape.ModCapes;
import com.minelife.cape.client.ItemCapeRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.IImageBuffer;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Map;
import java.util.UUID;

public class CustomLayerCape implements LayerRenderer<AbstractClientPlayer> {

    private final RenderPlayer playerRenderer;

    public CustomLayerCape(RenderPlayer playerRendererIn) {
        this.playerRenderer = playerRendererIn;
    }

    public void doRenderLayer(AbstractClientPlayer entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if (isWearingCape(entitylivingbaseIn)) {
            ItemStack itemstack = entitylivingbaseIn.getItemStackFromSlot(EntityEquipmentSlot.CHEST);

            if (itemstack.getItem() != Items.ELYTRA) {
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                this.playerRenderer.bindTexture(getTexture(entitylivingbaseIn));
                GlStateManager.pushMatrix();
                GlStateManager.translate(0.0F, 0.0F, 0.125F);
                double d0 = entitylivingbaseIn.prevChasingPosX + (entitylivingbaseIn.chasingPosX - entitylivingbaseIn.prevChasingPosX) * (double) partialTicks - (entitylivingbaseIn.prevPosX + (entitylivingbaseIn.posX - entitylivingbaseIn.prevPosX) * (double) partialTicks);
                double d1 = entitylivingbaseIn.prevChasingPosY + (entitylivingbaseIn.chasingPosY - entitylivingbaseIn.prevChasingPosY) * (double) partialTicks - (entitylivingbaseIn.prevPosY + (entitylivingbaseIn.posY - entitylivingbaseIn.prevPosY) * (double) partialTicks);
                double d2 = entitylivingbaseIn.prevChasingPosZ + (entitylivingbaseIn.chasingPosZ - entitylivingbaseIn.prevChasingPosZ) * (double) partialTicks - (entitylivingbaseIn.prevPosZ + (entitylivingbaseIn.posZ - entitylivingbaseIn.prevPosZ) * (double) partialTicks);
                float f = entitylivingbaseIn.prevRenderYawOffset + (entitylivingbaseIn.renderYawOffset - entitylivingbaseIn.prevRenderYawOffset) * partialTicks;
                double d3 = (double) MathHelper.sin(f * 0.017453292F);
                double d4 = (double) (-MathHelper.cos(f * 0.017453292F));
                float f1 = (float) d1 * 10.0F;
                f1 = MathHelper.clamp(f1, -6.0F, 32.0F);
                float f2 = (float) (d0 * d3 + d2 * d4) * 100.0F;
                float f3 = (float) (d0 * d4 - d2 * d3) * 100.0F;

                if (f2 < 0.0F) {
                    f2 = 0.0F;
                }

                float f4 = entitylivingbaseIn.prevCameraYaw + (entitylivingbaseIn.cameraYaw - entitylivingbaseIn.prevCameraYaw) * partialTicks;
                f1 = f1 + MathHelper.sin((entitylivingbaseIn.prevDistanceWalkedModified + (entitylivingbaseIn.distanceWalkedModified - entitylivingbaseIn.prevDistanceWalkedModified) * partialTicks) * 6.0F) * 32.0F * f4;

                if (entitylivingbaseIn.isSneaking()) {
                    f1 += 25.0F;
                }

                GlStateManager.rotate(6.0F + f2 / 2.0F + f1, 1.0F, 0.0F, 0.0F);
                GlStateManager.rotate(f3 / 2.0F, 0.0F, 0.0F, 1.0F);
                GlStateManager.rotate(-f3 / 2.0F, 0.0F, 1.0F, 0.0F);
                GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
                this.playerRenderer.getMainModel().renderCape(0.0625F);
                GlStateManager.popMatrix();
            }
        }
    }

    public boolean shouldCombineTextures() {
        return false;
    }

    public boolean isWearingCape(AbstractClientPlayer player) {
        return player.getEntityData().hasKey("Cape") && player.getEntityData().getBoolean("Cape");
    }

    public static final Map<UUID, ResourceLocation> textures = Maps.newHashMap();
    public static final ResourceLocation defaultCape = new ResourceLocation(Minelife.MOD_ID, "textures/capes/default.png");

    private ResourceLocation getTexture(EntityPlayer player) {
        String pixels = ModCapes.itemCape.getPixels(player);

        if(pixels == null) return defaultCape;

        if(textures.containsKey(player.getUniqueID())) return textures.get(player.getUniqueID());

        final ResourceLocation resourceLocation = new ResourceLocation(Minelife.MOD_ID,"textures/capes/" + player.getUniqueID().toString() + ".png");
        TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();

        BufferedImage bi = new BufferedImage(64, 32, BufferedImage.TYPE_INT_ARGB);

        for (String pixel : pixels.split("\\;")) {
            if(pixel.contains(",")) {
                String[] data = pixel.split("\\,");
                int x = Integer.parseInt(data[0]);
                int y = Integer.parseInt(data[1]);
                int rgb = Integer.parseInt(data[2]);
                int red = (rgb >> 16) & 0x000000FF;
                int green = (rgb >>8 ) & 0x000000FF;
                int blue = (rgb) & 0x000000FF;
                bi.setRGB(x, y, new Color(red, green, blue, 255).getRGB());
            }
        }

        CustomTexture threadDownloadImageData = new CustomTexture( null, null, null, null);
        threadDownloadImageData.setBufferedImage(bi);
        textureManager.loadTexture(resourceLocation, threadDownloadImageData);
        textures.put(player.getUniqueID(), resourceLocation);
        return resourceLocation;
    }

    private class CustomTexture extends ThreadDownloadImageData {

        public CustomTexture(File p_i1049_1_, String p_i1049_2_, ResourceLocation p_i1049_3_, IImageBuffer p_i1049_4_) {
            super(p_i1049_1_, p_i1049_2_, p_i1049_3_, p_i1049_4_);
        }

        @Override
        protected void loadTextureFromServer() {

        }
    }

}