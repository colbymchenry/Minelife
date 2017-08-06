package com.minelife.gun;

import com.minelife.CommonProxy;
import com.minelife.AbstractMod;
import com.minelife.Minelife;
import com.minelife.gun.block.BlockZincOre;
import com.minelife.gun.item.*;
import com.minelife.gun.item.ammos.ItemAmmo;
import com.minelife.gun.item.guns.ItemGun;
import com.minelife.gun.item.parts.ItemGunPart;
import com.minelife.gun.packet.PacketMouseClick;
import com.minelife.gun.packet.PacketReload;
import com.minelife.gun.packet.PacketSetAmmoType;
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

public class ModGun extends AbstractMod {

    public static final CreativeTabs tabGuns = new CreativeTabs("guns") {
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
    public void init(FMLInitializationEvent event) {
        GameRegistry.registerWorldGenerator(new ZincGenerator(), 0);
        GameRegistry.addSmelting(Minelife.blocks.zinc_ore, new ItemStack(Minelife.items.zinc_ingot, 1), 1F);

        GameRegistry.addRecipe(new ItemStack(Minelife.items.gunmetal),
                "CCC",
                "ZZZ",
                "TTT",
                'C', Ic2Items.platecopper,
                'Z', Minelife.items.zinc_plate,
                'T', Ic2Items.platetin
        );

        GameRegistry.addShapelessRecipe(new ItemStack(Minelife.items.zinc_plate), new ItemStack(ForgeHammer.getItem(), 1, OreDictionary.WILDCARD_VALUE), Minelife.items.zinc_ingot);
    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        registerPacket(PacketMouseClick.Handler.class, PacketMouseClick.class, Side.SERVER);
        registerPacket(PacketReload.Handler.class, PacketReload.class, Side.SERVER);
        registerPacket(PacketSetAmmoType.Handler.class, PacketSetAmmoType.class, Side.SERVER);

        ItemGunPart.registerRecipes();
        ItemAmmo.registerRecipes();
        ItemGun.registerRecipes();
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
