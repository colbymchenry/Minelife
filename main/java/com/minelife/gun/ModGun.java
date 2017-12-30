package com.minelife.gun;

import com.minelife.*;
import com.minelife.gun.bullets.BulletHandler;
import com.minelife.gun.item.ammos.ItemAmmo;
import com.minelife.gun.item.guns.ItemGun;
import com.minelife.gun.item.parts.ItemGunPart;
import com.minelife.gun.packet.*;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import ic2.core.Ic2Items;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import static ic2.core.Ic2Items.ForgeHammer;

public class ModGun extends MLMod {

    public static final CreativeTabs tabGuns = new CreativeTabs("guns") {
        @Override
        public Item getTabIconItem() {
            return MLItems.magnum;
        }
    };

    // TODO: Turrets
    public static final CreativeTabs tabAmmo = new CreativeTabs("ammo") {
        @Override
        public Item getTabIconItem() {
            return MLItems.ammo_556;
        }
    };

    @Override
    public void init(FMLInitializationEvent event) {
        GameRegistry.registerWorldGenerator(new ZincGenerator(), 8);
        GameRegistry.addSmelting(MLBlocks.zinc_ore, new ItemStack(MLItems.zinc_ingot, 1), 1F);

        GameRegistry.addRecipe(new ItemStack(MLItems.gunmetal),
                "CCC",
                "ZZZ",
                "TTT",
                'C', Ic2Items.platecopper,
                'Z', MLItems.zinc_plate,
                'T', Ic2Items.platetin
        );

        GameRegistry.addShapelessRecipe(new ItemStack(MLItems.zinc_plate), new ItemStack(ForgeHammer.getItem(), 1, OreDictionary.WILDCARD_VALUE), MLItems.zinc_ingot);
        FMLCommonHandler.instance().bus().register(new BulletHandler());
    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        registerPacket(PacketMouseClick.Handler.class, PacketMouseClick.class, Side.SERVER);
        registerPacket(PacketReload.Handler.class, PacketReload.class, Side.SERVER);
        registerPacket(PacketSetAmmoType.Handler.class, PacketSetAmmoType.class, Side.SERVER);
        registerPacket(PacketBullet.Handler.class, PacketBullet.class, Side.CLIENT);
        registerPacket(PacketSetSiteColor.Handler.class, PacketSetSiteColor.class, Side.SERVER);

        ItemGunPart.registerRecipes();
        ItemAmmo.registerRecipes();
        ItemGun.registerRecipes();
    }

    @Override
    public Class<? extends MLProxy> getClientProxyClass() {
        return com.minelife.gun.client.ClientProxy.class;
    }

    @Override
    public Class<? extends MLProxy> getServerProxyClass() {
        return com.minelife.gun.server.ServerProxy.class;
    }

}
