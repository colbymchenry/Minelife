package com.minelife.drugs;

import com.minelife.AbstractGuiHandler;
import com.minelife.MLMod;
import com.minelife.MLProxy;
import com.minelife.Minelife;
import com.minelife.drugs.block.*;
import com.minelife.drugs.item.ItemGrinder;
import com.minelife.drugs.item.ItemJoint;
import com.minelife.drugs.item.ItemProcessedCocaine;
import com.minelife.util.MLFluid;
import com.minelife.util.MLFluidBlock;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;

import java.awt.*;

public class ModDrugs extends MLMod {


    // TODO: Make hemp, lime, and coca randomly generate or come up with a way for players to get the seeds, would be cool if rare and random
    // TODO: That way they are more valuable
    // TODO: Make hydroponics system for marijuana

    public static BlockHempCrop blockHempCrop;
    public static BlockLimeCrop blockLimeCrop;
    public static BlockCocaCrop blockCocaCrop;
    public static BlockSulfurOre blockSulfurOre;
    public static BlockPyrolusiteOre blockPyrolusiteOre;
    public static BlockPotash blockPotash;
    public static BlockLimestone blockLimestone;
    public static ItemBlock itemSulfurOre, itemPyrolusiteOre, itemPotashBlock, itemLimestoneBlock;
//            itemVacuumBlock, itemLeafMulcherBlock, itemPresserBlock, itemCementMixerBlock;
    public static ItemJoint itemJoint;
    public static ItemSeeds itemHempSeed, itemLimeSeed, itemCocaSeed;
    public static Item itemCalciumHydroxide, itemCalciumOxide, itemHempBuds, itemHempShredded, itemCocaLeafShredded, itemCocaPaste, itemLime,
            itemPotassiumHydroxide, itemPotassiumManganate, itemPyrolusite, itemSalt, itemSulfur, itemPotassiumHydroxidePyrolusiteMixture,
            itemWaxyCocaine, itemHeatedCocaine, itemPressedCocaine, itemPurpleCocaine, itemCocaLeaf, itemProcessedCocaine, itemGrinder;
//    public static BlockVacuum blockVacuum;
//    public static BlockLeafMulcher blockLeafMulcher;
//    public static BlockPresser blockPresser;
//    public static BlockCementMixer blockCementMixer;

    public static MLFluidBlock blockAmmonia, blockPotassiumPermanganate, blockSulfuricAcid;
    public static MLFluid fluidAmmonia, fluidPotassiumPermanganate, fluidSulfuricAcid;

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        ForgeRegistries.POTIONS.register(XRayEffect.INSTANCE);
        ForgeRegistries.POTIONS.register(DetectableHempEffect.INSTANCE);
        ForgeRegistries.POTIONS.register(DetectableCocaineEffect.INSTANCE);
        MinecraftForge.EVENT_BUS.register(XRayEffect.INSTANCE);
        MinecraftForge.EVENT_BUS.register(DetectableHempEffect.INSTANCE);
        MinecraftForge.EVENT_BUS.register(DetectableCocaineEffect.INSTANCE);

        FluidRegistry.registerFluid(fluidAmmonia = new MLFluid("ammonia"));
        FluidRegistry.registerFluid(fluidPotassiumPermanganate = new MLFluid("potassium_permanganate"));
        FluidRegistry.registerFluid(fluidSulfuricAcid = new MLFluid("sulfuric_acid"));

        registerBlock(blockAmmonia = new MLFluidBlock(fluidAmmonia, Material.WATER, "ammonia", Color.GREEN));
        registerBlock(blockPotassiumPermanganate = new MLFluidBlock(fluidPotassiumPermanganate, Material.WATER, "potassium_permanganate", Color.MAGENTA));
        registerBlock(blockSulfuricAcid = new MLFluidBlock(fluidSulfuricAcid, Material.WATER, "sulfuric_acid", Color.WHITE));

