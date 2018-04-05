package com.minelife.notifications;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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

    public Notification(UUID playerID, String message, ResourceLocation icon, NotificationType type) {
        this.playerID = playerID;
        this.message = message;
        this.icon = icon == null ? new ResourceLocation("") : icon;
        this.notificationType = type;
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
    }

    public static Notification fromBytes(ByteBuf buf) {
        UUID playerID = UUID.fromString(ByteBufUtils.readUTF8String(buf));
        String message = ByteBufUtils.readUTF8String(buf);
        ResourceLocation icon = null;
        if(buf.readBoolean()) icon = new ResourceLocation(ByteBufUtils.readUTF8String(buf));
        else icon = new ResourceLocation("");

        NotificationType type = NotificationType.values()[buf.readInt()];
        return new Notification(playerID, message, icon, type);
    }

    public void save() throws SQLException {
        ResultSet result = ModNotifications.getDatabase().query("SELECT * FROM notifications " +
                "WHERE player='" + getPlayerID().toString() + "' AND message='" + getMessage().replace("'", "''") + "' " +
                "AND icon='" + getIcon().toString() + "'");
        if(!result.next()) {
            ModNotifications.getDatabase().query("INSERT INTO notifications (player, message, icon, type) VALUES ('" + getPlayerID().toString() + "'," +
                    " '" + getMessage() + "', '" + getIcon().toString() + "', '" + getNotificationType().ordinal() + "')");
        }
    }

    @SideOnly(Side.CLIENT)
    public void drawNotification() {
        Minecraft mc = Minecraft.getMinecraft();

        GlStateManager.color(1, 1, 1, 1);
        GlStateManager.disableLighting();
        mc.getTextureManager().bindTexture(toastTex);
        Gui.drawModalRectWithCustomSizedTexture(0, 0, getNotificationType().x, getNotificationType().y,
                getNotificationType().width, getNotificationType().topHeight, 256, 256);

        int x = getNotificationType() == NotificationType.IMPORTANT ? 16 : 8;
        List<String> lines = mc.fontRenderer.listFormattedStringToWidth(getMessage(), getNotificationType().width - x);
        int totalHeight = lines.size() * mc.fontRenderer.FONT_HEIGHT;

        for (int i = 0; i < totalHeight; i++) {
            Gui.drawModalRectWithCustomSizedTexture(0, getNotificationType().topHeight + i * mc.fontRenderer.FONT_HEIGHT,
                    getNotificationType().x, getNotificationType().y + getNotificationType().topHeight,
                    getNotificationType().width, mc.fontRenderer.FONT_HEIGHT, 256, 256);
        }

        mc.fontRenderer.drawSplitString(getMessage(), x, getNotificationType().topHeight, getNotificationType().width - x,
                getNotificationType().textColor);

        Gui.drawModalRectWithCustomSizedTexture(0, totalHeight + getNotificationType().topHeight, getNotificationType().x,
                getNotificationType().bottomY, getNotificationType().width, getNotificationType().bottomHeight, 256, 256);
    }

}
