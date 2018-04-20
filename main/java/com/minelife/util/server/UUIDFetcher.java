package com.minelife.util.server;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.minelife.Minelife;
import com.minelife.util.client.PacketRequestUUID;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class UUIDFetcher {

    protected static final ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);

    public static void get(String player, NameUUIDCallback callback, Object... objects) {
        pool.submit(() -> {
            UUID playerUUID = get(player);
            callback.callback(playerUUID, NameFetcher.get(playerUUID), objects);
        });
    }

    protected static final Map<UUID, String> CACHE = Maps.newHashMap();
    protected static final List<String> NULL_NAMES = Lists.newArrayList();

    private static final Set<String> fetching = Sets.newTreeSet();
    /*
    * 600 requests per 10 minutes for username -> UUID.
    * Up to 100 names in each request.
    */
    public static UUID get(String player) {
        if (player == null || player.isEmpty()) return null;

        // search through the cached players first
        for (UUID id : CACHE.keySet()) if (CACHE.get(id) != null && CACHE.get(id).equalsIgnoreCase(player)) return id;

        if (NULL_NAMES.contains(player.toLowerCase())) return null;

        if(fetching.contains(player)) return null;

        fetching.add(player);

        System.out.println("URRGGG");

        String profileURL = "https://api.minetools.eu/uuid/" + player.toLowerCase();

        try {
            JSONParser jsonParser = new JSONParser();
            HttpURLConnection connection = (HttpURLConnection) new URL(profileURL).openConnection();
            JSONObject response = (JSONObject) jsonParser.parse(new InputStreamReader(connection.getInputStream()));

            String name = (String) response.get("name");
            String uuid = (String) response.get("id");

            if (name == null) {
                NULL_NAMES.add(player.toLowerCase());
                return null;
            }

            // converts regionUniqueID from no dashes to having dashes
            uuid = uuid.replaceAll("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5");

            CACHE.put(UUID.fromString(uuid), name);
            return UUID.fromString(uuid);
        } catch (Exception ignored) {
//            ignored.printStackTrace();
        }

        NULL_NAMES.add(player.toLowerCase());
        return null;
    }

    @SubscribeEvent
    public void onJoin(PlayerEvent.PlayerLoggedInEvent event) {
        CACHE.put(event.player.getUniqueID(), event.player.getName());
    }

    public static UUID asyncFetchClient(String name) {
        if(CACHE.containsValue(name)) {
            for (UUID uuid : CACHE.keySet()) {
                if(CACHE.get(uuid) != null && CACHE.get(uuid).equals(name)) return uuid;
            }
        }
        Minelife.getNetwork().sendToServer(new PacketRequestUUID(name));
        return null;
    }

}