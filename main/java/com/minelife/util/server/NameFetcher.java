package com.minelife.util.server;

import com.minelife.Minelife;
import com.minelife.util.client.INameReceiver;
import com.minelife.util.client.PacketRequestName;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.UUID;

public final class NameFetcher  {

    protected static final String get(UUID id) {
        try {
            // Return cached name if it exists
            String cachedName = UUIDFetcher.CACHE.get(id);
            if (cachedName != null) return cachedName;

            String profileURL = "https://sessionserver.mojang.com/session/minecraft/profile/" + id.toString().replaceAll("-", "");
            JSONParser jsonParser = new JSONParser();

            HttpURLConnection connection = (HttpURLConnection) new URL(profileURL).openConnection();
            JSONObject response = (JSONObject) jsonParser.parse(new InputStreamReader(connection.getInputStream()));

            UUIDFetcher.CACHE.put(id, (String) response.get("name"));
        } catch (Exception e) {
            UUIDFetcher.CACHE.put(id, null);
        }

        return UUIDFetcher.CACHE.get(id);
    }

    public static String asyncFetchServer(UUID uuid, Callback callback, Object... objects) {
        FetchNameThread.instance.fetchName(uuid, callback, objects);
        return "Fetching...";
    }

    public static String asyncFetchClient(UUID uuid, INameReceiver receiver) {
        Minelife.NETWORK.sendToServer(new PacketRequestName(uuid, receiver.getClass().getName()));
        return "Fetching...";
    }

}