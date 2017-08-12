package com.minelife.permission;

import com.minelife.CommonProxy;
import com.minelife.Minelife;
import com.minelife.util.JsonFile;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.IOException;

public class ServerProxy extends CommonProxy {

    static JsonFile JSON_FILE = new JsonFile(new File(Minelife.getConfigDirectory(), "permissions.json"));
    static JSONObject JSON_OBJECT = new JSONObject();

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        // create json file if it doesn't exist
        if (!JSON_FILE.file.exists()) try {
            JSON_FILE.file.createNewFile();
            // also write an empty json object to the file so it is populated
            JSON_FILE.write(JSON_OBJECT);
            return;
        } catch (IOException e) {
            e.printStackTrace();
        }

        createGroupJSONArray();
        createPlayerJSONArray();

        // setup the json object from the file
        JSON_OBJECT = JSON_FILE.read();
    }

    @Override
    public void init(FMLInitializationEvent event) {

    }

    void createPlayerJSONArray() {
        // if players array doesn't exist create it inside of the JSON
        if (ServerProxy.JSON_OBJECT.get("players") == null) {
            ServerProxy.JSON_OBJECT.put("players", new JSONArray());
            ServerProxy.JSON_FILE.write(ServerProxy.JSON_OBJECT);
        }
    }

    void createGroupJSONArray() {
        // if groups array doesn't exist create it inside of the JSON
        if (ServerProxy.JSON_OBJECT.get("groups") == null) {
            ServerProxy.JSON_OBJECT.put("groups", new JSONArray());
            ServerProxy.JSON_FILE.write(ServerProxy.JSON_OBJECT);
        }
    }

}