        registerBlock(blockHempCrop = new BlockHempCrop());
        registerBlock(blockLimeCrop = new BlockLimeCrop());
        registerBlock(blockCocaCrop = new BlockCocaCrop());
        registerBlock(blockSulfurOre = new BlockSulfurOre());
        registerBlock(blockPyrolusiteOre = new BlockPyrolusiteOre());
        registerBlock(blockPotash = new BlockPotash());
        registerBlock(blockLimestone = new BlockLimestone());

//        registerBlock(blockVacuum = new BlockVacuum());
//        registerTileEntity(TileEntityVacuum.class, "vacuum");
//        registerItem(itemVacuumBlock = (ItemBlock) new ItemBlock(blockVacuum).setRegistryName(Minelife.MOD_ID, "vacuum")
//                .setUnlocalizedName(Minelife.MOD_ID + ":vacuum").setCreativeTab(CreativeTabs.MISC));
//
//        registerBlock(blockPresser = new BlockPresser());
//        registerTileEntity(TileEntityPresser.class, "presser");
//        registerItem(itemPresserBlock = (ItemBlock) new ItemBlock(blockPresser).setRegistryName(Minelife.MOD_ID, "presser")
//                .setUnlocalizedName(Minelife.MOD_ID + ":presser").setCreativeTab(CreativeTabs.MISC));
//
//        registerBlock(blockLeafMulcher = new BlockLeafMulcher());
//        registerTileEntity(TileEntityLeafMulcher.class, "leaf_mulcher");
//        registerItem(itemLeafMulcherBlock = (ItemBlock) new ItemBlock(blockLeafMulcher).setRegistryName(Minelife.MOD_ID, "leaf_mulcher")
//                .setUnlocalizedName(Minelife.MOD_ID + ":leaf_mulcher").setCreativeTab(CreativeTabs.MISC));
//
//        registerBlock(blockCementMixer = new BlockCementMixer());
//        registerTileEntity(TileEntityCementMixer.class, "cement_mixer");
//        registerItem(itemCementMixerBlock = (ItemBlock) new ItemBlock(blockCementMixer).setRegistryName(Minelife.MOD_ID, "cement_mixer")
//                .setUnlocalizedName(Minelife.MOD_ID + ":cement_mixer").setCreativeTab(CreativeTabs.MISC));

        registerItem(itemSulfurOre = (ItemBlock) new ItemBlock(blockSulfurOre).setRegistryName(Minelife.MOD_ID, "sulfur_ore")
                .setUnlocalizedName(Minelife.MOD_ID + ":sulfur_ore").setCreativeTab(CreativeTabs.MISC));
        registerItem(itemPyrolusiteOre = (ItemBlock) new ItemBlock(blockPyrolusiteOre).setRegistryName(Minelife.MOD_ID, "pyrolusite_ore")
                .setUnlocalizedName(Minelife.MOD_ID + ":pyrolusite_ore").setCreativeTab(CreativeTabs.MISC));
        registerItem(itemPotashBlock = (ItemBlock) new ItemBlock(blockPotash).setRegistryName(Minelife.MOD_ID, "potash")
                .setUnlocalizedName(Minelife.MOD_ID + ":potash").setCreativeTab(CreativeTabs.MISC));
        registerItem(itemLimestoneBlock = (ItemBlock) new ItemBlock(blockLimestone).setRegistryName(Minelife.MOD_ID, "limestone")
                .setUnlocalizedName(Minelife.MOD_ID + ":limestone").setCreativeTab(CreativeTabs.MISC));

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
        registerItem(itemCocaLeaf = new Item().setRegistryName(Minelife.MOD_ID, "coca_leaf").setUnlocalizedName(Minelife.MOD_ID + ":" + "coca_leaf")
                .setCreativeTab(CreativeTabs.MISC).setMaxDamage(100));
        registerItem(itemJoint = new ItemJoint());
        registerItem(itemProcessedCocaine = new ItemProcessedCocaine());
        registerItem(itemGrinder = new ItemGrinder());

