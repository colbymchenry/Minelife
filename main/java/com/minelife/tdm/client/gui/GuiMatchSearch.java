package com.minelife.tdm.client.gui;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.minelife.Minelife;
import com.minelife.tdm.Match;
import com.minelife.util.client.GuiHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.IImageBuffer;
import net.minecraft.client.renderer.ThreadDownloadImageData;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Map;

public class GuiMatchSearch extends GuiScreen {

    private int guiLeft, guiTop, guiWidth = 220, guiHeight = 190;
    private Map<String, Match> arenas;
    private static Map<String, String> arenaPixels;

    public GuiMatchSearch(Map<String, Match> arenas, Map<String, String> arenaPixels) {
        this.arenas = arenas;
        this.arenaPixels = arenaPixels;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        GuiHelper.drawDefaultBackground(guiLeft, guiTop, guiWidth, guiHeight);

        // draw TDM
        GlStateManager.pushMatrix();
        int stringWidth = fontRenderer.getStringWidth(TextFormatting.BOLD + TextFormatting.ITALIC.toString() + "TeamDeathmatch");
        GlStateManager.translate((this.width - stringWidth) / 2, this.guiTop - 20, zLevel);
        GlStateManager.translate(stringWidth / 2, fontRenderer.FONT_HEIGHT / 2, 0);
        GlStateManager.scale(2, 2, 2);
        GlStateManager.translate(-stringWidth / 2, -fontRenderer.FONT_HEIGHT / 2, 0);
        fontRenderer.drawString(TextFormatting.BOLD + TextFormatting.ITALIC.toString() + "TeamDeathmatch", 0, 0, 0xef8228);
        GlStateManager.popMatrix();

        GlStateManager.color(1, 1, 1, 1);

        String hoveringArena = null;

        int column = 0, row = 0;
        for (int i = 0; i < arenas.size(); i++) {
            ResourceLocation arenaImage = getTexture((String) arenas.keySet().toArray()[i]);
            if (arenaImage != null) {
                mc.getTextureManager().bindTexture(getTexture((String) arenas.keySet().toArray()[i]));
                GuiHelper.drawImage(guiLeft + 5 + (column * 73), guiTop + 5 + (row * 94), 64, 64, arenaImage);
            }

            if(mouseX >= guiLeft + 5 + (column * 73) && mouseX <= guiLeft + 5 + (column * 73) + 64 &&
                    mouseY >=  guiTop + 5 + (row * 94) && mouseY <=  guiTop + 5 + (row * 94) + 64) {
                hoveringArena = (String) arenas.keySet().toArray()[i];
            }

            column++;
            if (i % 2 == 0 && i != 0) {
                column = 0;
                row++;
            }
        }

        super.drawScreen(mouseX, mouseY, partialTicks);

        if(hoveringArena != null && arenas.get(hoveringArena) != null) {
            Match match = arenas.get(hoveringArena);
            java.util.List<String> stringList = Lists.newArrayList();
            stringList.add(TextFormatting.RED + hoveringArena);

            // info for game that hasn't started
            if(System.currentTimeMillis() < match.getStartTime()) {
                stringList.add("Starting in " + ((System.currentTimeMillis() - match.getStartTime()) % 60) + " seconds");
            }
            stringList.add("Team 1: (" + match.getTeam1().size() + "/" + match.getTeam1MaxSize() + ")");
            stringList.add("Team 1: (" + match.getTeam1().size() + "/" + match.getTeam1MaxSize() + ")");
            stringList.add("Team 2: (" + match.getTeam2().size() + "/" + match.getTeam2MaxSize() + ")");
            stringList.add("Best Of: " + match.getRounds());

            // info for game that has already started
            if(System.currentTimeMillis() > match.getStartTime()) {
                stringList.add("Round: " + (match.getTeam1Wins() + match.getTeam2Wins()));
                stringList.add("Score Team 1: " + match.getTeam1Wins());
                stringList.add("Score Team 2: " + match.getTeam2Wins());
            }
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void initGui() {
        super.initGui();
        guiLeft = (this.width - guiWidth) / 2;
        guiTop = (this.height - guiHeight + 20) / 2;
        buttonList.clear();

        int column = 0, row = 0;
        for (int i = 0; i < arenas.size(); i++) {
            String arenaName = (String) arenas.keySet().toArray()[i];
            Match match = arenas.get(arenaName);
            buttonList.add(new GuiButton(i, guiLeft + 7 + (column * 73), guiTop + 72 + (row * 94), 60, 20, match == null ? "New Game" : "Join"));

            column++;
            if (i % 2 == 0 && i != 0) {
                column = 0;
                row++;
            }
        }

    }

    private static Map<String, ResourceLocation> CACHED_PIXELS = Maps.newHashMap();

    @SideOnly(Side.CLIENT)
    public static ResourceLocation getTexture(String arena) {
        String pixels = arenaPixels.get(arena);

        if (pixels == null) return null;

        if (CACHED_PIXELS.containsKey(arena)) return CACHED_PIXELS.get(arena);

        final ResourceLocation resourceLocation = new ResourceLocation(Minelife.MOD_ID, "textures/arenas/" + arena + ".png");
        TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();

        BufferedImage bi = new BufferedImage(128, 128, BufferedImage.TYPE_INT_ARGB);

        for (String pixel : pixels.split("\\;")) {
            if (pixel.contains(",")) {
                String[] data = pixel.split("\\,");
                int x = Integer.parseInt(data[0]);
                int y = Integer.parseInt(data[1]);
                int rgb = Integer.parseInt(data[2]);
                int red = (rgb >> 16) & 0x000000FF;
                int green = (rgb >> 8) & 0x000000FF;
                int blue = (rgb) & 0x000000FF;
                bi.setRGB(x, y, new Color(red, green, blue, 255).getRGB());
            }
        }

        CustomTexture threadDownloadImageData = new CustomTexture(null, null, null, null);
        threadDownloadImageData.setBufferedImage(bi);
        textureManager.loadTexture(resourceLocation, threadDownloadImageData);
        CACHED_PIXELS.put(arena, resourceLocation);
        return resourceLocation;
    }

    @SideOnly(Side.CLIENT)
    private static class CustomTexture extends ThreadDownloadImageData {

        public CustomTexture(File p_i1049_1_, String p_i1049_2_, ResourceLocation p_i1049_3_, IImageBuffer p_i1049_4_) {
            super(p_i1049_1_, p_i1049_2_, p_i1049_3_, p_i1049_4_);
        }

        @Override
        protected void loadTextureFromServer() {

        }
    }
}
