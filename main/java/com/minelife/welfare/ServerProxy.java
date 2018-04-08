package com.minelife.welfare;

import com.minelife.MLProxy;
import com.minelife.economy.ModEconomy;
import com.minelife.notifications.Notification;
import com.minelife.notifications.NotificationType;
import com.minelife.tracker.ModTracker;
import com.minelife.util.NumberConversions;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.server.FMLServerHandler;

import java.util.Calendar;

public class ServerProxy extends MLProxy {

    private static long payTime = 0L;

    @Override
    public void preInit(FMLPreInitializationEvent event) throws Exception {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if(payTime == 0L) payTime = System.currentTimeMillis() + ((1000L * 60) * 20);

        if (System.currentTimeMillis() >= payTime) {
            payTime += (1000L * 60) * 20;
            try {
                for (EntityPlayerMP entityPlayerMP : FMLServerHandler.instance().getServer().getPlayerList().getPlayers()) {
                    int payout = (ModTracker.getHoursPlayed(entityPlayerMP.getUniqueID()) * 60) + 60;
                    ModEconomy.depositATM(entityPlayerMP.getUniqueID(), payout, true);
                    Notification welfareNotification = new Notification(entityPlayerMP.getUniqueID(),
                            TextFormatting.DARK_GRAY + "Welfare: " + TextFormatting.DARK_GREEN + "$" + NumberConversions.format(payout),
                            NotificationType.EDGED, 5, 0xFFFFFF);
                    welfareNotification.sendTo(entityPlayerMP, true, true, false);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
