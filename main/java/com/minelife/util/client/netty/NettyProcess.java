package com.minelife.util.client.netty;

public class NettyProcess {

    private int handler_id;
    private String[] messages = new String[64];
    private int stage = 0;
    private boolean done = false;

    public NettyProcess(int handler_id) {
        this.handler_id = handler_id;
    }

    public int getHandlerID() {
        return handler_id;
    }

    public void add(String text) {
        messages[stage] = text;
        stage++;
    }

    public String getStage(int stage) {
        return messages[stage];
    }

    public void finish() {
        done = true;
    }

    public boolean isDone() {
        return done;
    }

}