package com.minelife.util.server;

import com.google.common.collect.Lists;
import com.minelife.util.client.PacketRequestName;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

import java.util.List;
import java.util.ListIterator;

public class FetchNameThread implements Runnable {

    private static final List<NameThreadObject> queList = Lists.newArrayList();
    public static FetchNameThread instance;

    @Override
    public void run() {
        System.out.println("**** NAME FETCHER THREAD STARTED! ****");
        while(true) {
            List<NameThreadObject> que = Lists.newArrayList();
            que.addAll(queList);
            ListIterator<NameThreadObject> iterator = que.listIterator();

            while(iterator.hasNext()) {
                NameThreadObject object = iterator.next();
                String name = NameFetcher.get(object.message.playerUUID);
                object.callback.callback(name, object.message, object.ctx);
            }

            queList.removeAll(que);
        }

    }

    public void fetchName(PacketRequestName message, MessageContext ctx, Callback callback) {
        NameThreadObject threadObject = new NameThreadObject();
        threadObject.message = message;
        threadObject.ctx = ctx;
        threadObject.callback = callback;
        queList.add(threadObject);
    }

    private class NameThreadObject {
        public PacketRequestName message;
        public MessageContext ctx;
        public Callback callback;
    }
}
