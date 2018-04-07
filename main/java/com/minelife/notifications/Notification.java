package com.minelife.notifications;

import com.minelife.Minelife;
import com.minelife.util.DateHelper;
import com.minelife.util.client.GuiHelper;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
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

    public Notification(UUID playerID, String message, NotificationType type, int duration, int bgColor) {
        this(playerID, message, null, type, duration, bgColor);
    }

    public Notification(ResultSet result) throws SQLException {
        this(UUID.fromString(result.getString("player")), result.getString("message"),
                new ResourceLocation(result.getString("icon")), NotificationType.values()[result.getInt("type")],
                result.getInt("duration"), result.getInt("bgColor"));
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

    public boolean hasIcon() {
        return !getIcon().toString().equalsIgnoreCase("minecraft:");
    }

    public void sendTo(EntityPlayerMP player, boolean playSound, boolean render, boolean save) {
        Minelife.getNetwork().sendTo(new PacketNotification(this, playSound, render, save), player);
    }

    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, getPlayerID().toString());
        ByteBufUtils.writeUTF8String(buf, getMessage());

        buf.writeBoolean(hasIcon());
        if (hasIcon())
            ByteBufUtils.writeUTF8String(buf, getIcon().toString());

        buf.writeInt(notificationType.ordinal());
        buf.writeInt(getDuration());
        buf.writeInt(getBgColor());
    }

    public static Notification fromBytes(ByteBuf buf) {
        UUID playerID = UUID.fromString(ByteBufUtils.readUTF8String(buf));
        String message = ByteBufUtils.readUTF8String(buf);
        ResourceLocation icon;
        if (buf.readBoolean()) icon = new ResourceLocation(ByteBufUtils.readUTF8String(buf));
        else icon = new ResourceLocation("");

        NotificationType type = NotificationType.values()[buf.readInt()];
        int duration = buf.readInt();
        int bgColor = buf.readInt();
        return new Notification(playerID, message, icon, type, duration, bgColor);
    }

    @SideOnly(Side.CLIENT)
    public int getHeight() {
        Minecraft mc = Minecraft.getMinecraft();
        int x = hasIcon() ? 24 : 8;
        List<String> lines = mc.fontRenderer.listFormattedStringToWidth(getMessage(), getNotificationType().width - x - 3);
        int height = (lines.size() * mc.fontRenderer.FONT_HEIGHT) + getNotificationType().topHeight + getNotificationType().bottomHeight;
        if(hasIcon() && height < 24) height = 33;
        return height;
    }

    public void save() throws SQLException {
        ResultSet result = ModNotifications.getDatabase().query("SELECT * FROM notifications " +
                "WHERE player='" + getPlayerID().toString() + "' AND message='" + getMessage().replace("'", "''") + "' " +
                "AND icon='" + getIcon().toString() + "' AND duration='" + getDuration() + "'");
        if (!result.next()) {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, 5);
            ModNotifications.getDatabase().query("INSERT INTO notifications (player, message, icon, type, duration, bgColor, datepublished) VALUES ('" + getPlayerID().toString() + "'," +
                    " '" + getMessage().replace("'", "''") + "', '" + getIcon().toString() + "', '" + getNotificationType().ordinal() + "', '" + getDuration() + "', '" + getBgColor() + "','" + DateHelper.dateToString(cal.getTime()) + "')");
        }
    }

    public void delete() throws SQLException {
        ModNotifications.getDatabase().query("DELETE FROM notifications " +
                "WHERE player='" + getPlayerID().toString() + "' AND message='" + getMessage().replace("'", "''") + "' " +
                "AND icon='" + getIcon().toString() + "' AND duration='" + getDuration() + "'");
    }

    @SideOnly(Side.CLIENT)
    public void drawNotification() {
        Minecraft mc = Minecraft.getMinecraft();
        Color color = new Color(getBgColor());

        GlStateManager.color(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, 1f);
        mc.getTextureManager().bindTexture(toastTex);
        Gui.drawModalRectWithCustomSizedTexture(0, 0, getNotificationType().x, getNotificationType().y,
                getNotificationType().width, getNotificationType().topHeight, 256, 256);

        int x = hasIcon() ? 24 : 8;
        int totalHeight = getHeight() - (notificationType.topHeight + notificationType.bottomHeight);
        if (hasIcon() && totalHeight < 21) totalHeight = 21;

        GlStateManager.color(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, 1f);
        mc.getTextureManager().bindTexture(toastTex);

        GlStateManager.pushMatrix();
        GlStateManager.translate(0, getNotificationType().topHeight, 0);
        GlStateManager.scale(1, totalHeight * 0.114, 1);
        Gui.drawModalRectWithCustomSizedTexture(0, 0, getNotificationType().x, getNotificationType().y + getNotificationType().topHeight,
                getNotificationType().width, mc.fontRenderer.FONT_HEIGHT, 256, 256);
        GlStateManager.popMatrix();

        if (hasIcon()) {
            GlStateManager.color(1, 1, 1, 1);
            GlStateManager.enableBlend();
            mc.getTextureManager().bindTexture(getIcon());
            GuiHelper.drawImage(4, 4, 16, 16, getIcon());
            GlStateManager.disableBlend();
        }

        GlStateManager.color(1, 1, 1, 1);
        mc.fontRenderer.drawSplitString(getMessage(), x, getNotificationType().topHeight + 1, getNotificationType().width - x - 3, 0xFFFFFF);

        GlStateManager.color(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, 1f);
        GlStateManager.disableLighting();
        mc.getTextureManager().bindTexture(toastTex);
        Gui.drawModalRectWithCustomSizedTexture(0, totalHeight + getNotificationType().topHeight, getNotificationType().x,
                getNotificationType().bottomY, getNotificationType().width, getNotificationType().bottomHeight, 256, 256);
    }

}
