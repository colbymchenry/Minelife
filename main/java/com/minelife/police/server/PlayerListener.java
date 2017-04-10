package com.minelife.police.server;

import com.minelife.Minelife;
import com.minelife.police.ItemHandcuff;
import com.minelife.police.packet.PacketArrestPlayer;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.WorldSettings;
import net.minecraftforge.event.entity.player.EntityInteractEvent;

public class PlayerListener {

    @SubscribeEvent
    public void onRightClick(EntityInteractEvent event) {
        if (!(event.target instanceof EntityPlayer)) return;

        boolean targetArrested = event.target.getEntityData().hasKey("arrested") ? event.target.getEntityData().getBoolean("arrested") : false;
        boolean holdingHandcuffs = event.entityPlayer.getHeldItem() != null && event.entityPlayer.getHeldItem().getItem() == ItemHandcuff.INSTANCE;
        boolean carryingPlayer = event.entityPlayer.ridingEntity != null;

        // drop target
        if(carryingPlayer && holdingHandcuffs) {
            event.target.mountEntity(null);
            return;
        }

        // uncuff target
        if(holdingHandcuffs && targetArrested) {
            ((EntityPlayer) event.target).removePotionEffect(Potion.moveSlowdown.id);
            ((EntityPlayer) event.target).removePotionEffect(Potion.jump.id);
            ((EntityPlayer) event.target).removePotionEffect(Potion.digSlowdown.id);
            ((EntityPlayer) event.target).setGameType(WorldSettings.GameType.SURVIVAL);
            event.target.getEntityData().setBoolean("arrested", false);
            return;
        }

        // cuff target
        if(holdingHandcuffs && !targetArrested) {
            ((EntityPlayer) event.target).addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, Integer.MAX_VALUE, 8, true));
            ((EntityPlayer) event.target).addPotionEffect(new PotionEffect(Potion.jump.id, Integer.MAX_VALUE, -8, true));
            ((EntityPlayer) event.target).addPotionEffect(new PotionEffect(Potion.digSlowdown.id, Integer.MAX_VALUE, 200, true));
            ((EntityPlayer) event.target).setGameType(WorldSettings.GameType.ADVENTURE);
            event.target.mountEntity(event.entityPlayer);
            event.target.getEntityData().setBoolean("arrested", true);
            return;
        }

        // pickup target
        if(!holdingHandcuffs && targetArrested) {
            event.target.mountEntity(event.entityPlayer);
        }
    }

}
