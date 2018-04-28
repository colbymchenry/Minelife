package com.minelife.essentials.server.commands;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.minelife.Minelife;
import com.minelife.essentials.ModEssentials;
import com.minelife.permission.ModPermission;
import com.minelife.util.*;
import com.minelife.util.configuration.InvalidConfigurationException;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

// TODO
public class Kit extends CommandBase {

    private static List<Cooldown> cooldowns = Lists.newArrayList();
    private static MLConfig kitConfig;

    public Kit() throws IOException, InvalidConfigurationException {
        kitConfig = new MLConfig("kits");
    }

    @Override
    public String getName() {
        return "kit";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/kit <kit-name>\r/kit - to view a list of kits\r/kit create <name>";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (!(sender instanceof EntityPlayerMP)) return;

        EntityPlayerMP player = (EntityPlayerMP) sender;
        Map<String, Integer> allowedKits = getAllowedKits(player);

        if (args.length == 0) {
            player.sendMessage(new TextComponentString(TextFormatting.GOLD + "----[" + TextFormatting.RED + "Kits" + TextFormatting.GOLD + "]----"));
            for (String s : kitConfig.getKeys(true)) {
                if (allowedKits.containsKey(s)) {
                    player.sendMessage(new TextComponentString(TextFormatting.GOLD + s + (allowedKits.get(s) > 0 ? ",Cooldown: " + allowedKits.get(s) : "")));
                }
            }
            return;
        }

        if (args[0].equalsIgnoreCase("create")) {
            if (!ModPermission.hasPermission(player.getUniqueID(), "createkit")) {
                player.sendMessage(new TextComponentString(TextFormatting.RED + "You do not have permission for that command."));
                return;
            }
            if (args.length != 2) {
                player.sendMessage(new TextComponentString(getUsage(sender)));
                return;
            }
            saveKit(player, args[1].toLowerCase());
            player.sendMessage(new TextComponentString(TextFormatting.GREEN + "Kit created!"));
            return;
        }

        if (getKit(args[0]) == null) {
            player.sendMessage(new TextComponentString(TextFormatting.RED + "Kit does not exist."));
            return;
        }

        Cooldown cooldown = cooldowns.stream().filter(c -> c.playerID.equals(player.getUniqueID()) && c.kit.equalsIgnoreCase(args[0])).findFirst().orElse(null);

        if (cooldown != null) {
            if (System.currentTimeMillis() > cooldown.duration) {
                cooldowns.remove(cooldown);
            } else {
                player.sendMessage(new TextComponentString(TextFormatting.RED + "You must wait " + ((cooldown.duration - System.currentTimeMillis()) / 60) + " seconds before using that kit again."));
                return;
            }
        }

        giveKit(player, args[0]);
        Minelife.getNetwork().sendTo(new PacketPlaySound("minecraft:entity.player.levelup", 1, 1), player);
        player.sendMessage(new TextComponentString(TextFormatting.GREEN + "Kit redeemed!"));
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return sender instanceof EntityPlayerMP && ModPermission.hasPermission(((EntityPlayerMP) sender).getUniqueID(), "kit");
    }

    public static void saveKit(EntityPlayerMP player, String name) {
        List<String> items = Lists.newArrayList();
        for (int i = 0; i < player.inventory.mainInventory.size(); i++) {
            ItemStack stack = player.inventory.getStackInSlot(i);
            if (stack != ItemStack.EMPTY) {
                items.add(ItemHelper.itemToString(stack));
            }
        }
        kitConfig.set(name.toLowerCase(), items);
        kitConfig.save();
    }

    public static List<ItemStack> getKit(String name) {
        List<ItemStack> items = Lists.newArrayList();
        if (!kitConfig.contains(name.toLowerCase())) return null;
        for (String s : kitConfig.getStringList(name.toLowerCase())) {
            items.add(ItemHelper.itemFromString(s));
        }
        return items;
    }

    public static void giveKit(EntityPlayerMP player, String kit) {
        List<ItemStack> kitItems = getKit(kit);
        if (kitItems == null) return;
        kitItems.forEach(itemStack -> {
            EntityItem entityItem = player.dropItem(itemStack, false);
            entityItem.setPickupDelay(0);
        });
        cooldowns.add(new Cooldown(player.getUniqueID(), kit, System.currentTimeMillis() + (getAllowedKits(player).containsKey(kit) ? getAllowedKits(player).get(kit) * 1000L : 0)));
    }

    public static int getCooldown(EntityPlayerMP player, String kit) {
        Cooldown cooldown = cooldowns.stream().filter(c -> c.playerID.equals(player.getUniqueID()) && c.kit.equalsIgnoreCase(kit)).findFirst().orElse(null);

        return cooldown != null ? (int) ((cooldown.duration - System.currentTimeMillis()) / 60) : 0;
    }

    public static Map<String, Integer> getAllowedKits(EntityPlayerMP player) {
        Map<String, Integer> allowedKits = Maps.newHashMap();
        for (String s : ModPermission.getPlayerPermissions(player.getUniqueID())) {
            if (s.startsWith("kit.")) {
                if (s.contains(".cooldown.")) {
                    if (NumberConversions.isInt(s.split("\\.")[3])) {
                        allowedKits.put(s.split("\\.")[1], NumberConversions.toInt(s.split("\\.")[3]));
                    } else {
                        allowedKits.put(s.split("\\.")[1], 0);
                    }
                } else {
                    allowedKits.put(s.split("\\.")[1], 0);
                }
            }
        }
        return allowedKits;
    }

    static class Cooldown {
        UUID playerID;
        String kit;
        long duration;

        public Cooldown(UUID playerID, String kit, long duration) {
            this.playerID = playerID;
            this.kit = kit;
            this.duration = duration;
        }
    }

}
