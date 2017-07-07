package com.minelife.permission;

import com.mojang.authlib.GameProfile;
import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.UserListEntry;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ListIterator;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

public class Player {

    UUID player;
    GameProfile gameProfile;

    protected Player(UUID uuid)
    {
        this.player = uuid;
        gameProfile = new GameProfile(player, null);
    }

    void setGroup(Group group)
    {
        JSONObject object = getJSONObject();

        // if the player is not in the json file, add them to it
        if (object == null)
        {
            insertPlayer();
            object = getJSONObject();
        }

        object.put("group", group.getName());
        ServerProxy.JSON_FILE.write(ServerProxy.JSON_OBJECT);
    }

    public boolean hasPermission(String permission)
    {
        Group g = null;
        try
        {
            g = getGroup();
        } catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }

        MinecraftServer mcServer = FMLCommonHandler.instance().getMinecraftServerInstance();

        UserListEntry userlistops = mcServer.getConfigurationManager().func_152603_m().func_152683_b(gameProfile);
        if (userlistops != null) return true;

        // the - + permission checks for permissions to exclude from inheritance
        if (g == null) return false;
        if (g.getPermissions().contains("-" + permission)) return false;
        if (getPermissions().contains("-" + permission)) return false;
        if (g.getPermissions().contains(permission)) return true;
        if (getPermissions().contains(permission)) return true;
        return false;
    }

    Set<String> getPermissions()
    {
        JSONObject object = getJSONObject();

        Set<String> permSet = new TreeSet<>();

        if (object != null && object.get("permissions") != null)
        {
            JSONArray permArray = (JSONArray) object.get("permissions");

            ListIterator permIterator = permArray.listIterator();

            while (permIterator.hasNext()) permSet.add(String.valueOf(permIterator.next()));
        }

        return permSet;
    }

    public Group getGroup() throws Exception
    {
        JSONObject object = getJSONObject();

        if (object != null && object.get("group") != null)
            return new Group(String.valueOf(object.get("group")));

        // if we didn't find a group for the player, look for the default group
        Group defaultGroup = Group.getGroups().stream().filter(g -> g.isDefaultGroup()).findFirst().orElse(null);

        return defaultGroup;
    }

    void addPermission(String permission)
    {
        JSONObject object = getJSONObject();

        if (object == null)
        {
            insertPlayer();
            object = getJSONObject();
        }

        if (object.get("permissions") != null)
            ((JSONArray) object.get("permissions")).add(permission.toLowerCase());

        ServerProxy.JSON_FILE.write(ServerProxy.JSON_OBJECT);
    }

    void removePermission(String permission)
    {
        JSONObject object = getJSONObject();

        if (object != null && object.get("permissions") != null)
            ((JSONArray) object.get("permissions")).remove(permission.toLowerCase());

        ServerProxy.JSON_FILE.write(ServerProxy.JSON_OBJECT);
    }

    JSONObject getJSONObject()
    {
        JSONArray jsonArray = (JSONArray) ServerProxy.JSON_OBJECT.get("players");

        if (jsonArray == null) return null;

        ListIterator listIterator = jsonArray.listIterator();

        while (listIterator.hasNext())
        {
            JSONObject object = (JSONObject) listIterator.next();
            try
            {
                if (object.get("regionUniqueID") != null && UUID.fromString(String.valueOf(object.get("regionUniqueID"))).equals(player))
                    return object;
            } catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        return null;
    }

    void insertPlayer()
    {
        JSONArray jsonArray = (JSONArray) ServerProxy.JSON_OBJECT.get("players");

        if (jsonArray == null)
        {
            // add players array and add player into it
            JSONArray newArray = new JSONArray();
            JSONObject playerObject = new JSONObject();
            playerObject.put("regionUniqueID", player.toString());
            newArray.add(playerObject);
            ServerProxy.JSON_OBJECT.put("players", newArray);
            ServerProxy.JSON_FILE.write(ServerProxy.JSON_OBJECT);
            return;
        }

        JSONObject playerObject = new JSONObject();
        playerObject.put("regionUniqueID", player.toString());
        jsonArray.add(playerObject);

        ServerProxy.JSON_FILE.write(ServerProxy.JSON_OBJECT);
    }

    String getPrefix()
    {
        JSONObject object = getJSONObject();

        if (object != null && object.get("prefix") != null)
            return String.valueOf(object.get("prefix")).replaceAll("&", String.valueOf('\u00a7'));

        return null;
    }

    String getSuffix()
    {
        JSONObject object = getJSONObject();

        if (object != null && object.get("suffix") != null)
            return String.valueOf(object.get("suffix")).replaceAll("&", String.valueOf('\u00a7'));

        return null;
    }
}
