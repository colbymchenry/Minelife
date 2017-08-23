package com.minelife.minebay;

import com.minelife.Minelife;
import com.minelife.notification.AbstractGuiNotification;
import com.minelife.notification.AbstractNotification;
import com.minelife.util.ItemUtil;
import com.minelife.util.NumberConversions;
import com.minelife.util.client.GuiUtil;
import com.minelife.util.client.render.MLItemRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.UUID;

public class SoldNotification extends AbstractNotification {

    private ItemStack item_stack;
    private double price;

    public SoldNotification() {}

    public SoldNotification(UUID player_uuid, ItemStack item_stack, double price) {
        super(player_uuid);
        this.item_stack = item_stack;
        this.price = price;
    }

    @Override
    public void writeToNBT(NBTTagCompound tagCompound) {
        tagCompound.setString("item_stack", ItemUtil.itemToString(item_stack));
        tagCompound.setDouble("price", price);
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        item_stack = ItemUtil.itemFromString(tagCompound.getString("item_stack"));
        price = tagCompound.getDouble("price");
    }

    @Override
    public Class<? extends AbstractGuiNotification> getGuiClass() {
        return GuiSoldNotification.class;
    }

    public static class GuiSoldNotification extends AbstractGuiNotification {

        private MLItemRenderer item_renderer;

        private SoldNotification notification;

        public GuiSoldNotification() {}

        public GuiSoldNotification(AbstractNotification notification) {
            super(notification);
            this.notification = (SoldNotification) notification;
            this.item_renderer = new MLItemRenderer(mc);
        }

        @Override
        protected void drawForeground() {
            mc.fontRenderer.drawString("Item Sold!", 24, 0, 0xFFFFFF);
            mc.fontRenderer.drawString("Amount: x" + notification.item_stack.stackSize, 0, 16, 0xFFFFFF);
            mc.fontRenderer.drawString("Cash: $" + NumberConversions.formatter.format(notification.price), 0, 32, 0xFFFFFF);
            GL11.glEnable(GL11.GL_LIGHTING);
            item_renderer.attempt_gl_reset();
            RenderHelper.enableGUIStandardItemLighting();
            item_renderer.drawItemStack(notification.item_stack, 0, 0);
            // TODO: Format and add sound effect
        }

        @Override
        protected void onClick(int mouseX, int mouseY) {

        }

        @Override
        protected int getHeight() {
            return 50;
        }
    }

}
