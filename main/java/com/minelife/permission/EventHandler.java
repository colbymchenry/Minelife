package com.minelife.permission;

import com.google.common.collect.Lists;
import com.minelife.gangs.Gang;
import com.minelife.gangs.ModGangs;
import com.minelife.gangs.server.CommandGang;
import com.minelife.police.ModPolice;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.server.FMLServerHandler;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class EventHandler {

    @SideOnly(Side.SERVER)
    @SubscribeEvent
    public void onChat(ServerChatEvent event) {
        event.setCanceled(true);

        if(ModPolice.isUnconscious(event.getPlayer())) {
            event.getPlayer().sendMessage(new TextComponentString(TextFormatting.RED + "You cannot chat while unconscious."));
            return;
        }

        UUID uuid = event.getPlayer().getUniqueID();
        String playerPrefix = ModPermission.getPrefix(uuid), playerSuffix = ModPermission.getSuffix(uuid);
        StringBuilder groupPrefix = new StringBuilder(), groupSuffix = new StringBuilder();
        ModPermission.getGroups(uuid).forEach(g -> {
            groupPrefix.append(ModPermission.getPrefix(g));
            groupSuffix.append(ModPermission.getSuffix(g));
        });
        String displayName = groupPrefix.toString() + playerPrefix + event.getUsername();
        String format = ModPermission.getConfig().getString("chat-format");
        format = format.replace("{DISPLAYNAME}", displayName);
        format = format.replace("&", String.valueOf('\u00a7'));
        format = format.replace("{MESSAGE}", groupSuffix + playerSuffix + event.getMessage());

        if(ModPermission.hasPermission(uuid, "chat.color")) format = format.replaceAll("&", String.valueOf('\u00a7'));
        event.setComponent(new TextComponentString(format));

        Gang g = Gang.getGangForPlayer(uuid);

        for (EntityPlayerMP p : FMLServerHandler.instance().getServer().getPlayerList().getPlayers()) {
            if(CommandGang.gangChatEnabled.contains(p.getUniqueID()) || CommandGang.gangChatEnabled.contains(uuid)) {
                if(Objects.equals(Gang.getGangForPlayer(p.getUniqueID()), g)) {
                    p.connection.sendPacket(new SPacketChat(event.getComponent(), ChatType.CHAT));
                }
            } else {
                p.connection.sendPacket(new SPacketChat(event.getComponent(), ChatType.CHAT));
            }
        }
    }

    static List<String> toIgnore = Lists.newArrayList();

    static {
        toIgnore.add("§6Update Available: §9[§r§6§2Dynamic Surroundings");
        toIgnore.add("Thank you for downloading MrCrayfish");
        toIgnore.add("Check out MrCrayfish");
        toIgnore.add("youtube.com/user/MrCrayfishMinecraft");
        toIgnore.add("BuildCraft 7.99.15");
        toIgnore.add("Check out the Community Edition for more Furniture!");
        toIgnore.add("mrcrayfish.com/furniture-comm-edition");
        toIgnore.add("Check out the Furniture Mod Wiki");
        toIgnore.add("mrcrayfishs-furniture-mod.wikia.com");
        toIgnore.add("Make sure you check out the wiki! http://mrcrayfishs-furniture-mod.wikia.com/");
        toIgnore.add("§eJourneyMap:§f Press");
        toIgnore.add("JourneyMap: Press");
        toIgnore.add("InvTweaks: Configuration loaded.");
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onChatReceive(ClientChatReceivedEvent event) {
        int k = MathHelper.floor((float) func_146233_a() / Minecraft.getMinecraft().gameSettings.chatScale);

        List<String> lines = Minecraft.getMinecraft().fontRenderer.listFormattedStringToWidth(event.getMessage().getFormattedText(), k);
        lines.forEach(line -> {
            for (String s : toIgnore) {
                if (line.contains(s)) {
                    event.setCanceled(true);
                    return;
                }
            }
        });
    }

    @SideOnly(Side.CLIENT)
    public static int func_146233_a() {
        short short1 = 320;
        byte b0 = 40;
        return MathHelper.floor(Minecraft.getMinecraft().gameSettings.chatWidth * (float) (short1 - b0) + (float) b0);
    }

}
