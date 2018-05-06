package com.minelife.util.client.render;

import com.google.common.collect.Maps;
import com.minelife.Minelife;
import com.minelife.emt.ModEMT;
import com.minelife.util.SkinHelper;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.renderer.IImageBuffer;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.UUID;

public class SkinChanger {

    public static final Map<UUID, ResourceLocation> playerTextures = Maps.newHashMap();
    public static final ResourceLocation emtSkin = new ResourceLocation(Minelife.MOD_ID, "textures/entity/job/emt.png");
    public static final ResourceLocation policeSkin = new ResourceLocation(Minelife.MOD_ID, "textures/entity/job/police.png");

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        setPlayerTexture(Minecraft.getMinecraft().player);
    }

    public static void setPlayerTexture(AbstractClientPlayer player) {
        try {
            ResourceLocation texture = getTexture(player);
            if (texture != null && player.getLocationSkin() != texture) {
                Field skinField = ReflectionHelper.findField(NetworkPlayerInfo.class, "playerTextures", "field_187107_a");
                skinField.setAccessible(true);
                Field playerInfoField = ReflectionHelper.findField(AbstractClientPlayer.class, "playerInfo", "field_175157_a");
                playerInfoField.setAccessible(true);
                NetworkPlayerInfo playerInfo = (NetworkPlayerInfo) playerInfoField.get(player);
                Map<MinecraftProfileTexture.Type, ResourceLocation> playerTextures = (Map<MinecraftProfileTexture.Type, ResourceLocation>) skinField.get(playerInfo);
                playerTextures.put(MinecraftProfileTexture.Type.SKIN, texture);
                skinField.set(playerInfo, playerTextures);
                playerInfoField.set(player, playerInfo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static ResourceLocation getTexture(AbstractClientPlayer player) throws IOException {
        if(player == null) return null;
        if(!ModEMT.isEMTClientCheck(player.getUniqueID())) { return player.getLocationSkin(); }
        if (playerTextures.containsKey(player.getUniqueID())) return playerTextures.get(player.getUniqueID());

        final ResourceLocation resourceLocation = new ResourceLocation(Minelife.MOD_ID, "textures/players/" + player.getUniqueID().toString() + ".png");
        TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();

        BufferedImage bimgEMTSkin = ImageIO.read(Minecraft.getMinecraft().getResourceManager().getResource(ModEMT.isEMT(player.getUniqueID()) ? emtSkin : policeSkin).getInputStream());
        BufferedImage bimgPlayerSkin = SkinHelper.getBufferedImage(player.getUniqueID());

        if (bimgPlayerSkin == null) return null;

        int[] rgb = bimgPlayerSkin.getRGB(0, 0, 64, 16, null, 0, 64);
        bimgEMTSkin.setRGB(0, 0, 64, 16, rgb, 0, 64);

        CustomTexture threadDownloadImageData = new CustomTexture(null, null, null, null);
        threadDownloadImageData.setBufferedImage(bimgEMTSkin);
        textureManager.loadTexture(resourceLocation, threadDownloadImageData);
        playerTextures.put(player.getUniqueID(), resourceLocation);
        return resourceLocation;
    }

    private static class CustomTexture extends ThreadDownloadImageData {

        public CustomTexture(File p_i1049_1_, String p_i1049_2_, ResourceLocation p_i1049_3_, IImageBuffer p_i1049_4_) {
            super(p_i1049_1_, p_i1049_2_, p_i1049_3_, p_i1049_4_);
        }

        @Override
        protected void loadTextureFromServer() {

        }
    }

}
