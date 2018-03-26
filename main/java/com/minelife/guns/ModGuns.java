package com.minelife.guns;

import com.minelife.MLMod;
import com.minelife.MLProxy;
import com.minelife.guns.item.ItemGun;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ModGuns extends MLMod {

    public static ItemGun itemGun;

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        registerItem(itemGun = new ItemGun());
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public Class<? extends MLProxy> getClientProxyClass() {
        return com.minelife.guns.client.ClientProxy.class;
    }


}
