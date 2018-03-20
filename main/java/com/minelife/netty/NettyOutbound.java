package com.minelife.netty;

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
        if(ModNetty.getNettyConnection() == null || ModNetty.getNettyConnection().getChannel() == null || !ModNetty.getNettyConnection().getChannel().isActive()) {
            return false;
        }

        ModNetty.getNettyConnection().getChannel().write("START:" + processID + "\n");
        ModNetty.getNettyConnection().getChannel().flush();

        for (String stage1 : stages) {
            if (stage1 != null && !stage1.isEmpty()) {
                ModNetty.getNettyConnection().getChannel().write(stage1 + "\n");
                ModNetty.getNettyConnection().getChannel().flush();
            }
        }

        ModNetty.getNettyConnection().getChannel().write("END:" + processID + "\n");
        ModNetty.getNettyConnection().getChannel().flush();

        return true;
    }


}
