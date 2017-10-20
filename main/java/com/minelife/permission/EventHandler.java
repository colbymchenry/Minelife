package com.minelife.permission;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.event.ServerChatEvent;

public class EventHandler {

    @SubscribeEvent
    public void onChat(ServerChatEvent event) {
        String playerPrefix = ModPermission.getPrefix(event.player.getUniqueID());
        String playerSuffix = ModPermission.getSuffix(event.player.getUniqueID());
        StringBuilder groupSuffix = new StringBuilder();
        StringBuilder groupPrefix = new StringBuilder();
        ModPermission.getGroups(event.player.getUniqueID()).forEach(g -> groupPrefix.append(ModPermission.getPrefix(g)));
        ModPermission.getGroups(event.player.getUniqueID()).forEach(g -> groupSuffix.append(ModPermission.getSuffix(g)));
        event.setCanceled(true);
        event.player.getEntityWorld().playerEntities.forEach(p -> ((EntityPlayerMP) p).addChatComponentMessage(
                new ChatComponentText(
                        (groupPrefix != null && !groupPrefix.equals("null") ? groupPrefix.toString() : "") +
                        (playerPrefix != null ? playerPrefix : "") +
                        event.username + " > " +  (groupSuffix != null && !groupSuffix.equals("null") ? groupSuffix.toString() : "") +
                                (playerSuffix != null ? playerSuffix : "") + event.message)));
    }

}
