package com.minelife.util.client.render;

import com.minelife.Minelife;
import com.minelife.netty.ModNetty;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IImageBuffer;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * A simple CapesAPI implementation for Minecraft client developers.
 *
 * @author  Matthew Hatcher
 * @author  Marco MC
 * @version 2.1.0, February 2017
 */
public class CapeLoader {

    public static final ResourceLocation defaultCape = new ResourceLocation(Minelife.MOD_ID, "textures/capes/default.png");

    private static Map < UUID, ResourceLocation > capes = new HashMap <UUID, ResourceLocation> ();

    /**
     * Load cape from the webserver and put the cape as resourcelocation to the capes hashmap
     * @param uuid
     */
    public static void loadCape(final UUID uuid) {
        if(ModNetty.getNettyConnection() == null || ModNetty.getNettyConnection().getChannel() == null || !ModNetty.getNettyConnection().getChannel().isActive()) return;

        String url = "http:/" + ModNetty.getNettyConnection().getChannel().localAddress().toString().split("\\:")[0] + "/capes/" + uuid.toString() + ".png";
        final ResourceLocation resourceLocation = new ResourceLocation(Minelife.MOD_ID,"textures/capes/" + uuid.toString() + ".png");
        TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();

        IImageBuffer iImageBuffer = new IImageBuffer() {

            @Override
            public BufferedImage parseUserSkin(BufferedImage image) {
                return image;
            }

            // skinAvailable
            @Override
            public void skinAvailable() {
                capes.put(uuid, resourceLocation);
            }
        };

        ThreadDownloadImageData threadDownloadImageData = new ThreadDownloadImageData((File) null, url, (ResourceLocation) null, iImageBuffer);
        textureManager.loadTexture(resourceLocation, threadDownloadImageData);
    }

    /**
     * Remove the cape of the user from the cape hashmap
     * @param uuid
     */
    public static void deleteCape(UUID uuid) {
        capes.remove(uuid);
    }

    /**
     * Get the cape of the user from the cape hashmap
     * @param uuid
     * @return
     */
    public static ResourceLocation getCape(UUID uuid) {
        return capes.containsKey(uuid) ? capes.get(uuid) : defaultCape;
    }

    public static boolean hasCape(UUID uuid) {
        return capes.containsKey(uuid);
    }

    public static Map<UUID, ResourceLocation> getCapes() {
        return capes;
    }
}