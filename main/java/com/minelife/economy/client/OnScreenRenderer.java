package com.minelife.economy.client;

import com.minelife.economy.ModEconomy;
import com.minelife.util.NumberConversions;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import org.lwjgl.opengl.GL11;

import java.text.DecimalFormat;

public class OnScreenRenderer {

    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Pre event) {
        if(event.type == RenderGameOverlayEvent.ElementType.CROSSHAIRS) {
            GL11.glColor4f(1f, 1f, 1f, 1f);
            Minecraft.getMinecraft().fontRenderer.drawString("Wallet: " + EnumChatFormatting.GREEN + "$" + NumberConversions.formatter.format(ModEconomy.BALANCE_WALLET_CLIENT), 2, 2, 0xFFFFFF);
            GL11.glColor4f(1f, 1f, 1f, 1f);
        }
    }

}
