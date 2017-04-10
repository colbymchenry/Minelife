package com.minelife.economy.client;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import org.lwjgl.opengl.GL11;

import java.text.DecimalFormat;

public class OnScreenRenderer {

    public static long WALLET = 0;

    private DecimalFormat formatter = new DecimalFormat("#,###");

    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Pre event) {
        if(event.type == RenderGameOverlayEvent.ElementType.CROSSHAIRS) {
            GL11.glColor4f(1f, 1f, 1f, 1f);
            Minecraft.getMinecraft().fontRenderer.drawString("Wallet: " + EnumChatFormatting.GREEN + "$" + formatter.format(WALLET), 2, 2, 0xFFFFFF);
            GL11.glColor4f(1f, 1f, 1f, 1f);
        }
    }

}
