package com.minelife.economy.client.wallet;

import buildcraft.core.lib.inventory.SimpleInventory;
import com.minelife.util.client.GuiUtil;
import com.sk89q.worldedit.entity.Player;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class GuiWallet extends GuiContainer {

    private InventoryPlayer PlayerInventory;
    private InventoryWallet WalletInventory;
    private Color slotColor = new Color(139, 139, 139, 255);

    public GuiWallet(InventoryPlayer PlayerInventory, InventoryWallet WalletInventory) {
        super(new ContainerWallet(PlayerInventory, WalletInventory));
        this.PlayerInventory = PlayerInventory;
        this.WalletInventory = WalletInventory;
        this.xSize = 176;
        this.ySize = 166;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GuiUtil.drawDefaultBackground(this.guiLeft, this.guiTop, this.xSize, this.ySize);

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        for (Object s : this.inventorySlots.inventorySlots) {
            Slot slot = (Slot) s;
            GuiUtil.drawSlot(this.guiLeft + slot.xDisplayPosition - 1, this.guiTop + slot.yDisplayPosition - 1, 17, 17, slotColor);
        }
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glColor4f(1, 1, 1, 1);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        String s = "Wallet";
        this.fontRendererObj.drawString(s, 88 - this.fontRendererObj.getStringWidth(s) / 2, 6, 4210752);
        this.fontRendererObj.drawString(PlayerInventory.getInventoryName(), 8, 72, 4210752);
    }
}
