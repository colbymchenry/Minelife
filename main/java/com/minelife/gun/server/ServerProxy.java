package com.minelife.gun.server;

import com.minelife.CommonProxy;
import com.minelife.gun.item.guns.ItemGun;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;

public class ServerProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onBreak(BlockEvent.BreakEvent event) {
        ItemStack heldItem = event.getPlayer().getHeldItem();

        if(heldItem == null) return;

        if(! (heldItem.getItem() instanceof ItemGun)) return;

        event.setCanceled(true);
    }

}
