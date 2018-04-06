package com.minelife.notifications;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class Notification {

    private static final ResourceLocation toastTex = new ResourceLocation("textures/gui/toasts.png");

    private UUID playerID;
    private String message;
    private ResourceLocation icon;
    private NotificationType notificationType;
    private int duration;
    private int bgColor;

    public Notification(UUID playerID, String message, ResourceLocation icon, NotificationType type, int duration, int bgColor) {
        this.playerID = playerID;
        this.message = message;
        this.icon = icon == null ? new ResourceLocation("") : icon;
        this.notificationType = type;
        this.duration = duration;
        this.bgColor = bgColor;
    }

    public Notification(UUID playerID, String message, ResourceLocation icon, NotificationType type, int bgColor) {
        this(playerID, message, icon, type, 5, bgColor);
    }

    public Notification(UUID playerID, String message, int bgColor) {
        this(playerID, message, null, NotificationType.WHITE, 5, bgColor);
    }

    public Notification(UUID playerID, String message, int duration, int bgColor) {
        this(playerID, message, null, NotificationType.WHITE, duration, bgColor);
    }

    public Notification(UUID playerID, String message, NotificationType type, int duration, int bgColor) {
        this(playerID, message, null, type, duration, bgColor);
    }

    public ResourceLocation getIcon() {
        return this.icon;
    }

    public String getMessage() {
        return message;
    }

    public UUID getPlayerID() {
        return playerID;
    }

    public int getDuration() {
        return duration;
    }

    public int getBgColor() {
        return bgColor;
    }

    public NotificationType getNotificationType() {
        return notificationType;
    }

    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, getPlayerID().toString());
        ByteBufUtils.writeUTF8String(buf, getMessage());

        buf.writeBoolean(getIcon() != null);
        if (getIcon() != null)
            ByteBufUtils.writeUTF8String(buf, getIcon().toString());

        buf.writeInt(notificationType.ordinal());
        buf.writeInt(getDuration());
        buf.writeInt(getBgColor());
    }

    public static Notification fromBytes(ByteBuf buf) {
        UUID playerID = UUID.fromString(ByteBufUtils.readUTF8String(buf));
        String message = ByteBufUtils.readUTF8String(buf);
        ResourceLocation icon = null;
        if(buf.readBoolean()) icon = new ResourceLocation(ByteBufUtils.readUTF8String(buf));
        else icon = new ResourceLocation("");

        NotificationType type = NotificationType.values()[buf.readInt()];
        int duration = buf.readInt();
        int bgColor = buf.readInt();
        return new Notification(playerID, message, icon, type, duration, bgColor);
    }

    // TODO: Implement icon

    @SideOnly(Side.CLIENT)
    public int getHeight() {
        Minecraft mc = Minecraft.getMinecraft();
        int x = 8;
        List<String> lines = mc.fontRenderer.listFormattedStringToWidth(getMessage(), getNotificationType().width - x);
        return (lines.size() * mc.fontRenderer.FONT_HEIGHT) + getNotificationType().topHeight + getNotificationType().bottomHeight;
    }

    public void save() throws SQLException {
        ResultSet result = ModNotifications.getDatabase().query("SELECT * FROM notifications " +
                "WHERE player='" + getPlayerID().toString() + "' AND message='" + getMessage().replace("'", "''") + "' " +
                "AND icon='" + getIcon().toString() + "' AND duration='" + getDuration() + "'");
        if(!result.next()) {
            ModNotifications.getDatabase().query("INSERT INTO notifications (player, message, icon, type, duration) VALUES ('" + getPlayerID().toString() + "'," +
                    " '" + getMessage() + "', '" + getIcon().toString() + "', '" + getNotificationType().ordinal() + "', '" + getDuration() + "')");
        }
    }

    @SideOnly(Side.CLIENT)
    public void drawNotification() {
        Minecraft mc = Minecraft.getMinecraft();
        Color color = new Color(getBgColor());

        GlStateManager.color(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, 1f);
        GlStateManager.disableLighting();
        mc.getTextureManager().bindTexture(toastTex);
        Gui.drawModalRectWithCustomSizedTexture(0, 0, getNotificationType().x, getNotificationType().y,
                getNotificationType().width, getNotificationType().topHeight, 256, 256);

        int x = 8;
        List<String> lines = mc.fontRenderer.listFormattedStringToWidth(getMessage(), getNotificationType().width - x);
        int totalHeight = lines.size() * mc.fontRenderer.FONT_HEIGHT;
        totalHeight = totalHeight < getNotificationType().middleHeight ? getNotificationType().middleHeight : totalHeight;

        GlStateManager.color(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, 1f);
        for (int i = 0; i < lines.size(); i++) {
            Gui.drawModalRectWithCustomSizedTexture(0, getNotificationType().topHeight + i * mc.fontRenderer.FONT_HEIGHT,
                    getNotificationType().x, getNotificationType().y + getNotificationType().topHeight,
                    getNotificationType().width, mc.fontRenderer.FONT_HEIGHT, 256, 256);
        }

        GlStateManager.color(1, 1, 1, 1);
        mc.fontRenderer.drawSplitString(getMessage(), x, getNotificationType().topHeight + 1, getNotificationType().width - x, 0xFFFFFF);

        GlStateManager.color(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, 1f);
        GlStateManager.disableLighting();
        mc.getTextureManager().bindTexture(toastTex);
        Gui.drawModalRectWithCustomSizedTexture(0, totalHeight + getNotificationType().topHeight, getNotificationType().x,
                getNotificationType().bottomY, getNotificationType().width, getNotificationType().bottomHeight, 256, 256);
        GlStateManager.enableLighting();
    }

}
