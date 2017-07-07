package com.minelife.realestate;

import com.google.common.collect.Maps;
import com.minelife.Minelife;
import com.minelife.region.server.Region;
import com.minelife.util.client.GuiUtil;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.regions.CuboidRegion;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import org.lwjgl.input.Keyboard;

import java.util.Map;
import java.util.UUID;

public class ZoneInfoController {

    @SideOnly(Side.CLIENT)
    public static Zone clientZone;

    public static class PacketRespondZoneInfo implements IMessage {

        private Zone zone;

        public PacketRespondZoneInfo()
        {
        }

        public PacketRespondZoneInfo(Zone zone)
        {
            this.zone = zone;
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            zone.toBytes(buf);
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            zone = Zone.fromBytes(buf);
        }

        public static class Handler implements IMessageHandler<PacketRespondZoneInfo, IMessage> {

            @SideOnly(Side.CLIENT)
            public IMessage onMessage(PacketRespondZoneInfo message, MessageContext ctx)
            {
                ZoneInfoController.clientZone = message.zone;
                ZoneRenderer.pos1 = Vec3.createVectorHelper(
                        message.zone.getRegion().getBounds().minX,
                        message.zone.getRegion().getBounds().minY,
                        message.zone.getRegion().getBounds().minZ
                );
                ZoneRenderer.pos2 = Vec3.createVectorHelper(
                        message.zone.getRegion().getBounds().maxX,
                        message.zone.getRegion().getBounds().maxY,
                        message.zone.getRegion().getBounds().maxZ
                );
                return null;
            }
        }
    }

    public static class PacketRequestZoneInfo implements IMessage {

        public PacketRequestZoneInfo()
        {
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {

        }

        @Override
        public void toBytes(ByteBuf buf)
        {

        }

        public static class Handler implements IMessageHandler<PacketRequestZoneInfo, IMessage> {

            @SideOnly(Side.SERVER)
            public IMessage onMessage(PacketRequestZoneInfo message, MessageContext ctx)
            {
                EntityPlayerMP player = ctx.getServerHandler().playerEntity;
                Zone zone = Zone.getZone(player.getEntityWorld(), Vec3.createVectorHelper(player.posX, player.posY, player.posZ));

                if (zone != null) {
                    Minelife.NETWORK.sendTo(new PacketRespondZoneInfo(zone), player);
                } else {
                    player.addChatComponentMessage(new ChatComponentText("There is no zone here."));
                }

                return null;
            }
        }
    }

    public static class PacketUpdateZoneStatus implements IMessage {

        private boolean inZone;

        public PacketUpdateZoneStatus()
        {
        }

        public PacketUpdateZoneStatus(boolean inZone)
        {
            this.inZone = inZone;
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            inZone = buf.readBoolean();
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            buf.writeBoolean(inZone);
        }

        public static class Handler implements IMessageHandler<PacketUpdateZoneStatus, IMessage> {

            @SideOnly(Side.CLIENT)
            public IMessage onMessage(PacketUpdateZoneStatus message, MessageContext ctx)
            {
                ZoneRenderer.inZone = message.inZone;
                return null;
            }
        }
    }

    public static class PlayerTickListener {

        private static Map<UUID, Zone> inZone = Maps.newHashMap();

        @SubscribeEvent
        public void onPlayerTick(TickEvent.PlayerTickEvent event)
        {
            Zone zone = Zone.getZone(event.player.getEntityWorld(),
                    Vec3.createVectorHelper(event.player.posX, event.player.posY, event.player.posZ));

            if (inZone.containsKey(event.player.getUniqueID())) {
                if (zone == null) {
                    inZone.remove(event.player.getUniqueID());
                    Minelife.NETWORK.sendTo(new PacketUpdateZoneStatus(false), (EntityPlayerMP) event.player);
                }
            } else {
                if (zone != null) {
                    inZone.put(event.player.getUniqueID(), zone);
                    Minelife.NETWORK.sendTo(new PacketUpdateZoneStatus(true), (EntityPlayerMP) event.player);
                }
            }
        }

    }

