package com.minelife.gangs;

import com.google.common.collect.Sets;
import com.minelife.MLMod;
import com.minelife.capes.network.PacketCreateCape;
import com.minelife.capes.network.PacketCreateGui;
import com.minelife.gangs.server.CommandGang;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.relauncher.Side;

import java.util.Set;
import java.util.UUID;

public class ModGangs extends MLMod {

    public static Set<Gang> cache_gangs = Sets.newTreeSet();

    @Override
    public void preInit(FMLPreInitializationEvent event) {
    }

    @Override
    public void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandGang());
    }

    public static Gang getGang(String name) {
        return cache_gangs.stream().filter(g -> g.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public static Gang getPlayerGang(UUID player) {
        for (Gang gang : cache_gangs) {
            if(gang.getLeader().equals(player)) return gang;
            if(gang.getMembers().stream().filter(member -> member.equals(player)).findFirst().orElse(null) != null) return gang;
        }
        return null;
    }

}
