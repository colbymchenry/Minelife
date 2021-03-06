package com.minelife.realestate;

import com.google.common.collect.Sets;
import com.minelife.MLMod;
import com.minelife.MLProxy;
import com.minelife.Minelife;
import com.minelife.jobs.EntityJobNPC;
import com.minelife.realestate.network.*;
import com.minelife.realestate.server.CommandEstate;
import com.minelife.realestate.server.ServerProxy;
import com.minelife.util.MLConfig;
import lib.PatPeter.SQLibrary.Database;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;

import java.util.*;

public class ModRealEstate extends MLMod {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        registerPacket(PacketSelection.Handler.class, PacketSelection.class, Side.CLIENT);
        registerPacket(PacketCreateGui.Handler.class, PacketCreateGui.class, Side.CLIENT);
        registerPacket(PacketCreateEstate.Handler.class, PacketCreateEstate.class, Side.SERVER);
        registerPacket(PacketUpdateEstate.Handler.class, PacketUpdateEstate.class, Side.SERVER);
        registerPacket(PacketModifyGui.Handler.class, PacketModifyGui.class, Side.CLIENT);
        registerPacket(PacketBuyGui.Handler.class, PacketBuyGui.class, Side.CLIENT);
        registerPacket(PacketPurchaseEstate.Handler.class, PacketPurchaseEstate.class, Side.SERVER);
        registerPacket(PacketModifyMember.Handler.class, PacketModifyMember.class, Side.SERVER);
        registerPacket(PacketUpdatedMember.Handler.class, PacketUpdatedMember.class, Side.CLIENT);
        registerPacket(PacketOpenReceptionistGUI.Handler.class, PacketOpenReceptionistGUI.class, Side.CLIENT);
        registerPacket(PacketAddMember.Handler.class, PacketAddMember.class, Side.SERVER);
        registerPacket(PacketRemoveMember.Handler.class, PacketRemoveMember.class, Side.SERVER);
        EntityRegistry.registerModEntity(new ResourceLocation(Minelife.MOD_ID, "receptionist"), EntityReceptionist.class, "receptionist", 2, Minelife.getInstance(), 77, 1, true);
    }

    @Override
    public void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandEstate());
    }

    @Override
    public Class<? extends MLProxy> getClientProxyClass() {
        return com.minelife.realestate.client.ClientProxy.class;
    }

    @Override
    public Class<? extends MLProxy> getServerProxyClass() {
        return com.minelife.realestate.server.ServerProxy.class;
    }

    public static Database getDatabase() {
        return ServerProxy.DB;
    }

    public static MLConfig getConfig() {
        return ServerProxy.CONFIG;
    }

    public static Set<Estate> getLoadedEstates() {
        return ServerProxy.ESTATES;
    }

    public static Estate getEstateAt(World world, BlockPos pos) {
        Iterator<Estate> estates = getLoadedEstates().iterator();
        Estate closest = null;
        while(estates.hasNext()) {
            Estate e = estates.next();
            if (e.getWorld() != null && e.getWorld().equals(world) && e.contains(pos)) {
                if (closest == null) closest = e;
                else {
                    int distMinX = pos.getX() - e.getMinimum().getX();
                    int distMinX1 = pos.getX() - closest.getMinimum().getX();
                    if (distMinX < distMinX1) closest = e;
                }
            }

            if(e.getWorld() == null) estates.remove();
        }

        return closest;
    }

    public static Estate getEstate(UUID uniqueID) {
        return getLoadedEstates().stream().filter(estate -> estate.getUniqueID().equals(uniqueID)).findFirst().orElse(null);
    }

    public static Set<Estate> getEstates(UUID playerID) {
        Set<Estate> estates = Sets.newTreeSet();
        getLoadedEstates().forEach(estate -> {
            boolean isRenter = Objects.equals(playerID, estate.getRenterID());
            boolean isOwner = Objects.equals(playerID, estate.getOwnerID());
            boolean hasRenter = estate.getRenterID() != null;
            if(isOwner && !hasRenter)
                estates.add(estate);
            else if (isRenter) {
                estates.add(estate);
            }
        });
        return estates;
    }

}
