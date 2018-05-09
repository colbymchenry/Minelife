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

import java.util.Map;

public class CommandJail extends CommandBase {

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
        player.sendMessage(new TextComponentString(StringHelper.ParseFormatting("&6Total time served: &c$" + NumberConversions.format((prisoner.getTimeServed())) + " &6seconds.", '&')));

        StringBuilder charges = new StringBuilder();
        Map<ChargeType, Integer> chargeCounts = Maps.newHashMap();

        prisoner.getCharges().forEach(charge -> chargeCounts.put(charge, chargeCounts.containsKey(charge) ? chargeCounts.get(charge) + 1 : 1));

        chargeCounts.forEach(((chargeType, integer) -> charges.append(WordUtils.capitalizeFully(chargeType.name().replace("_", " "))).append("(" + integer + ") ")));

        player.sendMessage(new TextComponentString(StringHelper.ParseFormatting("&6Charges: &9$" + charges.toString() + " &6.", '&')));
        player.sendMessage(new TextComponentString(StringHelper.ParseFormatting("&4----------------------------------", '&')));
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return sender instanceof EntityPlayer;
    }
}
