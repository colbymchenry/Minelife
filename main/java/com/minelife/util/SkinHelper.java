package com.minelife.util;

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
import java.util.UUID;

public class SkinHelper {

    private static final TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();
    public static final File skinCacheDir = new File("skinCache");

    public static ResourceLocation loadSkin(UUID playerid) {
        final ResourceLocation resourcelocation = new ResourceLocation("skinCache/" + playerid.toString() + ".png");
        ITextureObject itextureobject = textureManager.getTexture(resourcelocation);

        if (itextureobject == null) {
            final IImageBuffer iimagebuffer = new ImageBufferDownload();
            File file1 = new File(skinCacheDir, playerid.toString() + ".png");
            ThreadDownloadImageData threaddownloadimagedata = new ThreadDownloadImageData(file1, "https://crafatar.com/skins/" + playerid.toString(), DefaultPlayerSkin.getDefaultSkinLegacy(), new IImageBuffer() {

                public BufferedImage parseUserSkin(BufferedImage image) {
                    if (iimagebuffer != null) {
                        image = iimagebuffer.parseUserSkin(image);
                    }

                    return image;
                }

                @Override
                public void skinAvailable()
                {
                    if (iimagebuffer != null) {
                        iimagebuffer.skinAvailable();
                    }
                }

            });
            textureManager.loadTexture(resourcelocation, threaddownloadimagedata);
        }
        return resourcelocation;
    }


}
