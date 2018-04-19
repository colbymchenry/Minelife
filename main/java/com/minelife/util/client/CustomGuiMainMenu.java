package com.minelife.util.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.audio.SoundManager;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.fml.client.GuiModList;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import org.lwjgl.input.Mouse;

import java.util.Iterator;
import java.util.Map;
import java.util.Random;

public class CustomGuiMainMenu extends GuiMainMenu {

    private static final ResourceLocation[] introSongs = new ResourceLocation[] {new ResourceLocation("minelife:intro0"),new ResourceLocation("minelife:intro1"),new ResourceLocation("minelife:intro2")};
    private static final ResourceLocation[] introImages = new ResourceLocation[] {
            new ResourceLocation("minelife:textures/gui/title/background/background0.png"),
            new ResourceLocation("minelife:textures/gui/title/background/background1.png"),
            new ResourceLocation("minelife:textures/gui/title/background/background2.png"),
            new ResourceLocation("minelife:textures/gui/title/background/background3.png"),
            new ResourceLocation("minelife:textures/gui/title/background/background4.png"),
            new ResourceLocation("minelife:textures/gui/title/background/background5.png") };
    private static final ResourceLocation minelifeLogo = new ResourceLocation("minelife:textures/gui/title/background/minelife_logo.png");
    private float scale = 1, fade = 255;
    private int image = 0;
    private int songLoop = -1;

    public CustomGuiMainMenu() {
        songLoop = new Random().nextInt(introSongs.length);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawGradientRect(0, 0, this.width, this.height, -2130706433, 16777215);
        this.drawGradientRect(0, 0, this.width, this.height, 0, Integer.MIN_VALUE);

        int nextImage = image == introImages.length - 1 ? 0 : image + 1;
        mc.getTextureManager().bindTexture(introImages[nextImage]);
        GuiHelper.drawImage(0, 0, width, height, introImages[nextImage]);

        mc.getTextureManager().bindTexture(introImages[image]);
        GlStateManager.pushMatrix();
        GlStateManager.translate(0, 0, zLevel);
        GlStateManager.translate(width / 2, height / 2, 0);
        scale += 0.0005;

        if(scale >= 1.185) {
            GlStateManager.enableBlend();
            fade -= 15;
            GlStateManager.color(1, 1, 1, fade / 255F);
        }

        GlStateManager.scale(scale, scale, scale);
        GlStateManager.translate(-width / 2, -height / 2, 0);
        GuiHelper.drawImage(0, 0, width, height, introImages[image]);
        GlStateManager.popMatrix();

        if(scale >= 1.19) {
            scale = 1;
            fade = 255;
            image++;
            if(image > introImages.length - 1) image = 0;
        }

        int widthCopyright = this.fontRenderer.getStringWidth("Copyright Mojang AB. Do not distribute!");
        int widthCopyrightRest = this.width - widthCopyright - 2;
        this.drawString(this.fontRenderer, "Copyright Mojang AB. Do not distribute!", widthCopyrightRest, this.height - 10, -1);

        if (mouseX > widthCopyrightRest && mouseX < widthCopyrightRest + widthCopyright && mouseY > this.height - 10 && mouseY < this.height && Mouse.isInsideWindow())
        {
            drawRect(widthCopyrightRest, this.height - 1, widthCopyrightRest + widthCopyright, this.height, -1);
        }

        GlStateManager.color(1, 1, 1, 1);
        mc.getTextureManager().bindTexture(minelifeLogo);
        GlStateManager.pushMatrix();
        GuiHelper.drawImage((this.width - 178) / 2, ((this.height - 45) / 2) - 60, 178, 45, minelifeLogo);
        GlStateManager.popMatrix();

        this.buttonList.forEach(btn -> btn.drawButton(mc, mouseX, mouseY, partialTicks));
    }

    @Override
    public void initGui() {
        super.initGui();
        GuiButton singlePlayerBtn = this.buttonList.stream().filter(btn -> btn.id == 1).findFirst().orElse(null);
        if (singlePlayerBtn != null) singlePlayerBtn.visible = false;
        GuiButton realmsBtn = this.buttonList.stream().filter(btn -> btn.id == 14).findFirst().orElse(null);
        if (realmsBtn != null) realmsBtn.visible = false;
        GuiButton multiplayerBtn = this.buttonList.stream().filter(btn -> btn.id == 2).findFirst().orElse(null);
        if(multiplayerBtn != null) {
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

    @SubscribeEvent
    public void onGui(GuiOpenEvent event) {
        if (event.getGui() != null && event.getGui().getClass() == GuiMainMenu.class) event.setGui(new CustomGuiMainMenu());
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        if(mc.player == null && (mc.currentScreen instanceof CustomGuiMainMenu || mc.currentScreen instanceof GuiMultiplayer
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
                    if(reloc.getResourcePath().equals(introSong.getResourcePath())) {
                        foundMusic = true;
                        break;
                    }
                }
            }

            if(!foundMusic) {
                if(songLoop > introSongs.length - 1) songLoop = 0;
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
