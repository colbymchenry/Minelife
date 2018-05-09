package com.minelife.police.server;

import com.google.common.collect.Maps;
import com.minelife.police.ChargeType;
import com.minelife.police.Prisoner;
import com.minelife.util.NumberConversions;
import com.minelife.util.StringHelper;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import org.apache.commons.lang3.text.WordUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

public class CommandJail extends CommandBase {

    // TODO: Add paying bail
    @Override
    public String getName() {
        return "jail";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/jail";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        EntityPlayer player = (EntityPlayer) sender;

        if(!Prisoner.isPrisoner(player.getUniqueID())) {
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
