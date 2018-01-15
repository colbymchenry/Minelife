package com.minelife.economy.client;

import com.minelife.economy.ModEconomy;
import com.minelife.util.NumberConversions;
import com.minelife.util.client.GuiUtil;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import org.lwjgl.opengl.GL11;

import java.text.DecimalFormat;

import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;

public class OnScreenRenderer {

    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Pre event) {
        if (event.type == RenderGameOverlayEvent.ElementType.CROSSHAIRS) {
            GL11.glColor4f(1f, 1f, 1f, 1f);
            Minecraft.getMinecraft().fontRenderer.drawString("Wallet: " + EnumChatFormatting.GREEN + "$" + NumberConversions.formatter.format(ModEconomy.BALANCE_WALLET_CLIENT), 2, 2, 0xFFFFFF);
            GL11.glColor4f(1f, 1f, 1f, 1f);
        }
    }

}
