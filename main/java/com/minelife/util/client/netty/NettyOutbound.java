package com.minelife.util.client.netty;

import com.minelife.Minelife;

public class NettyOutbound {

    private int processID;
    private int stage;
    private String[] stages;

    public NettyOutbound(int processID) {
        this.processID = processID;
        stages = new String[64];
    }

    public void write(String text) {
        stages[stage] = text;
        stage++;
    }

    public boolean send() {
        if(Minelife.NETTY_CONNECTION == null || Minelife.NETTY_CONNECTION.getChannel() == null || !Minelife.NETTY_CONNECTION.getChannel().isActive()) {
            return false;
        }

        Minelife.NETTY_CONNECTION.getChannel().write("START:" + processID + "\n");
        Minelife.NETTY_CONNECTION.getChannel().flush();
        for (int i = 0; i < stages.length; i++) {
            if (stages[i] != null && !stages[i].isEmpty()) {
                Minelife.NETTY_CONNECTION.getChannel().write(stages[i] + "\n");
                Minelife.NETTY_CONNECTION.getChannel().flush();
            }
        }
        Minelife.NETTY_CONNECTION.getChannel().write("END:" + processID + "\n");
        Minelife.NETTY_CONNECTION.getChannel().flush();

        return true;
    }


}
