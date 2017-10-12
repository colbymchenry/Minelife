package com.minelife.realestate.server;

import com.google.common.collect.Maps;
import com.minelife.Minelife;
import com.minelife.realestate.ModRealEstate;
import com.minelife.realestate.Selection;
import com.minelife.realestate.network.PacketSendSelection;
import com.minelife.util.client.render.LineRenderer;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent;
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
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import java.awt.*;
import java.util.Map;

public class SelectionHandler {

    private static Map<EntityPlayer, Selection> selections = Maps.newHashMap();

    @SideOnly(Side.SERVER)
    @SubscribeEvent
    public void onInteract(PlayerInteractEvent event) {
        EntityPlayerMP player = (EntityPlayerMP) event.entityPlayer;

        // Make sure the player is holding the selection tool
        if (player.getHeldItem() == null || player.getHeldItem().getItem() !=
                Item.getItemById(ModRealEstate.getServerProxy().config.getInt("selection_tool"))) return;

        // Make sure the selection is not within two different worlds
        if (getSelection(player).getWorld() != event.world) {
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
        if (getSelection(player).getMin() != null)
            Minelife.NETWORK.sendTo(new PacketSendSelection(getSelection(player)), player);
    }

    @SideOnly(Side.SERVER)
    @SubscribeEvent
    public void onLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        selections.remove(event.player);
    }

    @SideOnly(Side.SERVER)
    @SubscribeEvent
    public void onLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        selections.remove(event.player);
    }

    public static Selection getSelection(EntityPlayer player) {
        return selections.containsKey(player) ? selections.get(player) : new Selection().setWorld(player.getEntityWorld());
    }

    public static Selection selection;
    private static Color selectionColor = new Color(255, 100, 100, 128);

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void renderSelection(RenderWorldLastEvent event) {
        if (selection == null || !selection.isComplete()) return;
        Vec3 min = selection.getMin(), max = selection.getMax();
        AxisAlignedBB bounds = AxisAlignedBB.getBoundingBox(min.xCoord, min.yCoord, min.zCoord, max.xCoord, max.yCoord, max.zCoord);
//        LineRenderer.drawCuboidAroundBlocks(Minecraft.getMinecraft(), block_vector, block_vector, event.partialTicks, whitelist.get(block), false);
        LineRenderer.drawCuboidAroundsBlocks(Minecraft.getMinecraft(), bounds, event.partialTicks, selectionColor, true, false);
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onLoggedOut(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        selection = null;
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onLoggedIn(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        selection = null;
    }



}
