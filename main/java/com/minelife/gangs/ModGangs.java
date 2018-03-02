package com.minelife.gangs;

import com.google.common.collect.Sets;
import com.minelife.MLMod;
import com.minelife.MLProxy;
import com.minelife.capes.network.PacketCreateCape;
import com.minelife.capes.network.PacketCreateGui;
import com.minelife.gangs.network.*;
import com.minelife.gangs.server.CommandGang;
import com.minelife.realestate.Estate;
import com.minelife.realestate.EstateHandler;
import com.minelife.realestate.Permission;
import com.minelife.util.Location;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import java.util.Set;
import java.util.UUID;

public class ModGangs extends MLMod {

    public static Set<Gang> cache_gangs = Sets.newTreeSet();

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        registerPacket(PacketOpenGangGui.Handler.class, PacketOpenGangGui.class, Side.CLIENT);
        registerPacket(PacketInviteToGang.Handler.class, PacketInviteToGang.class, Side.SERVER);
        registerPacket(PacketModifyPlayer.Handler.class, PacketModifyPlayer.class, Side.SERVER);
        registerPacket(PacketUpdateMemberList.Handler.class, PacketUpdateMemberList.class, Side.CLIENT);
        registerPacket(PacketModifyPlayerResponse.Handler.class, PacketModifyPlayerResponse.class, Side.CLIENT);
    }

    @Override
    public void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandGang());
    }

    @Override
    public Class<? extends MLProxy> getServerProxyClass() {
        return com.minelife.gangs.server.ServerProxy.class;
    }

    public static Gang getGang(String name) {
        return cache_gangs.stream().filter(g -> g.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public static Gang getGang(UUID id) {
        return cache_gangs.stream().filter(g -> g.getGangID().equals(id)).findFirst().orElse(null);
    }

    public static Gang getPlayerGang(UUID player) {
        for (Gang gang : cache_gangs) {
            if(gang.getLeader().equals(player)) return gang;
            if(gang.getMembers().stream().filter(member -> member.equals(player)).findFirst().orElse(null) != null) return gang;
        }
        return null;
    }

    public static Set<Estate> getEstates(Gang gang) {
        Set<Estate> estates = Sets.newTreeSet();

        for (Estate estate : EstateHandler.loadedEstates) {
            if(estate.getOwner() != null) {
                if(gang.getLeader() != null) {
                    if (estate.getOwner().equals(gang.getLeader())) {
                        estates.add(estate);
                    }
                }
            }
        }

        return estates;
    }

}
