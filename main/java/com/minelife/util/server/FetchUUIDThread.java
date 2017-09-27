package com.minelife.util.server;

import com.google.common.collect.Lists;
import com.minelife.util.client.PacketRequestUUID;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;

import java.util.List;
import java.util.ListIterator;
import java.util.UUID;

public class FetchUUIDThread implements Runnable {

    private static final List<UUIDThreadObject> queList = Lists.newArrayList();
    private static final List<SimpleUUIDThreadObject> simpleQueList = Lists.newArrayList();
    public static FetchUUIDThread instance;

    @Override
    public void run() {
        System.out.println("**** UUID FETCHER THREAD STARTED! ****");
        while(true) {
            try {
                List<UUIDThreadObject> que = Lists.newArrayList();
                que.addAll(queList);
                ListIterator<UUIDThreadObject> iterator = que.listIterator();

                while (iterator.hasNext()) {
                    UUIDThreadObject object = iterator.next();
                    UUID uuid = UUIDFetcher.get(object.message.playerName);
                    String name = NameFetcher.get(uuid);
                    object.callback.callback(uuid, name, object.message, object.ctx);
                }

                queList.removeAll(que);

                // ----------------------------------------

                List<SimpleUUIDThreadObject> simpleQue = Lists.newArrayList();
                simpleQue.addAll(simpleQueList);
                ListIterator<SimpleUUIDThreadObject> simpleIterator = simpleQue.listIterator();

                while (simpleIterator.hasNext()) {
                    SimpleUUIDThreadObject object = simpleIterator.next();
                    UUID uuid = UUIDFetcher.get(object.name);
                    String name = NameFetcher.get(uuid);
                    object.callback.callback(uuid, name, object.includes);
                }

                simpleQueList.removeAll(simpleQue);
            }catch(Exception e) {}
        }

    }

    public void fetchUUID(PacketRequestUUID message, MessageContext ctx, Callback callback) {
        UUIDThreadObject threadObject = new UUIDThreadObject();
        threadObject.message = message;
        threadObject.ctx = ctx;
        threadObject.callback = callback;
        queList.add(threadObject);
    }

    public void fetchUUID(String name, Callback callback, Object... includes) {
        SimpleUUIDThreadObject threadObject = new SimpleUUIDThreadObject();
        threadObject.name = name;
        threadObject.callback = callback;
        threadObject.includes = includes;
        simpleQueList.add(threadObject);
    }

    private class UUIDThreadObject {
        public PacketRequestUUID message;
        public MessageContext ctx;
        public Callback callback;
    }

    private class SimpleUUIDThreadObject {
        public String name;
        public Callback callback;
        public Object[] includes;
    }
}
