package com.minelife.cape.server;

import com.minelife.Minelife;
import com.minelife.cape.ModCapes;
import com.minelife.cape.network.PacketUpdateCape;
import com.minelife.cape.network.PacketUpdateCapeStatus;
import com.minelife.permission.ModPermission;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

public class CommandCape extends CommandBase {

    @Override
    public String getName() {
        return "cape";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        sendMessage(sender, "/cape on");
        sendMessage(sender, "/cape off");
        return null;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (!(sender instanceof EntityPlayerMP)) {
            sendMessage(sender, TextFormatting.RED + "Only players can use capes.");
            return;
        }

        if (args.length != 1) {
            getUsage(sender);
            return;
        }

        EntityPlayerMP player = (EntityPlayerMP) sender;

        boolean holdingCape = player.getHeldItemMainhand().getItem() == ModCapes.itemCape;

//        if(!ModNetty.hasConnection()) {
//            sendMessage(sender, TextFormatting.RED + "Cape servers are down. Try again later.");
//            return;
//        }

        if (args[0].equalsIgnoreCase("on")) {
            if (!holdingCape) {
                sendMessage(sender, TextFormatting.RED + "You must be holding a cape.");
                return;
            }

            if (ModCapes.itemCape.getPixels(player.getHeldItemMainhand()) == null) {
                sendMessage(sender, TextFormatting.RED + "This cape is blank. Right-Click with it to edit it.");
                return;
            }

            if (player.getEntityData().hasKey("Cape") && player.getEntityData().hasKey("CapePixels")) {
                ItemStack capeStack = new ItemStack(ModCapes.itemCape);
                ModCapes.itemCape.setUniqueID(capeStack);
                ModCapes.itemCape.setPixels(capeStack, ModCapes.itemCape.getPixels(player));
                EntityItem entityCapeItem = player.dropItem(capeStack, true);
                entityCapeItem.setPickupDelay(0);
            }

            setCape(player, ModCapes.itemCape.getPixels(player.getHeldItemMainhand()));
            player.setHeldItem(EnumHand.MAIN_HAND, ItemStack.EMPTY);

            Minelife.getNetwork().sendToAll(new PacketUpdateCape(player.getUniqueID(), player.getEntityId(), ModCapes.itemCape.getPixels(player)));
            Minelife.getNetwork().sendToAll(new PacketUpdateCapeStatus(player.getEntityId(), true));
        } else if (args[0].equalsIgnoreCase("off")) {
            if (player.getEntityData().hasKey("Cape") && player.getEntityData().hasKey("CapePixels")) {
                ItemStack capeStack = new ItemStack(ModCapes.itemCape);
                ModCapes.itemCape.setUniqueID(capeStack);
                ModCapes.itemCape.setPixels(capeStack, ModCapes.itemCape.getPixels(player));
                EntityItem entityCapeItem = player.dropItem(capeStack, true);
                entityCapeItem.setPickupDelay(0);
                setCape(player, null);

                Minelife.getNetwork().sendToAll(new PacketUpdateCapeStatus(player.getEntityId(), false));
            } else {
                sendMessage(sender, TextFormatting.RED + "You are not wearing a cape.");
            }
        } else {
            getUsage(sender);
        }
    }

    public static void sendMessage(ICommandSender sender, String msg) {
        sender.sendMessage(new TextComponentString(TextFormatting.LIGHT_PURPLE + "[Cape] " + TextFormatting.GOLD + msg));
    }

    public static void setCape(EntityPlayerMP player, String pixels) {
        if (pixels == null) {
//            NettyOutbound outbound = new NettyOutbound(1);
//            outbound.write(player.getUniqueID().toString());
//            outbound.send();
            player.getEntityData().removeTag("Cape");
            player.writeEntityToNBT(player.getEntityData());
            ModCapes.itemCape.setPixels(player, null);
        } else {
//            NettyOutbound outbound = new NettyOutbound(0);
//            outbound.write(player.getUniqueID().toString());
//            outbound.write(ModCapes.itemCape.getPixels(capeStack));
//            outbound.send();
            player.getEntityData().setBoolean("Cape", true);
            player.writeEntityToNBT(player.getEntityData());
            ModCapes.itemCape.setPixels(player, pixels);
        }
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return true;
    }
}
