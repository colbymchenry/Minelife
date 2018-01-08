package com.minelife.util.client.netty;

import com.minelife.Minelife;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

public class ConnectionRetryHandler {

    private int tick = 0;
    private boolean active = false;

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {

        tick++;

        if(tick < 100) return;

        if(Minelife.NETTY_CONNECTION == null || Minelife.NETTY_CONNECTION.getChannel() == null) {
            Minelife.NETTY_CONNECTION = new ChatClient(Minelife.config.getString("netty_ip"), Minelife.config.getInt("netty_port"));
            Minelife.NETTY_CONNECTION.run();
            active = false;
        }

        if(Minelife.NETTY_CONNECTION.getChannel() != null) {
            if (!Minelife.NETTY_CONNECTION.getChannel().isActive()) {
                Minelife.NETTY_CONNECTION.run();
                active = false;
            } else {
                if (!active) {
                    Minelife.NETWORK.sendToAll(new PacketSendNettyServer(Minelife.config.getString("netty_ip"), Minelife.config.getInt("netty_port")));
                }
                active = true;
            }
        }

        tick = 0;
    }

}
