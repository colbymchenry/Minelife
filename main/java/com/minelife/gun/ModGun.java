package com.minelife.gun;

import com.minelife.CommonProxy;
import com.minelife.SubMod;
import com.minelife.gun.block.BlockZincOre;
import com.minelife.gun.item.*;
import com.minelife.gun.item.ammos.ItemAmmo;
import com.minelife.gun.item.guns.ItemGun;
import com.minelife.gun.item.parts.ItemGunPart;
import com.minelife.gun.packet.PacketMouseClick;
import com.minelife.gun.packet.PacketReload;
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

public class ModGun extends SubMod {

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
//       Recipes.macerator.addRecipe(new RecipeInputItemStack(yourInputItemStack, amount), null, yourItemStackOutput);
        GameRegistry.registerWorldGenerator(new ZincGenerator(), 0);
        GameRegistry.addSmelting(BlockZincOre.getBlock(), new ItemStack(ItemZincIngot.getItem(), 1), 1F);

        GameRegistry.addRecipe(new ItemStack(ItemGunmetal.getItem()),
                "CCC",
                "ZZZ",
                "TTT",
                'C', Ic2Items.platecopper,
                'Z', ItemZincPlate.getItem(),
                'T', Ic2Items.platetin
        );

        GameRegistry.addShapelessRecipe(new ItemStack(ItemZincPlate.getItem()), new ItemStack(ForgeHammer.getItem(), 1, OreDictionary.WILDCARD_VALUE), ItemZincIngot.getItem());
    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        registerPacket(PacketMouseClick.Handler.class, PacketMouseClick.class, Side.SERVER);
        registerPacket(PacketReload.Handler.class, PacketReload.class, Side.SERVER);

        GameRegistry.registerBlock(BlockZincOre.getBlock(), "zincOre");
        GameRegistry.registerItem(ItemGunmetal.getItem(), "gunMetal");
        GameRegistry.registerItem(ItemZincIngot.getItem(), "zincIngot");
        GameRegistry.registerItem(ItemZincPlate.getItem(), "zincPlate");

        ItemGunPart.registerParts();
        ItemAmmo.registerAmmos();
        ItemGun.registerGuns();
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
