package com.minelife.guns;

import com.minelife.MLMod;
import com.minelife.MLProxy;
import com.minelife.guns.item.ItemAmmo;
import com.minelife.guns.item.ItemGun;
import com.minelife.guns.packet.PacketBullet;
import com.minelife.guns.packet.PacketFire;
import com.minelife.guns.packet.PacketReload;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

public class ModGuns extends MLMod {

    public static ItemGun itemGun;
    public static ItemAmmo itemAmmo;

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        registerItem(itemGun = new ItemGun());
        registerItem(itemAmmo = new ItemAmmo());
        MinecraftForge.EVENT_BUS.register(this);
        registerPacket(PacketFire.Handler.class, PacketFire.class, Side.SERVER);
        registerPacket(PacketBullet.Handler.class, PacketBullet.class, Side.CLIENT);
        registerPacket(PacketReload.Handler.class, PacketReload.class, Side.SERVER);
    }

    @Override
    public Class<? extends MLProxy> getClientProxyClass() {
        return com.minelife.guns.client.ClientProxy.class;
    }

    @Override
    public Class<? extends MLProxy> getServerProxyClass() {
        return com.minelife.guns.server.ServerProxy.class;
    }
}
