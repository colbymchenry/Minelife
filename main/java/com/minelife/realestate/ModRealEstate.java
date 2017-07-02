package com.minelife.realestate;

import com.minelife.CommonProxy;
import com.minelife.SubMod;
import com.minelife.realestate.client.packet.PacketBuyChunk;
import com.minelife.realestate.server.CommandClaim;
import com.minelife.realestate.server.PacketOpenGui;
import com.minelife.util.SimpleConfig;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import java.util.Map;

public class ModRealEstate extends SubMod {

    @SideOnly(Side.SERVER)
    public static SimpleConfig config;

    @Override
    public void preInit(FMLPreInitializationEvent event)
    {
        registerPacket(PacketOpenGui.Handler.class, PacketOpenGui.class, Side.CLIENT);
        registerPacket(PacketBuyChunk.Handler.class, PacketBuyChunk.class, Side.SERVER);
    }

    @Override
    public void serverStarting(FMLServerStartingEvent event)
    {
        event.registerServerCommand(new CommandClaim());
    }

    @Override
    public Class<? extends CommonProxy> getServerProxy()
    {
        return com.minelife.realestate.server.ServerProxy.class;
    }

    @SideOnly(Side.SERVER)
    public static int getPricePerChunk()
    {
        Map<String, Object> options = config.getOptions();

        if (!options.containsKey("PricePerChunk"))
        {
            options.put("PricePerChunk", 100);
            config.setOptions(options);
        }

        return Integer.parseInt((String) options.get("PricePerChunk"));
    }

    @SideOnly(Side.SERVER)
    public static String getMessage(String key)
    {
        Map<String, Object> options = config.getOptions();

        if (!options.containsKey(key))
        {
            return "Message not found.";
        }

        return (String) options.get(key);
    }
}
