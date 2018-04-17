package com.minelife.cape.client;

import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.OBJParser;
import codechicken.lib.render.item.IItemRenderer;
import codechicken.lib.texture.TextureUtils;
import codechicken.lib.util.TransformUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.minelife.Minelife;
import com.minelife.cape.ModCapes;
import com.minelife.util.client.render.CustomLayerCape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.model.IModelState;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ItemCapeRenderer implements IItemRenderer {

    public static final Map<UUID, ResourceLocation> textures = Maps.newHashMap();
    private static CCModel model;

    public ItemCapeRenderer() {
        Map<String, CCModel> map = OBJParser.parseModels(new ResourceLocation(Minelife.MOD_ID, "models/cape.obj"));
        model = CCModel.combine(map.values());
        model.computeNormals();
    }

    @Override
    public void renderItem(ItemStack stack, ItemCameraTransforms.TransformType transformType) {
        GlStateManager.pushMatrix();
        GlStateManager.disableCull();

        if(transformType == ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND) {
            GlStateManager.translate(0, 0.8, 1);
            GlStateManager.rotate(180f, 0, 1, 0);
            RenderHelper.enableGUIStandardItemLighting();
        }

        CCRenderState ccrs = CCRenderState.instance();
        Minecraft.getMinecraft().getTextureManager().bindTexture(getTexture(stack) == null ? CustomLayerCape.defaultCape : getTexture(stack));
        ccrs.startDrawing(GL11.GL_TRIANGLES, DefaultVertexFormats.POSITION_TEX_NORMAL);
        model.render(ccrs);
        ccrs.draw();

        GlStateManager.enableCull();
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
        return TextureUtils.getTexture("minelife:textures/capes/default.png");
    }

    @Override
    public ItemOverrideList getOverrides() {
        return ItemOverrideList.NONE;
    }

    private ResourceLocation getTexture(ItemStack item) {
        String pixels = ModCapes.itemCape.getPixels(item);

        if(pixels == null) return null;

        if(textures.containsKey(ModCapes.itemCape.getUniqueID(item))) return textures.get(ModCapes.itemCape.getUniqueID(item));

        final ResourceLocation resourceLocation = new ResourceLocation(Minelife.MOD_ID,"textures/capes/" + ModCapes.itemCape.getUniqueID(item) + ".png");
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
        textures.put(ModCapes.itemCape.getUniqueID(item), resourceLocation);
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