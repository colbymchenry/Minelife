package com.minelife.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.IImageBuffer;
import net.minecraft.client.renderer.ImageBufferDownload;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.UUID;

public class SkinUtil {

    private static final TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();
    public static final File skinCacheDir = new File("skinCache");

    public static ResourceLocation loadSkin(UUID playerid) {
        final ResourceLocation resourcelocation = new ResourceLocation("skinCache/" + playerid.toString() + ".png");
        ITextureObject itextureobject = textureManager.getTexture(resourcelocation);

        if (itextureobject == null) {
            final IImageBuffer iimagebuffer = new ImageBufferDownload();
            File file1 = new File(skinCacheDir, playerid.toString() + ".png");
            ThreadDownloadImageData threaddownloadimagedata = new ThreadDownloadImageData(file1, "https://crafatar.com/skins/" + playerid.toString(), AbstractClientPlayer.locationStevePng, new IImageBuffer() {

                public BufferedImage parseUserSkin(BufferedImage image) {
                    if (iimagebuffer != null) {
                        image = iimagebuffer.parseUserSkin(image);
                    }

                    return image;
                }

                @Override
                public void func_152634_a()
                {
                    if (iimagebuffer != null) {
                        iimagebuffer.func_152634_a();
                    }
                }

            });
            textureManager.loadTexture(resourcelocation, threaddownloadimagedata);
        }
        return resourcelocation;
    }


}
