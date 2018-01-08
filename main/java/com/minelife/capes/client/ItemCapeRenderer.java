package com.minelife.capes.client;

import com.google.common.collect.Maps;
import com.minelife.MLItems;
import com.minelife.Minelife;
import com.minelife.util.client.render.CapeLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.IImageBuffer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

public class ItemCapeRenderer implements IItemRenderer {

    private Map<String, ResourceLocation> textures = Maps.newHashMap();
    private static final ModelBiped modelBipedMain = new ModelBiped();

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return true;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return true;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data1) {
        Minecraft mc = Minecraft.getMinecraft();

        GL11.glDisable(GL11.GL_CULL_FACE);

        if(type == ItemRenderType.INVENTORY) {
            GL11.glEnable(GL11.GL_BLEND);
        }

        if(MLItems.cape.getUUID(item) == null || MLItems.cape.getUUID(item).isEmpty()) {
            mc.getTextureManager().bindTexture(CapeLoader.defaultCape);
        } else {
            if (textures.containsKey(MLItems.cape.getUUID(item))) {
                mc.getTextureManager().bindTexture(textures.get(MLItems.cape.getUUID(item)));
            } else {
                loadTexture(item);
                mc.getTextureManager().bindTexture(CapeLoader.defaultCape);
            }
        }

        GL11.glPushMatrix();
        GL11.glTranslatef(0f, 0.5f, 0f);
        GL11.glRotatef(180.0F, 0.0F, 1, 0.0F);
        GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
        if(type == ItemRenderType.ENTITY) {
            GL11.glRotatef(-90f, 0f, 1f, 0f);
        }
        if(type == ItemRenderType.EQUIPPED_FIRST_PERSON) {
            GL11.glTranslatef(0.3f, -1.3f, -0.3f);
            GL11.glRotatef(100f, 0f, 1f, 0f);
        }
        this.modelBipedMain.renderCloak(0.0625F);
        GL11.glPopMatrix();

        GL11.glDisable(GL11.GL_BLEND);
        // entityRenderer activateNextShader adds cool effects for drugs
//        Minecraft.getMinecraft().entityRenderer.activateNextShader();
    }

    private void loadTexture(ItemStack item) {
        String pixels = MLItems.cape.getPixels(item);

        if(pixels == null) return;

        final ResourceLocation resourceLocation = new ResourceLocation(Minelife.MOD_ID,"textures/capes/" + MLItems.cape.getUUID(item) + ".png");
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
        textures.put(MLItems.cape.getUUID(item), resourceLocation);
    }

    private class CustomTexture extends ThreadDownloadImageData {

        public CustomTexture(File p_i1049_1_, String p_i1049_2_, ResourceLocation p_i1049_3_, IImageBuffer p_i1049_4_) {
            super(p_i1049_1_, p_i1049_2_, p_i1049_3_, p_i1049_4_);
        }

        @Override
        protected void func_152433_a() {

        }
    }
}
