package com.minelife.capes.client;

import com.minelife.MLItems;
import com.minelife.MLProxy;
import com.minelife.Minelife;
import com.minelife.capes.network.PacketUpdateCapeStatus;
import com.minelife.gun.client.RenderGun;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;

public class ClientProxy extends MLProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) throws Exception {
        MinecraftForgeClient.registerItemRenderer(MLItems.cape, new ItemCapeRenderer());
    }


}
