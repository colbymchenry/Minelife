package com.minelife.realestate.server;

import com.google.common.collect.Maps;
import com.minelife.Minelife;
import com.minelife.realestate.ModRegion;
import com.minelife.util.client.render.LineRenderer;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import java.awt.*;
import java.util.Map;

public class SelectionHandler {

    @SideOnly(Side.SERVER)
    public static class Server {
        private static Map<EntityPlayer, Selection> selections = Maps.newHashMap();

        @SubscribeEvent
        public void onInteract(PlayerInteractEvent event) {
            EntityPlayerMP player = (EntityPlayerMP) event.entityPlayer;

            // Make sure the player is holding the selection tool
            if (player.getHeldItem() == null || player.getHeldItem().getItem() !=
                    Item.getItemById(ModRegion.getServerProxy().config.getInt("selection_tool"))) return;

            // Make sure the selection is not within two different worlds
            if(getSelection(player).world != event.world) {
                player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.RED + "You cannot make a selection within two worlds."));
                return;
            }

            // Left click = Pos1 | Right Click = Pos2
            switch (event.action) {
                case LEFT_CLICK_BLOCK: {
                    selections.put(player, getSelection(player).setPos1(event.x, event.y, event.z));
                    player.addChatComponentMessage(new ChatComponentText(String.format("Pos1: x=%1$s y=%2$s z=%3$s", event.x, event.y, event.z)));
                    break;
                }
                case RIGHT_CLICK_BLOCK: {
                    selections.put(player, getSelection(player).setPos2(event.x, event.y, event.z));
                    player.addChatComponentMessage(new ChatComponentText(String.format("Pos2: x=%1$s y=%2$s z=%3$s", event.x, event.y, event.z)));
                    break;
                }
                default:
                    return;
            }

            // Set the world after the selection point is made
            selections.put(player, getSelection(player).setWorld(event.world));
            event.setCanceled(true);

            // send the selection to the player
            if(getSelection(player).getMin() != null)
                Minelife.NETWORK.sendTo(new PacketSendSelection(getSelection(player)), player);
        }

        public static Selection getSelection(EntityPlayer player) {
            return selections.containsKey(player) ? selections.get(player) : new Selection().setWorld(player.getEntityWorld());
        }
    }

    @SideOnly(Side.CLIENT)
    public static class Client {
        public static Selection selection;
        private static Color selectionColor = new Color(255, 100, 100, 255);

        @SubscribeEvent
        public void renderSelection(RenderWorldLastEvent event) {
            if(selection == null || selection.getMin() == null) return;
            Vec3 min = selection.getMin(), max = selection.getMax();
            AxisAlignedBB bounds = AxisAlignedBB.getBoundingBox(min.xCoord, min.yCoord, min.zCoord, max.xCoord, max.yCoord, max.zCoord);
            LineRenderer.drawCuboidAroundsBlocks(Minecraft.getMinecraft(), bounds, event.partialTicks, selectionColor, false);
        }
    }

    public static class Selection {
        private Vec3 pos1, pos2;
        public World world;

        public Selection setPos1(int x, int y, int z) {
            pos1 = Vec3.createVectorHelper(x, y, z);
            return this;
        }

        public Selection setPos2(int x, int y, int z) {
            pos2 = Vec3.createVectorHelper(x, y, z);
            return this;
        }

        public Selection setWorld(World world) {
            this.world = world;
            return this;
        }

        public Vec3 getMin() {
            if(pos1 == null || pos2 == null) return null;
            return Vec3.createVectorHelper(Math.min(pos1.xCoord, pos2.xCoord), Math.min(pos1.yCoord, pos2.yCoord), Math.min(pos1.zCoord, pos2.zCoord));
        }

        public Vec3 getMax() {
            if(pos1 == null || pos2 == null) return null;
            return Vec3.createVectorHelper(Math.max(pos1.xCoord, pos2.xCoord), Math.max(pos1.yCoord, pos2.yCoord), Math.max(pos1.zCoord, pos2.zCoord));
        }
    }

}
