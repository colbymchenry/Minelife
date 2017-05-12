package com.minelife.gun;

import com.minelife.CommonProxy;
import com.minelife.SubMod;
import com.minelife.gun.packet.PacketMouseClick;
import com.minelife.gun.packet.PacketReload;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;

public class ModGun extends SubMod {

    public static final CreativeTabs tabGuns = new CreativeTabs("gun") {
        @Override
        public Item getTabIconItem() {
            return Items.diamond;
        }
    };

    public static final CreativeTabs tabAmmo = new CreativeTabs("ammo") {
        @Override
        public Item getTabIconItem() {
            return Items.emerald;
        }
    };

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        registerPacket(PacketMouseClick.Handler.class, PacketMouseClick.class, Side.SERVER);
        registerPacket(PacketReload.Handler.class, PacketReload.class, Side.SERVER);

        AmmoRegistry.registerAmmos();
        GunRegistry.registerGuns(event);
    }

    @Override
    public Class<? extends CommonProxy> getClientProxy() {
        return com.minelife.gun.client.ClientProxy.class;
    }

    @Override
    public Class<? extends CommonProxy> getServerProxy() {
        return com.minelife.gun.server.ServerProxy.class;
    }
}
