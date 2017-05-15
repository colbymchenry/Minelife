package com.minelife.gun;

import com.minelife.CommonProxy;
import com.minelife.SubMod;
import com.minelife.gun.block.BlockZincOre;
import com.minelife.gun.item.*;
import com.minelife.gun.packet.PacketMouseClick;
import com.minelife.gun.packet.PacketReload;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import ic2.api.recipe.Recipes;
import ic2.core.IC2;
import ic2.core.Ic2Items;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import static ic2.core.Ic2Items.ForgeHammer;

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
    public void init(FMLInitializationEvent event) {
//       Recipes.macerator.addRecipe(new RecipeInputItemStack(yourInputItemStack, amount), null, yourItemStackOutput);
        GameRegistry.registerWorldGenerator(new ZincGenerator(), 0);
        GameRegistry.addSmelting(BlockZincOre.instance, new ItemStack(ItemZincIngot.instance, 1), 1F);

        GameRegistry.addRecipe(new ItemStack(ItemGunmetal.instance),
                "CCC",
                "ZZZ",
                "TTT",
                'C', Ic2Items.platecopper,
                'Z', ItemZincPlate.instance,
                'T', Ic2Items.platetin
        );

        GameRegistry.addShapelessRecipe(new ItemStack(ItemZincPlate.instance), new ItemStack(ForgeHammer.getItem(), 1, OreDictionary.WILDCARD_VALUE), ItemZincIngot.instance);

        ItemGrip.registerRecipe();
        ItemTrigger.registerRecipe();
        ItemPistolBarrel.registerRecipe();
        ItemRifleStock.registerRecipe();
        ItemRifleBarrel.registerRecipe();
        ItemSniperBarrel.registerRecipe();
        ItemSniperScope.registerRecipe();
        ItemPistolFrame.registerRecipe();
        ItemRifleFrame.registerRecipe();
        ItemSniperFrame.registerRecipe();
    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        registerPacket(PacketMouseClick.Handler.class, PacketMouseClick.class, Side.SERVER);
        registerPacket(PacketReload.Handler.class, PacketReload.class, Side.SERVER);

        AmmoRegistry.registerAmmos();
        GunRegistry.registerGuns(event);

        GameRegistry.registerBlock(new BlockZincOre(), "zincOre");
        GameRegistry.registerItem(new ItemZincPlate(), "zincPlate");
        GameRegistry.registerItem(new ItemZincIngot(), "zincIngot");
        GameRegistry.registerItem(new ItemGunmetal(), "gunmetal");
        GameRegistry.registerItem(new ItemGrip(), "grip");
        GameRegistry.registerItem(new ItemTrigger(), "trigger");
        GameRegistry.registerItem(new ItemPistolBarrel(), "pistolBarrel");
        GameRegistry.registerItem(new ItemPistolFrame(), "pistolFrame");
        GameRegistry.registerItem(new ItemRifleBarrel(), "rifleBarrel");
        GameRegistry.registerItem(new ItemRifleFrame(), "rifleFrame");
        GameRegistry.registerItem(new ItemRifleStock(), "rifleStock");
        GameRegistry.registerItem(new ItemSniperBarrel(), "sniperBarrel");
        GameRegistry.registerItem(new ItemSniperFrame(), "sniperFrame");
        GameRegistry.registerItem(new ItemSniperScope(), "sniperScope");
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
