package com.minelife.util;

import com.google.common.collect.Maps;
import com.minelife.Minelife;
import com.minelife.util.client.render.RenderPlayerCustom;
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

    private static final TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();
    public static final Map<UUID, ResourceLocation> playerTextures = Maps.newHashMap();
    public static final Map<UUID, BufferedImage> playerTexturesBufferedImage = Maps.newHashMap();

    public static ResourceLocation loadSkin(UUID playerid) {
        final ResourceLocation resourcelocation = new ResourceLocation(Minelife.MOD_ID, "textures/skins/" + playerid.toString() + ".png");
        ITextureObject itextureobject = textureManager.getTexture(resourcelocation);

        if (itextureobject == null) {
            final IImageBuffer iimagebuffer = new ImageBufferDownload();


            ThreadDownloadImageData threaddownloadimagedata = new ThreadDownloadImageData(null, "https://crafatar.com/skins/" + playerid.toString(), DefaultPlayerSkin.getDefaultSkinLegacy(), new IImageBuffer() {

                public BufferedImage parseUserSkin(BufferedImage image) {
                    if (iimagebuffer != null) {
                        image = iimagebuffer.parseUserSkin(image);
                        playerTexturesBufferedImage.put(playerid, image);
                    }

                    return image;
                }

                @Override
                public void skinAvailable() {
                    if (iimagebuffer != null) {
                        iimagebuffer.skinAvailable();
                    }
                }

            });

            textureManager.loadTexture(resourcelocation, threaddownloadimagedata);
            playerTextures.put(playerid, resourcelocation);
        }
        return resourcelocation;
    }

    public static BufferedImage getBufferedImage(UUID player) {
        if(!playerTexturesBufferedImage.containsKey(player)) loadSkin(player);
        return playerTexturesBufferedImage.get(player);
    }

}
