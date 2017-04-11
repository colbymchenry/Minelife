package com.minelife.gun;

import com.minelife.CommonProxy;
import com.minelife.SubMod;
import com.minelife.gun.gun.M4;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;

public class ModGun extends SubMod {

    public static final CreativeTabs tabGuns = new CreativeTabs("guns") {
        @Override
        public Item getTabIconItem() {
            return Items.diamond;
        }
    };

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        GameRegistry.registerItem(new M4(event), "m4");
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
