package com.minelife.util.server;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class UUIDFetcher {

    protected static final Map<UUID, String> CACHE = Maps.newHashMap();
    protected static final List<String> NULL_NAMES = Lists.newArrayList();

    /*
    * 600 requests per 10 minutes for username -> UUID.
    * Up to 100 names in each request.
    */
    public static final UUID get(String player) {
        if (player == null || player.isEmpty()) return null;

        // search through the cached players first
        for (UUID id : CACHE.keySet()) if (CACHE.get(id) != null && CACHE.get(id).equalsIgnoreCase(player)) return id;

        if(NULL_NAMES.contains(player.toLowerCase())) return null;

        String profileURL = "https://api.mojang.com/users/profiles/minecraft/" + player.toLowerCase();

        try {
            JSONParser jsonParser = new JSONParser();
            HttpURLConnection connection = (HttpURLConnection) new URL(profileURL).openConnection();
            JSONObject response = (JSONObject) jsonParser.parse(new InputStreamReader(connection.getInputStream()));

            String name = (String) response.get("name");
            String uuid = (String) response.get("id");

            if(name == null) {
                NULL_NAMES.add(player.toLowerCase());
                return null;
            }

            // converts regionUniqueID from no dashes to having dashes
            uuid = uuid.replaceAll("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5");

            CACHE.put(UUID.fromString(uuid), name);
            return UUID.fromString(uuid);
        } catch (Exception ignored) {}

        NULL_NAMES.add(player.toLowerCase());
        return null;
    }

}