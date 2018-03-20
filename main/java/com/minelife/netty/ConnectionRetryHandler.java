package com.minelife.netty;

import com.minelife.Minelife;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class ConnectionRetryHandler {

    private int tick = 0;
    private boolean active = false;

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        tick++;

        if(tick < 100) return;

        if(ModNetty.getNettyConnection() == null || ModNetty.getNettyConnection().getChannel() == null) {
            ModNetty.setNettyConnection(new ChatClient(ModNetty.getConfig().getString("netty_ip"), ModNetty.getConfig().getInt("netty_port")));
            ModNetty.getNettyConnection().run();
            active = false;
        }

        if(ModNetty.getNettyConnection().getChannel() != null) {
            if (!ModNetty.getNettyConnection().getChannel().isActive()) {
                ModNetty.getNettyConnection().run();
                active = false;
            } else {
                if (!active) {
                    Minelife.getNetwork().sendToAll(new PacketSendNettyServer(ModNetty.getConfig().getString("netty_ip"), ModNetty.getConfig().getInt("netty_port")));
                }
                active = true;
            }
        }

        tick = 0;
    }

}
