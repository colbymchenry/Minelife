package com.minelife.minebay;

import com.minelife.notification.AbstractGuiNotification;
import com.minelife.notification.AbstractNotification;
import com.minelife.util.ItemHelper;
import com.minelife.util.NumberConversions;
import com.minelife.util.client.render.MLItemRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.lwjgl.opengl.GL11;

import java.util.UUID;

import static net.minecraft.util.EnumChatFormatting.*;

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
        tagCompound.setString("item_stack", ItemHelper.itemToString(item_stack));
        tagCompound.setDouble("price", price);
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        item_stack = ItemHelper.itemFromString(tagCompound.getString("item_stack"));
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
            mc.fontRenderer.drawString(YELLOW.toString() + UNDERLINE.toString() + BOLD.toString() + "Item Sold!", 28, 11, 0xFFFFFF);
            mc.fontRenderer.drawString(GREEN.toString() + BOLD.toString() + "Cash: +$" + NumberConversions.formatter.format(notification.price), 8, 28, 0xFFFFFF);
            GL11.glEnable(GL11.GL_LIGHTING);
            item_renderer.attempt_gl_reset();
            RenderHelper.enableGUIStandardItemLighting();
            item_renderer.drawItemStack(notification.item_stack, 6, 6);
        }

        @Override
        protected void onClick(int mouseX, int mouseY) {

        }

        @Override
        protected int getHeight() {
            return 35;
        }

        @Override
        public String getSound() {
            return "cha_ching";
        }
    }

}
