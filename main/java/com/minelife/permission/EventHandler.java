package com.minelife.permission;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.event.ServerChatEvent;

import java.util.List;
import java.util.UUID;

public class EventHandler {

    @SideOnly(Side.SERVER)
    @SubscribeEvent
    public void onChat(ServerChatEvent event) {
        UUID uuid = event.player.getUniqueID();
        String playerPrefix = ModPermission.getPrefix(uuid), playerSuffix = ModPermission.getSuffix(uuid);
        StringBuilder groupPrefix = new StringBuilder(), groupSuffix = new StringBuilder();
        ModPermission.getGroups(uuid).forEach(g -> {
            groupPrefix.append(ModPermission.getPrefix(g));
            groupSuffix.append(ModPermission.getSuffix(g));
        });
        String displayName = groupPrefix.toString() + playerPrefix + event.username;
        String format = ModPermission.getConfig().getString("chat-format");
        format = format.replaceAll("\\{DISPLAYNAME}", displayName);
        format = format.replaceAll("\\{MESSAGE}", groupSuffix + playerSuffix + event.message);
        format = format.replaceAll("&", String.valueOf('\u00a7'));
        ChatComponentText text = new ChatComponentText(format);
        event.setCanceled(true);
        event.player.getEntityWorld().playerEntities.forEach(player -> ((EntityPlayerMP) player).addChatComponentMessage(text));
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onChatReceive(ClientChatReceivedEvent event) {
        event.setCanceled(true);
        int k = MathHelper.floor_float((float) func_146233_a() / Minecraft.getMinecraft().gameSettings.chatScale);
        List<String> lines = Minecraft.getMinecraft().fontRenderer.listFormattedStringToWidth(event.message.getFormattedText(), k);
        lines.forEach(line -> Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText(line)));
    }

    @SideOnly(Side.CLIENT)
    public static int func_146233_a()
    {
        short short1 = 320;
        byte b0 = 40;
        return MathHelper.floor_float(Minecraft.getMinecraft().gameSettings.chatWidth * (float)(short1 - b0) + (float)b0);
    }

}