        GameRegistry.registerWorldGenerator(new BlockSulfurOre.Generator(), 0);
        GameRegistry.registerWorldGenerator(new BlockPyrolusiteOre.Generator(), 0);
        GameRegistry.registerWorldGenerator(new BlockPotash.Generator(), 0);
        GameRegistry.registerWorldGenerator(new BlockLimestone.Generator(), 0);
    }

    @Override
    public void init(FMLInitializationEvent event) {
        registerRecipes();
    }

    @Override
    public Class<? extends MLProxy> getClientProxyClass() {
        return com.minelife.drugs.client.ClientProxy.class;
    }

    @Override
    public Class<? extends MLProxy> getServerProxyClass() {
        return com.minelife.drugs.server.ServerProxy.class;
    }

    @Override
    public AbstractGuiHandler getGuiHandler() {
        return new DrugsGuiHandler();
    }

    // TODO: Make marijuana need torches near them to grow and make plants need a certain altitude to grow and maybe fans for air
    private void registerRecipes() {
        ResourceLocation name = new ResourceLocation(Minelife.MOD_ID + ":leaf_mulcher");
//        GameRegistry.addShapedRecipe(name, null, new ItemStack(itemLeafMulcherBlock),
//                "AAA",
//                "ABA",
//                "ACA",
//                'A', Ingredient.fromStacks(new ItemStack(ItemName.crafting.getInstance(), 1, 3)),
//                'B', Ingredient.fromStacks(new ItemStack(Item.getItemFromBlock(BCCoreBlocks.engine), 1, 2)),
//                'C', Ingredient.fromStacks(new ItemStack(ItemName.block_cutting_blade.getInstance(), 1, 0)));
//
//        name = new ResourceLocation(Minelife.MOD_ID + ":cement_mixer");
//        GameRegistry.addShapedRecipe(name, null, new ItemStack(itemCementMixerBlock),
//                "AAA",
//                "ABA",
//                "ACA",
//                'A', Ingredient.fromStacks(new ItemStack(ItemName.crafting.getInstance(), 1, 3)),
//                'B', Ingredient.fromStacks(new ItemStack(ItemName.crafting.getInstance(), 1, 6)),
//                'C', Ingredient.fromStacks(new ItemStack(Item.getItemFromBlock(BCFactoryBlocks.tank))));
//
//        name = new ResourceLocation(Minelife.MOD_ID + ":vacuum");
//        GameRegistry.addShapedRecipe(name, null, new ItemStack(itemVacuumBlock),
//                "AAA",
//                "ABA",
//                "ACA",
//                'A', Ingredient.fromStacks(new ItemStack(ItemName.crafting.getInstance(), 1, 15)),
//                'B', Ingredient.fromStacks(new ItemStack(ItemName.crafting.getInstance(), 1, 2)),
//                'C', Ingredient.fromStacks(new ItemStack(ItemName.crafting.getInstance(), 1, 6)));
//
//        name = new ResourceLocation(Minelife.MOD_ID + ":presser");
//        GameRegistry.addShapedRecipe(name, null, new ItemStack(itemPresserBlock),
//                "AAA",
//                "ABA",
//                "ACA",
//                'A', Ingredient.fromStacks(new ItemStack(ItemName.crafting.getInstance(), 1, 15)),
//                'B', Ingredient.fromStacks(new ItemStack(ItemName.crafting.getInstance(), 1, 2)),
//                'C', Ingredient.fromStacks(new ItemStack(Item.getItemFromBlock(BCCoreBlocks.engine), 1, 0)));

        name = new ResourceLocation(Minelife.MOD_ID + ":calcium_hydroxide");
        GameRegistry.addShapelessRecipe(name, null, new ItemStack(itemCalciumHydroxide), Ingredient.fromItem(Items.WATER_BUCKET), Ingredient.fromItem(ModDrugs.itemCalciumOxide));

        name = new ResourceLocation(Minelife.MOD_ID + ":shredded_hemp");
        GameRegistry.addShapelessRecipe(name, null, new ItemStack(itemHempShredded), Ingredient.fromItem(ModDrugs.itemHempBuds), Ingredient.fromStacks(new ItemStack(ModDrugs.itemGrinder, 1, OreDictionary.WILDCARD_VALUE)));

        name = new ResourceLocation(Minelife.MOD_ID + ":shredded_coca");
        GameRegistry.addShapelessRecipe(name, null, new ItemStack(itemCocaLeafShredded), Ingredient.fromStacks(new ItemStack(ModDrugs.itemCocaLeaf, 1, 100)), Ingredient.fromStacks(new ItemStack(ModDrugs.itemGrinder, 1, OreDictionary.WILDCARD_VALUE)));

        name = new ResourceLocation(Minelife.MOD_ID + ":joint");
        GameRegistry.addShapelessRecipe(name, null, new ItemStack(itemJoint), Ingredient.fromItem(ModDrugs.itemHempShredded), Ingredient.fromItem(Items.PAPER));

        name = new ResourceLocation(Minelife.MOD_ID + ":grinder");
        GameRegistry.addShapedRecipe(name, null, new ItemStack(itemGrinder),
                "AAA",
                "BBB",
                "AAA",
                'A', Ingredient.fromStacks(new ItemStack(Items.IRON_INGOT)),
                'B', Ingredient.fromStacks(new ItemStack(Blocks.PLANKS)));

        name = new ResourceLocation(Minelife.MOD_ID + ":potassium_hydroxide");
        GameRegistry.addShapelessRecipe(name, null, new ItemStack(itemPotassiumHydroxide), Ingredient.fromItem(ModDrugs.itemPotashBlock), Ingredient.fromItem(itemCalciumHydroxide));

        name = new ResourceLocation(Minelife.MOD_ID + ":potassium_hydroxide_pyrolusite_mixture");
        GameRegistry.addShapelessRecipe(name, null, new ItemStack(itemPotassiumHydroxidePyrolusiteMixture), Ingredient.fromItem(ModDrugs.itemPotassiumHydroxide), Ingredient.fromItem(itemPyrolusite));

        GameRegistry.addSmelting(itemLimestoneBlock, new ItemStack(itemCalciumOxide), 0.3F);
        GameRegistry.addSmelting(Items.WATER_BUCKET, new ItemStack(itemSalt), 1F);
        GameRegistry.addSmelting(itemPotassiumHydroxidePyrolusiteMixture, new ItemStack(itemPotassiumManganate), 0.3F);
        GameRegistry.addSmelting(itemWaxyCocaine, new ItemStack(itemHeatedCocaine), 0.3F);
    }

}
