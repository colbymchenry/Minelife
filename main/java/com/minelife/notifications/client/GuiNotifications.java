package com.minelife.notifications.client;

import com.google.common.collect.Lists;
import com.minelife.Minelife;
import com.minelife.notifications.ModNotifications;
import com.minelife.notifications.Notification;
import com.minelife.notifications.NotificationClickEvent;
import com.minelife.notifications.NotificationType;
import com.minelife.util.client.GuiHelper;
import com.minelife.util.client.GuiScrollableContent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class GuiNotifications extends GuiScreen {

    private static List<Notification> notifications = Lists.newArrayList();
    private Content content;

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();
        if(notifications.isEmpty()) {
            String msg = "You have no notifications!";
            GlStateManager.pushMatrix();
            GlStateManager.translate((this.width - this.fontRenderer.getStringWidth(msg)) / 2,
                    (this.height - fontRenderer.FONT_HEIGHT) / 2, 0);
            GlStateManager.translate(this.fontRenderer.getStringWidth(msg) / 2, fontRenderer.FONT_HEIGHT / 2, 0);
            GlStateManager.scale(2, 2, 2);
            GlStateManager.translate(-(this.fontRenderer.getStringWidth(msg) / 2), -(fontRenderer.FONT_HEIGHT / 2), 0);
            fontRenderer.drawString(msg, 0, 0, 0xFFFFFF);
            GlStateManager.popMatrix();
        } else {
            content.draw(mouseX, mouseY, Mouse.getDWheel());
        }
    }

    @Override
    public void initGui() {
        super.initGui();
        notifications.clear();
        try {
            ResultSet result = ModNotifications.getDatabase().query("SELECT * FROM notifications");
            while(result.next()) notifications.add(new Notification(result));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        content = new Content(mc, (this.width - NotificationType.WHITE.width + 6) / 2, (this.height - 186) / 2, NotificationType.WHITE.width + 6, 186);
    }

    static class Content extends GuiScrollableContent {

        private static ResourceLocation xTex = new ResourceLocation(Minelife.MOD_ID, "textures/gui/x.png");

        public Content(Minecraft mc, int x, int y, int width, int height) {
            super(mc, x, y, width, height);
        }

        @Override
        public int getObjectHeight(int index) {
            return index < notifications.size() ? notifications.get(index).getHeight() : 0;
        }

        @Override
        public void drawObject(int index, int mouseX, int mouseY, boolean isHovering) {
            if(index >= notifications.size()) return;

            GlStateManager.color(1, 1, 1, 1);
            notifications.get(index).drawNotification();

            mc.getTextureManager().bindTexture(xTex);
            GuiHelper.drawImage(3, 3, 8, 8, xTex);

        }

        @Override
        public int getSize() {
            return notifications.size();
        }

        @Override
        public void elementClicked(int index, int mouseX, int mouseY, boolean doubleClick) {
            if(index >= notifications.size()) return;

            if(mouseX >= 3 && mouseX <= 11 && mouseY >= 3 && mouseY <= 11) {
                try {
                    notifications.get(index).delete();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                notifications.remove(index);
                return;
            }
            if(doubleClick) {
                NotificationClickEvent clickEvent = new NotificationClickEvent(notifications.get(index));
                MinecraftForge.EVENT_BUS.post(clickEvent);
            }
        }

        @Override
        public void drawBackground() {
        }

        @Override
        public void drawSelectionBox(int index, int width, int height) {
        }
    }

}
