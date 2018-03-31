package com.minelife.drugs;

import com.minelife.MLMod;
import com.minelife.MLProxy;
import com.minelife.Minelife;
import com.minelife.drugs.block.BlockCocaCrop;
import com.minelife.drugs.block.BlockHempCrop;
import com.minelife.drugs.block.BlockLimeCrop;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ModDrugs extends MLMod {

    public static BlockHempCrop blockHempCrop;
    public static BlockLimeCrop blockLimeCrop;
    public static BlockCocaCrop blockCocaCrop;
    public static ItemSeeds itemHempSeed, itemLimeSeed, itemCocaSeed;
    public static Item itemCalciumHydroxide, itemCalciumOxide, itemHempBuds, itemHempShredded, itemCocaLeafShredded, itemCocaPaste, itemLime,
            itemPotassiumHydroxide, itemPotassiumManganate, itemPyrolusite, itemSalt, itemSulfur, itemPotassiumHydroxidePyrolusiteMixture,
            itemWaxyCocaine, itemHeatedCocaine, itemPressedCocaine, itemPurpleCocaine, itemAmmonia, itemCocaLeaf, itemPotassiumPermanganate,
            itemSulfuricAcid;

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        registerBlock(blockHempCrop = new BlockHempCrop());
        registerBlock(blockLimeCrop = new BlockLimeCrop());
        registerBlock(blockCocaCrop = new BlockCocaCrop());
        registerItem(itemHempSeed = (ItemSeeds) new ItemSeeds(blockHempCrop, Blocks.FARMLAND)
                .setRegistryName(Minelife.MOD_ID, "hemp_seed").setUnlocalizedName(Minelife.MOD_ID + ":hemp_seed")
                .setCreativeTab(CreativeTabs.MISC));
        registerItem(itemLimeSeed = (ItemSeeds) new ItemSeeds(blockLimeCrop, Blocks.FARMLAND)
                .setRegistryName(Minelife.MOD_ID, "lime_seed").setUnlocalizedName(Minelife.MOD_ID + ":lime_seed")
                .setCreativeTab(CreativeTabs.MISC));
        registerItem(itemCocaSeed = (ItemSeeds) new ItemSeeds(blockCocaCrop, Blocks.FARMLAND)
                .setRegistryName(Minelife.MOD_ID, "coca_seed").setUnlocalizedName(Minelife.MOD_ID + ":coca_seed")
                .setCreativeTab(CreativeTabs.MISC));

        registerItem(itemCalciumHydroxide = new Item().setRegistryName(Minelife.MOD_ID, "calcium_hydroxide").setUnlocalizedName(Minelife.MOD_ID + ":" + "calcium_hydroxide")
                .setCreativeTab(CreativeTabs.MISC));
        registerItem(itemCalciumOxide = new Item().setRegistryName(Minelife.MOD_ID, "calcium_oxide").setUnlocalizedName(Minelife.MOD_ID + ":" + "calcium_oxide")
                .setCreativeTab(CreativeTabs.MISC));
        registerItem(itemHempBuds = new Item().setRegistryName(Minelife.MOD_ID, "hemp_buds").setUnlocalizedName(Minelife.MOD_ID + ":" + "hemp_buds")
                .setCreativeTab(CreativeTabs.MISC));
        registerItem(itemHempShredded = new Item().setRegistryName(Minelife.MOD_ID, "hemp_shredded").setUnlocalizedName(Minelife.MOD_ID + ":" + "hemp_shredded")
                .setCreativeTab(CreativeTabs.MISC));
        registerItem(itemCocaLeafShredded = new Item().setRegistryName(Minelife.MOD_ID, "coca_leaf_shredded").setUnlocalizedName(Minelife.MOD_ID + ":" + "coca_leaf_shredded")
                .setCreativeTab(CreativeTabs.MISC));
        registerItem(itemCocaPaste = new Item().setRegistryName(Minelife.MOD_ID, "coca_paste").setUnlocalizedName(Minelife.MOD_ID + ":" + "coca_paste")
                .setCreativeTab(CreativeTabs.MISC));
        registerItem(itemLime = new Item().setRegistryName(Minelife.MOD_ID, "lime").setUnlocalizedName(Minelife.MOD_ID + ":" + "lime")
                .setCreativeTab(CreativeTabs.MISC));
        registerItem(itemPotassiumHydroxide = new Item().setRegistryName(Minelife.MOD_ID, "potassium_hydroxide").setUnlocalizedName(Minelife.MOD_ID + ":" + "potassium_hydroxide")
                .setCreativeTab(CreativeTabs.MISC));
        registerItem(itemPotassiumManganate = new Item().setRegistryName(Minelife.MOD_ID, "potassium_manganate").setUnlocalizedName(Minelife.MOD_ID + ":" + "potassium_manganate")
                .setCreativeTab(CreativeTabs.MISC));
        registerItem(itemPyrolusite = new Item().setRegistryName(Minelife.MOD_ID, "pyrolusite").setUnlocalizedName(Minelife.MOD_ID + ":" + "pyrolusite")
                .setCreativeTab(CreativeTabs.MISC));
        registerItem(itemSalt = new Item().setRegistryName(Minelife.MOD_ID, "salt").setUnlocalizedName(Minelife.MOD_ID + ":" + "salt")
                .setCreativeTab(CreativeTabs.MISC));
        registerItem(itemSulfur = new Item().setRegistryName(Minelife.MOD_ID, "sulfur").setUnlocalizedName(Minelife.MOD_ID + ":" + "sulfur")
                .setCreativeTab(CreativeTabs.MISC));
        registerItem(itemPotassiumHydroxidePyrolusiteMixture = new Item().setRegistryName(Minelife.MOD_ID, "potassium_hydroxide_pyrolusite_mixture").setUnlocalizedName(Minelife.MOD_ID + ":" + "potassium_hydroxide_pyrolusite_mixture")
                .setCreativeTab(CreativeTabs.MISC));
        registerItem(itemWaxyCocaine = new Item().setRegistryName(Minelife.MOD_ID, "waxy_cocaine").setUnlocalizedName(Minelife.MOD_ID + ":" + "waxy_cocaine")
                .setCreativeTab(CreativeTabs.MISC));
        registerItem(itemHeatedCocaine = new Item().setRegistryName(Minelife.MOD_ID, "heated_cocaine").setUnlocalizedName(Minelife.MOD_ID + ":" + "heated_cocaine")
                .setCreativeTab(CreativeTabs.MISC));
        registerItem(itemPressedCocaine = new Item().setRegistryName(Minelife.MOD_ID, "pressed_cocaine").setUnlocalizedName(Minelife.MOD_ID + ":" + "pressed_cocaine")
                .setCreativeTab(CreativeTabs.MISC));
        registerItem(itemPurpleCocaine = new Item().setRegistryName(Minelife.MOD_ID, "purple_cocaine").setUnlocalizedName(Minelife.MOD_ID + ":" + "purple_cocaine")
                .setCreativeTab(CreativeTabs.MISC));
        registerItem(itemAmmonia = new Item().setRegistryName(Minelife.MOD_ID, "ammonia").setUnlocalizedName(Minelife.MOD_ID + ":" + "ammonia")
                .setCreativeTab(CreativeTabs.MISC));
        registerItem(itemCocaLeaf = new Item().setRegistryName(Minelife.MOD_ID, "coca_leaf").setUnlocalizedName(Minelife.MOD_ID + ":" + "coca_leaf")
                .setCreativeTab(CreativeTabs.MISC).setMaxDamage(100));
        registerItem(itemPotassiumPermanganate = new Item().setRegistryName(Minelife.MOD_ID, "potassium_permanganate").setUnlocalizedName(Minelife.MOD_ID + ":" + "potassium_permanganate")
                .setCreativeTab(CreativeTabs.MISC));
        registerItem(itemSulfuricAcid = new Item().setRegistryName(Minelife.MOD_ID, "sulfuric_acid").setUnlocalizedName(Minelife.MOD_ID + ":" + "sulfuric_acid")
                .setCreativeTab(CreativeTabs.MISC));
    }

    @Override
    public Class<? extends MLProxy> getClientProxyClass() {
        return com.minelife.drugs.client.ClientProxy.class;
    }

    @Override
    public Class<? extends MLProxy> getServerProxyClass() {
        return com.minelife.drugs.server.ServerProxy.class;
    }

    public static void setCocaLeafMoisture(ItemStack stack, int moisture) {
        if(stack == null) return;
        if(stack.getItem() != itemCocaLeaf) return;
        moisture = moisture < 0 ? 0 : moisture > 100 ? 100 : moisture;
        stack.setItemDamage(100 - moisture);
    }

    public static int getCocaLeafMoisture(ItemStack stack) {
        if(stack == null) return 0;
        if(stack.getItem() != itemCocaLeaf) return 0;

        return 100 - stack.getItemDamage();
    }

}
