package com.minelife.util.client;

import codechicken.lib.texture.TextureUtils;
import com.google.common.collect.Sets;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.audio.SoundManager;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.GuiModList;
import net.minecraftforge.fml.client.GuiScrollingList;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

public class CustomGuiMainMenu extends GuiMainMenu {

    private static final ResourceLocation[] introSongs = new ResourceLocation[]{new ResourceLocation("minelife:intro0"), new ResourceLocation("minelife:intro1"), new ResourceLocation("minelife:intro2")};
    private static final ResourceLocation[] introImages = new ResourceLocation[]{
            new ResourceLocation("minelife:textures/gui/title/background/background0.png"),
            new ResourceLocation("minelife:textures/gui/title/background/background1.png"),
            new ResourceLocation("minelife:textures/gui/title/background/background2.png"),
            new ResourceLocation("minelife:textures/gui/title/background/background3.png"),
            new ResourceLocation("minelife:textures/gui/title/background/background4.png"),
            new ResourceLocation("minelife:textures/gui/title/background/background5.png")};
    private static final ResourceLocation minelifeLogo = new ResourceLocation("minelife:textures/gui/title/background/minelife_logo.png");
    private static final ResourceLocation javaDownload = new ResourceLocation("minelife:textures/gui/title/ram/javadownload.png");
    private static final ResourceLocation launcherHome = new ResourceLocation("minelife:textures/gui/title/ram/launcherhome.png");
    private static final ResourceLocation launcherSettings = new ResourceLocation("minelife:textures/gui/title/ram/launchersettings.png");
    private float scale = 1, fade = 255;
    private int image = 0;
    private int songLoop = -1;

