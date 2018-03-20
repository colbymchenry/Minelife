package com.minelife.realestate.server;

import com.google.common.collect.Maps;
import com.minelife.Minelife;
import com.minelife.realestate.network.PacketSelection;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Map;

public class SelectionListener {

    private static Map<EntityPlayer, BlockPos[]> SELECTIONS = Maps.newHashMap();

    @SubscribeEvent
    public void interactLeftClick(PlayerInteractEvent.LeftClickBlock event) {
        EntityPlayer player = event.getEntityPlayer();
        if (player.getHeldItem(player.getActiveHand()).getItem() == Items.GOLDEN_HOE) {
            event.setCanceled(true);
            BlockPos[] selection = new BlockPos[]{event.getPos(), null};
            SELECTIONS.put(player, selection);
            Minelife.getNetwork().sendTo(new PacketSelection(selection[0], selection[1]), (EntityPlayerMP) player);
            player.sendMessage(new TextComponentString(TextFormatting.LIGHT_PURPLE + "[RealEstate] " + TextFormatting.RED + "Position 1 " + TextFormatting.GOLD + "set!"));
            player.sendMessage(new TextComponentString(TextFormatting.LIGHT_PURPLE + "[RealEstate] " + TextFormatting.GOLD + "Right-click another block to set " + TextFormatting.RED + "Position 2" + TextFormatting.GOLD + "."));
        }
    }


    @SubscribeEvent
    public void interactRightClick(PlayerInteractEvent.RightClickBlock event) {
        EntityPlayer player = event.getEntityPlayer();
        if (player.getHeldItem(player.getActiveHand()).getItem() == Items.GOLDEN_HOE) {
            event.setCanceled(true);
            if (!SELECTIONS.containsKey(player)) {
                player.sendMessage(new TextComponentString(TextFormatting.LIGHT_PURPLE + "[RealEstate] " + TextFormatting.RED + "Please set Position 1 first by left-clicking a block."));
                return;
            }
            BlockPos[] selection = SELECTIONS.get(player);
            if(selection[1] == null)
                player.sendMessage(new TextComponentString(TextFormatting.LIGHT_PURPLE + "[RealEstate] " + TextFormatting.RED + "Position 1" + TextFormatting.GOLD + " and " + TextFormatting.RED + "Position 2" + TextFormatting.GOLD + " set!"));
            else
                player.sendMessage(new TextComponentString(TextFormatting.LIGHT_PURPLE + "[RealEstate] " + TextFormatting.RED + "Position 2" + TextFormatting.GOLD + " updated!"));
            selection[1] = event.getPos();
            SELECTIONS.put(player, selection);
            BlockPos pos1 = selection[0];
            BlockPos pos2 = selection[1];
            BlockPos min = new BlockPos(Math.min(pos1.getX(), pos2.getX()), Math.min(pos1.getY(), pos2.getY()), Math.min(pos1.getZ(), pos2.getZ()));
            BlockPos max = new BlockPos(Math.max(pos1.getX(), pos2.getX()), Math.max(pos1.getY(), pos2.getY()), Math.max(pos1.getZ(), pos2.getZ()));
            Minelife.getNetwork().sendTo(new PacketSelection(min, max), (EntityPlayerMP) player);
        }
    }

    public static boolean hasFullSelection(EntityPlayer player) {
        return SELECTIONS.containsKey(player) && SELECTIONS.get(player)[0] != null && SELECTIONS.get(player)[1] != null;
    }

    public static BlockPos[] getSelection(EntityPlayer player) {
        return SELECTIONS.get(player);
    }

    public static void removeSelection(EntityPlayer player) {
        SELECTIONS.remove(player);
    }

    public static BlockPos getMinimum(EntityPlayer player) {
        BlockPos[] selection = getSelection(player);
        if(selection.length < 2 || selection[0] == null || selection[1] == null) return null;
        BlockPos pos1 = selection[0], pos2 = selection[1];
        return new BlockPos(Math.min(pos1.getX(), pos2.getX()), Math.min(pos1.getY(), pos2.getY()), Math.min(pos1.getZ(), pos2.getZ()));
    }

    public static BlockPos getMaximum(EntityPlayer player) {
        BlockPos[] selection = getSelection(player);
        if(selection.length < 2 || selection[0] == null || selection[1] == null) return null;
        BlockPos pos1 = selection[0], pos2 = selection[1];
        return new BlockPos(Math.max(pos1.getX(), pos2.getX()), Math.max(pos1.getY(), pos2.getY()), Math.max(pos1.getZ(), pos2.getZ()));
    }

}
