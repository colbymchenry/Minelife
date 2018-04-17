package com.minelife.cape.client;

import com.minelife.MLProxy;
import com.minelife.Minelife;
import com.minelife.cape.ModCapes;
import com.minelife.cape.network.PacketRequestCape;
import com.minelife.cape.network.PacketUpdateCape;
import com.minelife.cape.network.PacketUpdateCapeStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ClientProxy extends MLProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        registerItemRenderer(ModCapes.itemCape, new ItemCapeRenderer());
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onJoinWorld(EntityJoinWorldEvent event) {
        if(event.getEntity() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.getEntity();
            if(!player.getUniqueID().equals(Minecraft.getMinecraft().player.getUniqueID()))
                Minelife.getNetwork().sendToServer(new PacketRequestCape(player.getUniqueID()));
        }
    }

}
