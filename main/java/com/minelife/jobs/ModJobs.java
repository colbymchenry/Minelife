package com.minelife.jobs;

import com.minelife.MLMod;
import com.minelife.MLProxy;
import com.minelife.jobs.job.bountyhunter.CommandBounty;
import com.minelife.jobs.job.bountyhunter.ItemBountyCard;
import com.minelife.jobs.network.PacketJoinProfession;
import com.minelife.jobs.network.PacketOpenNormalGui;
import com.minelife.jobs.network.PacketOpenSignupGui;
import com.minelife.jobs.network.PacketSellItemStack;
import com.minelife.jobs.server.CommandJob;
import com.minelife.jobs.server.ServerProxy;
import lib.PatPeter.SQLibrary.Database;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

public class ModJobs extends MLMod {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        registerPacket(PacketOpenNormalGui.Handler.class, PacketOpenNormalGui.class, Side.CLIENT);
        registerPacket(PacketOpenSignupGui.Handler.class, PacketOpenSignupGui.class, Side.CLIENT);
        registerPacket(PacketJoinProfession.Handler.class, PacketJoinProfession.class, Side.SERVER);
        registerPacket(PacketSellItemStack.Handler.class, PacketSellItemStack.class, Side.SERVER);
        registerItem(ItemBountyCard.INSTANCE);
    }

    @Override
    public Class<? extends MLProxy> getClientProxyClass() {
        return com.minelife.jobs.client.ClientProxy.class;
    }

    @Override
    public Class<? extends MLProxy> getServerProxyClass() {
        return com.minelife.jobs.server.ServerProxy.class;
    }

    @Override
    public void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandJob());
        event.registerServerCommand(new CommandBounty());
    }

    @Override
    public void entityRegistration(RegistryEvent.Register<EntityEntry> event) {
        event.getRegistry().register(
                EntityEntryBuilder.create()
                        .entity(EntityJobNPC.class)
                        .id(new ResourceLocation("minecraft", "job_npc"), 77)
                        .name("JobNPC")
                        .tracker(64, 20, false)
                        .build()
        );
    }

    public static Database getDatabase() {
        return ServerProxy.DB;
    }

}
