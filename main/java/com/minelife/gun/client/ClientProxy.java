package com.minelife.gun.client;

import com.minelife.CommonProxy;
import com.minelife.gun.item.guns.ItemGun;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.common.MinecraftForge;

public class ClientProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        FMLCommonHandler.instance().bus().register(new KeyStrokeListener());
        MinecraftForge.EVENT_BUS.register(new OverlayRenderer());
        MinecraftForge.EVENT_BUS.register(this);
        ItemGun.registerRenderers();
    }

    /**
     * This method is put in place to prevent the player from interacting with blocks while holding a guns
     */
    @SubscribeEvent
    public void onClick(MouseEvent event) {
        if(Minecraft.getMinecraft().thePlayer == null) return;

        ItemStack heldItem = Minecraft.getMinecraft().thePlayer.getHeldItem();

        if(heldItem == null || !(heldItem.getItem() instanceof ItemGun)) return;

        if(event.button == 1) return;

        if(event.dwheel != 0) return;

        event.setCanceled(true);
    }

}
