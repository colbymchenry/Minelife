package com.minelife.police.server;

import com.google.common.collect.Maps;
import com.minelife.economy.ModEconomy;
import com.minelife.police.ChargeType;
import com.minelife.police.Prisoner;
import com.minelife.police.cop.EntityCop;
import com.minelife.util.NumberConversions;
import com.minelife.util.PlayerHelper;
import com.minelife.util.StringHelper;
import com.minelife.util.server.MLCommand;
import com.minelife.util.server.UUIDFetcher;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import org.apache.commons.lang3.text.WordUtils;

import javax.xml.soap.Text;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.UUID;

public class CommandJail extends MLCommand {

    // TODO: Add paying bail
    @Override
    public String getName() {
        return "jail";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/jail bail <player>";
    }

    @Override
    public synchronized void runAsync(MinecraftServer server, ICommandSender sender, String[] args) throws Exception {
        EntityPlayer player = (EntityPlayer) sender;

        if (args.length > 0) {
            if (args[0].equalsIgnoreCase("bail")) {
                UUID playerID = args.length == 1 ? player.getUniqueID() : UUIDFetcher.get(args[1]);

                if (playerID == null) {
                    sender.sendMessage(new TextComponentString(TextFormatting.RED + "Player not found."));
                    return;
                }

                Prisoner prisoner = Prisoner.getPrisoner(playerID);

                if (prisoner == null) {
                    sender.sendMessage(new TextComponentString(TextFormatting.RED + "Player is not a prisoner."));
                    return;
                }

                if (!PlayerHelper.isOp((EntityPlayerMP) sender)) {
                    if (ModEconomy.getBalanceInventory((EntityPlayerMP) sender) < prisoner.getTotalBailAmount()) {
                        sender.sendMessage(new TextComponentString(TextFormatting.RED + "Insufficient funds. Total bail " + TextFormatting.DARK_RED + "$" + NumberConversions.format(prisoner.getTotalBailAmount())));
                        return;
                    }
                }

                // kick off cop if player is riding cop
                EntityCop cop = player.getRidingEntity() instanceof EntityCop ? (EntityCop) player.getRidingEntity() : null;
                if (cop != null) {
                    cop.getCarryingPlayer().dismountRidingEntity();
                    cop.setAttackTarget(null);
                    cop.setChasingPlayer(null);
                    cop.getNavigator().clearPath();
                }

                // if a prisoner take back to spawn and give items back
                if (PlayerHelper.getPlayer(playerID) != null) {
                    prisoner.freePrisoner(PlayerHelper.getPlayer(playerID));
                    PlayerHelper.getPlayer(playerID).sendMessage(new TextComponentString(TextFormatting.RED + "Your bail was paid."));
                } else
                    sender.sendMessage(new TextComponentString(TextFormatting.RED + "Player is not online."));

            } else sender.sendMessage(new TextComponentString(getUsage(sender)));
            return;
        }

        if (!Prisoner.isPrisoner(player.getUniqueID())) {
            player.sendMessage(new TextComponentString(TextFormatting.RED + "You are not a prisoner."));
            return;
        }

        Prisoner prisoner = Prisoner.getPrisoner(player.getUniqueID());

        player.sendMessage(new TextComponentString(StringHelper.ParseFormatting("&4----------------------------------", '&')));
        player.sendMessage(new TextComponentString(StringHelper.ParseFormatting("&6You are jailed for: &c" + ((prisoner.getTotalSentenceTime() / 60)) + " &6minutes.", '&')));
        player.sendMessage(new TextComponentString(StringHelper.ParseFormatting("&6Total for bail: &c$" + NumberConversions.format((prisoner.getTotalBailAmount())) + "&6.", '&')));

        int minutes = (int) (prisoner.getTimeServed() / 60);
        int leftOver = (int) (prisoner.getTimeServed() % 60.0D);
        player.sendMessage(new TextComponentString(StringHelper.ParseFormatting("&6Total time served: &c" + (minutes + "." + leftOver) + " &6minutes.", '&')));

        StringBuilder charges = new StringBuilder();
        Map<ChargeType, Integer> chargeCounts = Maps.newHashMap();

        prisoner.getCharges().forEach(charge -> chargeCounts.put(charge, chargeCounts.containsKey(charge) ? chargeCounts.get(charge) + 1 : 1));

        chargeCounts.forEach(((chargeType, integer) -> charges.append("&9").append(WordUtils.capitalizeFully(chargeType.name().replace("_", " "))).append(" &a(" + integer + ") ")));

        player.sendMessage(new TextComponentString(StringHelper.ParseFormatting("&6Charges: &9" + charges.toString(), '&')));
        player.sendMessage(new TextComponentString(StringHelper.ParseFormatting("&4----------------------------------", '&')));
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return sender instanceof EntityPlayer;
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
