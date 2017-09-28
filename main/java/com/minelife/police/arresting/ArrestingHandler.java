package com.minelife.police.arresting;

import com.minelife.Minelife;
import com.minelife.police.ModPolice;
import com.minelife.police.server.ServerProxy;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.Vec3;
import net.minecraft.world.WorldSettings;

import java.util.List;

public class ArrestingHandler {

    public static void arrestPlayer(EntityPlayer player) {
        player.getEntityData().setBoolean("arrested", true);
        player.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, Integer.MAX_VALUE, 8, true));
        player.addPotionEffect(new PotionEffect(Potion.jump.id, Integer.MAX_VALUE, -8, true));
        player.addPotionEffect(new PotionEffect(Potion.digSlowdown.id, Integer.MAX_VALUE, 200, true));
        player.setGameType(WorldSettings.GameType.ADVENTURE);

        if (!player.worldObj.isRemote) {
            player.worldObj.playSoundAtEntity(player, Minelife.MOD_ID + ":handcuff", 1.0F, player.worldObj.rand.nextFloat() * 0.1F + 0.9F);
        }
    }

    public static void freePlayer(EntityPlayer player) {
        player.getEntityData().setBoolean("arrested", false);
        player.removePotionEffect(Potion.moveSlowdown.id);
        player.removePotionEffect(Potion.jump.id);
        player.removePotionEffect(Potion.digSlowdown.id);
        player.setGameType(WorldSettings.GameType.SURVIVAL);
    }

    public static boolean isArrested(EntityPlayer player) {
        return player.getEntityData().hasKey("arrested") && player.getEntityData().getBoolean("arrested");
    }

    private int tick = 0;

    @SubscribeEvent
    public void onTick(TickEvent.PlayerTickEvent event) {
        tick++;

        // 20 ticks = 1 second
        if (tick > 19) {
            List<ServerProxy.TicketInfo> tickets = ModPolice.getServerProxy().getTickets(event.player.getUniqueID());
            boolean wasEmpty = tickets.isEmpty();
            for (ServerProxy.TicketInfo ticket : tickets) {
                if (ModPolice.getServerProxy().getPrisonRegion() != null &&
                        ModPolice.getServerProxy().getPrisonRegion().contains(event.player.worldObj.getWorldInfo().getWorldName(), event.player.posX, event.player.posY, event.player.posZ)) {
                    ticket.setTimeServed(ticket.timeServed + (1.0D / 60.0D));
                }

                if (ticket.timeServed >= ticket.getTimeToServe()) ticket.delete();
                else if (ticket.amountPayed >= ticket.getAmountToPay()) ticket.delete();
            }

            if (!wasEmpty && ModPolice.getServerProxy().getTickets(event.player.getUniqueID()).isEmpty()) {
                Vec3 tpVec = ModPolice.getServerProxy().getPrisonExit();
                event.player.mountEntity(null);
                ((EntityPlayerMP) event.player).playerNetServerHandler.setPlayerLocation(tpVec.xCoord, tpVec.yCoord, tpVec.zCoord, event.player.rotationYaw, event.player.rotationPitch);
            }
            tick = 0;
        }
    }

}