    public static class ZoneRenderer {

        public static Vec3 pos1, pos2;
        public static boolean inZone = false;

        @SubscribeEvent
        public void renderOverlay(RenderGameOverlayEvent event)
        {
            if (event.type == RenderGameOverlayEvent.ElementType.CROSSHAIRS && inZone) {
                Minecraft.getMinecraft().fontRenderer.drawStringWithShadow("Press \"I\" to view zone info.", 2, 12, 0xFFFFFF);
            }
        }

        @SubscribeEvent
        public void render(RenderWorldLastEvent event)
        {
            ItemStack heldItem = Minecraft.getMinecraft().thePlayer.getHeldItem();

            if (heldItem == null || heldItem.getItem() != SelectionController.Selector.getInstance()) return;

            if (pos1 != null && pos2 != null) {
                com.sk89q.worldedit.regions.Region bounds = new CuboidRegion(new Vector(pos1.xCoord, pos1.yCoord, pos1.zCoord), new Vector(pos2.xCoord, pos2.yCoord, pos2.zCoord));

                int color = 0xf44242;
                GuiUtil.drawSquareInWorld(bounds.getMinimumPoint().getBlockX() + bounds.getWidth(), bounds.getMinimumPoint().getBlockY(), bounds.getMinimumPoint().getBlockZ(), bounds.getWidth(), bounds.getHeight(), 180f, event.partialTicks, color);
                GuiUtil.drawSquareInWorld(bounds.getMinimumPoint().getBlockX(), bounds.getMinimumPoint().getBlockY(), bounds.getMinimumPoint().getBlockZ(), bounds.getWidth(), bounds.getHeight(), 0f, event.partialTicks, color);

                GuiUtil.drawSquareInWorld(bounds.getMinimumPoint().getBlockX(), bounds.getMinimumPoint().getBlockY(), bounds.getMinimumPoint().getBlockZ() + bounds.getLength(), bounds.getLength(), bounds.getHeight(), 90f, event.partialTicks, color);
                GuiUtil.drawSquareInWorld(bounds.getMinimumPoint().getBlockX(), bounds.getMinimumPoint().getBlockY(), bounds.getMinimumPoint().getBlockZ(), bounds.getLength(), bounds.getHeight(), -90f, event.partialTicks, color);

                GuiUtil.drawSquareInWorld(bounds.getMinimumPoint().getBlockX(), bounds.getMinimumPoint().getBlockY(), bounds.getMinimumPoint().getBlockZ() + bounds.getLength(), bounds.getWidth(), bounds.getHeight(), 0f, event.partialTicks, color);
                GuiUtil.drawSquareInWorld(bounds.getMinimumPoint().getBlockX() + bounds.getWidth(), bounds.getMinimumPoint().getBlockY(), bounds.getMinimumPoint().getBlockZ() + bounds.getLength(), bounds.getWidth(), bounds.getHeight(), 180f, event.partialTicks, color);

                GuiUtil.drawSquareInWorld(bounds.getMinimumPoint().getBlockX() + bounds.getWidth(), bounds.getMinimumPoint().getBlockY(), bounds.getMinimumPoint().getBlockZ(), bounds.getLength(), bounds.getHeight(), -90f, event.partialTicks, color);
                GuiUtil.drawSquareInWorld(bounds.getMinimumPoint().getBlockX() + bounds.getWidth(), bounds.getMinimumPoint().getBlockY(), bounds.getMinimumPoint().getBlockZ() + bounds.getLength(), bounds.getLength(), bounds.getHeight(), 90f, event.partialTicks, color);
            }
        }
    }

    public static class KeyListener {
        private KeyBinding keyZoneInfo = new KeyBinding("key." + Minelife.MOD_ID + ".zone.info", Keyboard.KEY_R, Minelife.NAME);
    }

}
