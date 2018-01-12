package com.minelife.essentials;

import com.minelife.notification.AbstractGuiNotification;
import com.minelife.notification.AbstractNotification;
import com.minelife.util.client.GuiUtil;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import java.util.Map;
import java.util.UUID;

public class NotificationPM extends AbstractNotification {

    private String msg;
    private GameProfile gameprofile;

    public NotificationPM() {
    }

    public NotificationPM(UUID playerUUID, String msg, GameProfile gameprofile) {
        super(playerUUID);
        this.msg = msg;
        this.gameprofile = gameprofile;
    }

    @Override
    public void writeToNBT(NBTTagCompound tagCompound) {
        tagCompound.setString("msg", msg);

        if (gameprofile != null) {
            NBTTagCompound nbttagcompound1 = new NBTTagCompound();
            NBTUtil.func_152460_a(nbttagcompound1, gameprofile);
            tagCompound.setTag("gameprofile", nbttagcompound1);
        }

    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        msg = tagCompound.getString("msg");
        if (tagCompound.hasKey("gameprofile", 10)) {
            this.gameprofile = NBTUtil.func_152459_a(tagCompound.getCompoundTag("gameprofile"));
        }
    }

    @Override
    public Class<? extends AbstractGuiNotification> getGuiClass() {
        return GuiNotificationPM.class;
    }

    public static class GuiNotificationPM extends AbstractGuiNotification {

        private NotificationPM notificationPM;
        private String message;

        public GuiNotificationPM() {

        }

        public GuiNotificationPM(AbstractNotification notification) {
            super(notification);
            this.notificationPM = (NotificationPM) notification;
            this.mc = Minecraft.getMinecraft();
            this.message = notificationPM.msg;
        }

        @Override
        protected void drawForeground() {
            ResourceLocation resourcelocation = AbstractClientPlayer.locationStevePng;
            GameProfile gameprofile = notificationPM.gameprofile;

            if (gameprofile != null) {

                Minecraft minecraft = Minecraft.getMinecraft();
                Map map = minecraft.func_152342_ad().func_152788_a(gameprofile);

                if (map.containsKey(MinecraftProfileTexture.Type.SKIN)) {
                    resourcelocation = minecraft.func_152342_ad().func_152792_a((MinecraftProfileTexture) map.get(MinecraftProfileTexture.Type.SKIN), MinecraftProfileTexture.Type.SKIN);
                }

                this.message = EnumChatFormatting.YELLOW + gameprofile.getName() + EnumChatFormatting.RESET + ": " + notificationPM.msg;
            }

            if (resourcelocation == null) resourcelocation = AbstractClientPlayer.locationStevePng;

            mc.getTextureManager().bindTexture(resourcelocation);

            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            GL11.glEnable(GL11.GL_ALPHA_TEST);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glColor4f(1, 1, 1, 1);


            GL11.glPushMatrix();
            {
                GL11.glTranslatef(-2, -18, zLevel);
                GL11.glTranslatef(16, 32, zLevel);
                GL11.glScalef(0.5f, 0.25f, 0.5f);
                GL11.glTranslatef(-16, -32, -zLevel);
                drawTexturedModalRect(0, 0, 32, 64, 32, 64);
                drawTexturedModalRect(0, 0, 160, 64, 32, 64);
            }
            GL11.glPopMatrix();

            mc.fontRenderer.drawString(EnumChatFormatting.YELLOW.toString() + EnumChatFormatting.ITALIC.toString() + (gameprofile == null ? "Player" : gameprofile.getName()), 28, 5, 0xFFFFFF);
            mc.fontRenderer.drawSplitString(notificationPM.msg, 28, 16, getWidth() - 32, 0xFFFFFF);
        }

        @Override
        protected void onClick(int mouseX, int mouseY) {

        }

        @Override
        protected int getHeight() {
            return 3 + (mc.fontRenderer.listFormattedStringToWidth(notificationPM.msg, getWidth() - 32).size() + 1) * mc.fontRenderer.FONT_HEIGHT;
        }

        @Override
        public String getSound() {
            return "text_message";
        }

    }

}
