package com.minelife.realestate.server;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.minelife.realestate.Zone;
import com.minelife.realestate.ZonePermission;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Vec3;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class LandController {

    @SubscribeEvent
    public void onBreak(BlockEvent.BreakEvent event)
    {
        Zone zone = Zone.getZone(event.world, Vec3.createVectorHelper(event.x, event.y, event.z));

        if (zone != null) {
            if (!zone.canPlayer(ZonePermission.BREAK, event.getPlayer())) {
                event.setCanceled(true);
                event.getPlayer().addChatComponentMessage(new ChatComponentText(EnumChatFormatting.RED + "You are not allowed to break blocks here."));
            }
        }
    }

    @SubscribeEvent
    public void onPlace(BlockEvent.PlaceEvent event)
    {
        Zone zone = Zone.getZone(event.world, Vec3.createVectorHelper(event.x, event.y, event.z));

        if (zone != null) {
            if (!zone.canPlayer(ZonePermission.PLACE, event.player)) {
                event.setCanceled(true);
                event.player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.RED + "You are not allowed to place blocks here."));
            }
        }
    }

    @SubscribeEvent
    public void onInteract(PlayerInteractEvent event)
    {
        Zone zone = Zone.getZone(event.world, Vec3.createVectorHelper(event.x, event.y, event.z));

        List<Block> blacklist = Lists.newArrayList();
        blacklist.add(Blocks.iron_door);
        blacklist.add(Blocks.wooden_door);
        blacklist.add(Blocks.trapdoor);
        blacklist.add(Blocks.chest);
        blacklist.add(Blocks.anvil);
        blacklist.add(Blocks.dispenser);
        blacklist.add(Blocks.crafting_table);
        blacklist.add(Blocks.furnace);
        blacklist.add(Blocks.enchanting_table);
        blacklist.add(Blocks.ender_chest);
        blacklist.add(Blocks.beacon);
        blacklist.add(Blocks.dropper);
        blacklist.add(Blocks.hopper);
        blacklist.add(Blocks.cauldron);

        if (zone != null) {
            if (!zone.canPlayer(ZonePermission.INTERACT, event.entityPlayer)) {
                if (event.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) {
                    event.entityPlayer.addChatComponentMessage(new ChatComponentText(event.world.getBlock(event.x, event.y, event.z).getClass().getName()));
                    Block block = event.world.getBlock(event.x, event.y, event.z);
                    if (block.getClass().getName().contains("ic2") || block.getClass().getName().contains("buildcraft") || blacklist.contains(block)) {
                        event.setCanceled(true);
                        event.entityPlayer.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.RED + "You are not allowed to interact here."));
                    }
                }
            }
        }
    }

}
