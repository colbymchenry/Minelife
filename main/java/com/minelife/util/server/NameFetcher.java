package com.minelife.util.server;

import com.minelife.Minelife;
import com.minelife.util.client.PacketRequestName;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

public final class NameFetcher  {

    public static String get(UUID id, NameUUIDCallback callback, Object... objects) {
        Runnable runnableTask = () -> {
            try {
                callback.callback(id, get(id), objects);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        MLCommand.pool.execute(runnableTask);

        return "Fetching...";
    }

    // TODO: This is called WAY more than it needs to be, make sure to make it only call once for the ID.
    public static String get(UUID id) {

        try {
            // Return cached name if it exists
            String cachedName = UUIDFetcher.CACHE.get(id);
            if (cachedName != null) return cachedName;
            if(id == null) return null;

            UUIDFetcher.CACHE.put(id, "Fetching...");

            String profileURL = "https://api.minetools.eu/uuid/" + id.toString().replaceAll("-", "");
            JSONParser jsonParser = new JSONParser();

            HttpURLConnection connection = (HttpURLConnection) new URL(profileURL).openConnection();
            JSONObject response = (JSONObject) jsonParser.parse(new InputStreamReader(connection.getInputStream()));

            UUIDFetcher.CACHE.put(id, (String) response.get("name"));
        } catch (Exception e) {
            UUIDFetcher.CACHE.put(id, null);
            e.printStackTrace();
        }

        return UUIDFetcher.CACHE.get(id);
    }

    public static String asyncFetchClient(UUID uuid) {
        if(UUIDFetcher.CACHE.containsKey(uuid)) return UUIDFetcher.CACHE.get(uuid);
        Minelife.getNetwork().sendToServer(new PacketRequestName(uuid));
        return "Fetching...";
    }

}