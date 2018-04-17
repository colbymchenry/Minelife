package com.minelife.util.client;

import com.google.common.collect.Maps;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IImageBuffer;
import net.minecraft.client.renderer.ImageBufferDownload;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.util.ResourceLocation;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Map;
import java.util.UUID;

public class SkinHelper {

    @SuppressWarnings("ConstantConditions")
    public static ResourceLocation loadSkin(UUID playerid) {
        TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();
        final ResourceLocation resourcelocation = new ResourceLocation("skins/" + playerid.toString() + ".png");
        ITextureObject itextureobject = textureManager.getTexture(resourcelocation);

        if (itextureobject == null) {
            final IImageBuffer iimagebuffer = new ImageBufferDownload();
            ThreadDownloadImageData threaddownloadimagedata = new ThreadDownloadImageData(null, "https://crafatar.com/skins/" + playerid.toString(), DefaultPlayerSkin.getDefaultSkinLegacy(), new IImageBuffer() {
                @Override
                public BufferedImage parseUserSkin(BufferedImage image) {
                    if (iimagebuffer != null) image = iimagebuffer.parseUserSkin(image);
                    return image;
                }
                @Override
                public void skinAvailable() {
                    if (iimagebuffer != null) iimagebuffer.skinAvailable();
                }
            });
            textureManager.loadTexture(resourcelocation, threaddownloadimagedata);
        }

        return resourcelocation;
    }

}
