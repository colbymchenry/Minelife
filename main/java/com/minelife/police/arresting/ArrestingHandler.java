package com.minelife.police.arresting;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.WorldSettings;

public class ArrestingHandler {

    public static void arrestPlayer(EntityPlayer player) {
        player.getEntityData().setBoolean("arrested", true);
        player.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, Integer.MAX_VALUE, 8, true));
        player.addPotionEffect(new PotionEffect(Potion.jump.id, Integer.MAX_VALUE, -8, true));
        player.addPotionEffect(new PotionEffect(Potion.digSlowdown.id, Integer.MAX_VALUE, 200, true));
        player.setGameType(WorldSettings.GameType.ADVENTURE);
    }

    public static void freePlayer(EntityPlayer player) {
        player.getEntityData().setBoolean("arrested", false);
        player.removePotionEffect(Potion.moveSlowdown.id);
        player.removePotionEffect(Potion.jump.id);
        player.removePotionEffect(Potion.digSlowdown.id);
        player.setGameType(WorldSettings.GameType.SURVIVAL);
    }

    public static boolean isArrested(EntityPlayer player) {
        return !player.getEntityData().hasKey("arrested") ? false : player.getEntityData().getBoolean("arrested");
    }
}