    public CustomGuiMainMenu() {
        songLoop = new Random().nextInt(introSongs.length);
        try {
            OptionsInitiator.init();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
//        long total = Runtime.getRuntime().maxMemory();
//        if ((total / (1024 * 1024)) < 3000) {
//            mc.getTextureManager().bindTexture(javaDownload);
//            GuiHelper.drawImage(0, 0, this.width / 2, this.height / 2, javaDownload);
//            mc.getTextureManager().bindTexture(launcherHome);
//            GuiHelper.drawImage(this.width / 2, 0, this.width / 2, this.height / 2, launcherHome);
//            mc.getTextureManager().bindTexture(launcherSettings);
//            GuiHelper.drawImage(0, this.height / 2, this.width / 2, this.height / 2, launcherSettings);
//            drawCenteredString(fontRenderer, "You need to allocate more RAM!", (this.width / 2) + (this.width / 4), (this.height / 2) + (this.height / 4) - 20, 0xFFFFFF);
//            drawCenteredString(fontRenderer, "Allocate at least 3 GB", (this.width / 2) + (this.width / 4), (this.height / 2) + (this.height / 4), 0xFFFFFF);
//            int x = (this.width / 2) + (this.width / 4), y = (this.height / 2) + (this.height / 4) + 20;
//            int stringWidth = fontRenderer.getStringWidth("Click Here to download Java");
//            boolean hover = (mouseX >= x - (stringWidth / 2) && mouseX <= x + (stringWidth / 2) && mouseY >= y && mouseY <= y + 9);
//            drawCenteredString(fontRenderer, (hover ? TextFormatting.YELLOW : "") + "Click Here to download Java", x, y, 0xFFFFFF);
//        } else {
        int nextImage = image == introImages.length - 1 ? 0 : image + 1;
        mc.getTextureManager().bindTexture(introImages[nextImage]);
        GuiHelper.drawImage(0, 0, width, height, introImages[nextImage]);

        mc.getTextureManager().bindTexture(introImages[image]);
        GlStateManager.pushMatrix();
        GlStateManager.translate(0, 0, zLevel);
        GlStateManager.translate(width / 2, height / 2, 0);
        scale += 0.0005;

        if (scale >= 1.185) {
            GlStateManager.enableBlend();
            fade -= 15;
            GlStateManager.color(1, 1, 1, fade / 255F);
        }

        GlStateManager.scale(scale, scale, scale);
        GlStateManager.translate(-width / 2, -height / 2, 0);
        GuiHelper.drawImage(0, 0, width, height, introImages[image]);
        GlStateManager.popMatrix();

        if (scale >= 1.19) {
            scale = 1;
            fade = 255;
            image++;
            if (image > introImages.length - 1) image = 0;
        }

        int widthCopyright = this.fontRenderer.getStringWidth("Copyright Mojang AB. Do not distribute!");
        int widthCopyrightRest = this.width - widthCopyright - 2;
        this.drawString(this.fontRenderer, "Copyright Mojang AB. Do not distribute!", widthCopyrightRest, this.height - 10, -1);

        if (mouseX > widthCopyrightRest && mouseX < widthCopyrightRest + widthCopyright && mouseY > this.height - 10 && mouseY < this.height && Mouse.isInsideWindow()) {
            drawRect(widthCopyrightRest, this.height - 1, widthCopyrightRest + widthCopyright, this.height, -1);
        }

        GlStateManager.color(1, 1, 1, 1);
        mc.getTextureManager().bindTexture(minelifeLogo);
        GlStateManager.pushMatrix();
        GuiHelper.drawImage((this.width - 178) / 2, ((this.height - 45) / 2) - 60, 178, 45, minelifeLogo);
        GlStateManager.popMatrix();

        this.buttonList.forEach(btn -> btn.drawButton(mc, mouseX, mouseY, partialTicks));
//        }
    }

    @Override
    public void initGui() {
        try {
            OptionsInitiator.init();
        } catch (IOException e) {

        }
        super.initGui();
        GuiButton singlePlayerBtn = this.buttonList.stream().filter(btn -> btn.id == 1).findFirst().orElse(null);
        if (singlePlayerBtn != null) singlePlayerBtn.visible = false;
        GuiButton realmsBtn = this.buttonList.stream().filter(btn -> btn.id == 14).findFirst().orElse(null);
        if (realmsBtn != null) realmsBtn.visible = false;
        GuiButton multiplayerBtn = this.buttonList.stream().filter(btn -> btn.id == 2).findFirst().orElse(null);
        if (multiplayerBtn != null) {
            GuiButton modsBtn = this.buttonList.stream().filter(btn -> btn.id == 6).findFirst().orElse(null);
            modsBtn.width = multiplayerBtn.width;
        }
    }

    @Override
    public void updateScreen() {
        GuiButton singlePlayerBtn = this.buttonList.stream().filter(btn -> btn.id == 1).findFirst().orElse(null);
        if (singlePlayerBtn != null) singlePlayerBtn.visible = false;
        GuiButton realmsBtn = this.buttonList.stream().filter(btn -> btn.id == 14).findFirst().orElse(null);
        if (realmsBtn != null) realmsBtn.visible = false;
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
//        long total = Runtime.getRuntime().maxMemory();
//        if ((total / (1024 * 1024)) < 3000) {
//            int x = (this.width / 2) + (this.width / 4), y = (this.height / 2) + (this.height / 4) + 20;
//            int stringWidth = fontRenderer.getStringWidth("Click Here to download Java");
//            boolean hover = (mouseX >= x - (stringWidth / 2) && mouseX <= x + (stringWidth / 2) && mouseY >= y && mouseY <= y + 9);
//            if (hover) {
//                try {
//                    Class<?> oclass = Class.forName("java.awt.Desktop");
//                    Object object = oclass.getMethod("getDesktop").invoke((Object) null);
//                    oclass.getMethod("browse", URI.class).invoke(object, new URI("http://www.oracle.com/technetwork/java/javase/downloads/jre8-downloads-2133155.html"));
//                } catch (Throwable throwable1) {
//                }
//            }
//        } else {
        super.mouseClicked(mouseX, mouseY, mouseButton);
//        }
    }

    @SubscribeEvent
    public void onGui(GuiOpenEvent event) {
        if (event.getGui() != null && event.getGui().getClass() == GuiMainMenu.class)
            event.setGui(new CustomGuiMainMenu());
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.player == null && (mc.currentScreen instanceof CustomGuiMainMenu || mc.currentScreen instanceof GuiMultiplayer
                || mc.currentScreen instanceof GuiOptions || mc.currentScreen instanceof GuiLanguage || mc.currentScreen instanceof GuiModList)) {
            boolean foundMusic = false;
            SoundManager mng = ReflectionHelper.getPrivateValue(SoundHandler.class, mc.getSoundHandler(), "sndManager", "field_147694_f");
            Map playingSounds = ReflectionHelper.getPrivateValue(SoundManager.class, mng, "playingSounds", "field_148629_h");
            Iterator it = playingSounds.keySet().iterator();
            while (it.hasNext()) {
                PositionedSound ps = (PositionedSound) playingSounds.get(it.next());
                ResourceLocation reloc = ReflectionHelper.getPrivateValue(PositionedSound.class, ps, "positionedSoundLocation", "field_147664_a");
                if ("music.menu".equals(reloc.getResourcePath())) {
                    mc.getSoundHandler().stopSound(ps);
                    break;
                }
            }

            it = playingSounds.keySet().iterator();
            while (it.hasNext()) {
                PositionedSound ps = (PositionedSound) playingSounds.get(it.next());
                ResourceLocation reloc = ReflectionHelper.getPrivateValue(PositionedSound.class, ps, "positionedSoundLocation", "field_147664_a");
                for (ResourceLocation introSong : introSongs) {
                    if (reloc.getResourcePath().equals(introSong.getResourcePath())) {
                        foundMusic = true;
                        break;
                    }
                }
            }

            if (!foundMusic) {
                if (songLoop > introSongs.length - 1) songLoop = 0;
                mc.getSoundHandler().playSound(PositionedSoundRecord.getRecord(new SoundEvent(introSongs[songLoop]), 1, 0.03F));
                songLoop++;
            }
        }
    }

    @SubscribeEvent
    public void joinedServer(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        Minecraft.getMinecraft().getSoundHandler().stopSounds();
    }


}
