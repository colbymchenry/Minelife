package com.minelife.util.server;

import com.google.common.collect.Lists;
import com.minelife.util.client.PacketRequestName;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

import java.util.List;
import java.util.ListIterator;
import java.util.UUID;

public class FetchNameThread implements Runnable {

    private static final List<NameThreadObject> queList = Lists.newArrayList();
    private static final List<SimpleNameThreadObject> simpleQueList = Lists.newArrayList();
    public static FetchNameThread instance;

    @Override
    public void run() {
        System.out.println("**** NAME FETCHER THREAD STARTED! ****");
        while(true) {
            try {
                List<NameThreadObject> que = Lists.newArrayList();
                que.addAll(queList);
                ListIterator<NameThreadObject> iterator = que.listIterator();

                while (iterator.hasNext()) {
                    NameThreadObject object = iterator.next();
                    String name = NameFetcher.get(object.message.playerUUID);
                    object.callback.callback(name, object.message, object.ctx);
                }

                queList.removeAll(que);

                // --------------------------------------------------

                List<SimpleNameThreadObject> simpleQue = Lists.newArrayList();
                simpleQue.addAll(simpleQueList);
                ListIterator<SimpleNameThreadObject> simpleIterator = simpleQue.listIterator();

                while (simpleIterator.hasNext()) {
                    SimpleNameThreadObject object = simpleIterator.next();
                    String name = NameFetcher.get(object.uuid);
                    object.callback.callback(object.uuid, name, object.includes);
                }

                simpleQueList.removeAll(simpleQue);
            }catch(Exception e) {}
        }

    }

    public void fetchName(PacketRequestName message, MessageContext ctx, Callback callback) {
        NameThreadObject threadObject = new NameThreadObject();
        threadObject.message = message;
        threadObject.ctx = ctx;
        threadObject.callback = callback;
        queList.add(threadObject);
    }

    public void fetchName(UUID uuid, Callback callback, Object... includes) {
        SimpleNameThreadObject threadObject = new SimpleNameThreadObject();
        threadObject.uuid = uuid;
        threadObject.callback = callback;
        threadObject.includes = includes;
        simpleQueList.add(threadObject);
    }

    private class NameThreadObject {
        public PacketRequestName message;
        public MessageContext ctx;
        public Callback callback;
    }

    private class SimpleNameThreadObject {
        public UUID uuid;
        public Callback callback;
        public Object[] includes;
    }
}
