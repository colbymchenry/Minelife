package com.minelife.realestate.server;

import com.minelife.realestate.EnumPermission;
import com.minelife.realestate.Estate;
import com.minelife.region.server.Region;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Vec3;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;

public class PlayerListener {

    @SubscribeEvent
    public void onBreak(BlockEvent.BreakEvent event) {
        Region region = Region.getRegionAt(event.world, Vec3.createVectorHelper(event.x, event.y, event.z));
        if(region == null) return;
        Estate estate = Estate.estates.stream().filter(e -> e.getRegion().equals(region)).findFirst().orElse(null);
        if(estate == null) return;
        EntityPlayerMP player = (EntityPlayerMP) event.getPlayer();
        boolean hasPermission = estate.hasPermission(player.getUniqueID(), EnumPermission.BREAK);
        event.setCanceled(!hasPermission);
        if(!hasPermission) player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.RED + "You do not have permission to break here."));
    }

    @SubscribeEvent
    public void onPlace(BlockEvent.PlaceEvent event) {
        Region region = Region.getRegionAt(event.world, Vec3.createVectorHelper(event.x, event.y, event.z));
        if(region == null) return;
        Estate estate = Estate.estates.stream().filter(e -> e.getRegion().equals(region)).findFirst().orElse(null);
        if(estate == null) return;
        EntityPlayerMP player = (EntityPlayerMP) event.player;
        boolean hasPermission = estate.hasPermission(player.getUniqueID(), EnumPermission.PLACE);
        event.setCanceled(!hasPermission);
        if(!hasPermission) player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.RED + "You do not have permission to place here."));
    }

    @SubscribeEvent
    public void onInteract(PlayerInteractEvent event) {
//        Region region = Region.getRegionAt(event.world, Vec3.createVectorHelper(event.x, event.y, event.z));
//        if(region == null) return;
//        Estate estate = Estate.estates.stream().filter(e -> e.getRegion().equals(region)).findFirst().orElse(null);
//        if(estate == null) return;
//        EntityPlayerMP player = (EntityPlayerMP) event.entityPlayer;
//        event.setCanceled(estate.hasPermission(player.getUniqueID(), EnumPermission.INTERACT));
    }

}
