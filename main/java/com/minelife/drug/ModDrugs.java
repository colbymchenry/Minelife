package com.minelife.drug;

import buildcraft.BuildCraftEnergy;
import com.minelife.*;
import com.minelife.drug.block.*;
import com.minelife.drug.item.ItemDrugTest;
import com.minelife.drug.tileentity.TileEntityCementMixer;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

public class ModDrugs extends AbstractMod {

    // TODO: Possible add syringe for shooting up cocaine for longer duration of effects, same for marjuana but maybe do oil

    public static final CreativeTabs tab_drugs = new CreativeTabs("tab_drugs") {
        @Override
        public Item getTabIconItem() {
            return MLItems.grinder;
        }
    };

    public static Potion x_ray_potion;
    public static Potion marijuana_potion;
    public static Potion cocaine_potion;

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        x_ray_potion = new XRayEffect(25, false, 0);
        marijuana_potion = new MarijuanaEffect(26, false, 0);
        cocaine_potion = new CocaineEffect(27, false, 0);
        MinecraftForge.EVENT_BUS.register(x_ray_potion);
        MinecraftForge.EVENT_BUS.register(marijuana_potion);
        MinecraftForge.EVENT_BUS.register(cocaine_potion);

        BucketHandler.INSTANCE.buckets.put(MLBlocks.ammonia, MLItems.ammonia);
        BucketHandler.INSTANCE.buckets.put(MLBlocks.sulfuric_acid, MLItems.sulfuric_acid);
        BucketHandler.INSTANCE.buckets.put(MLBlocks.potassium_permanganate, MLItems.potassium_permanganate);

        // register recipes
        GameRegistry.addShapelessRecipe(new ItemStack(MLItems.calcium_hydroxide), Items.water_bucket, MLItems.calcium_oxide);
        GameRegistry.addSmelting(Item.getItemFromBlock(MLBlocks.limestone), new ItemStack(MLItems.calcium_oxide), 0.3F);
        GameRegistry.addShapelessRecipe(new ItemStack(MLItems.cannabis_shredded), MLItems.cannabis_buds, MLItems.grinder);
        GameRegistry.addShapelessRecipe(new ItemStack(MLItems.coca_leaf_shredded), MLItems.grinder, new ItemStack(MLItems.coca_leaf, 1, 0));
        GameRegistry.addShapelessRecipe(new ItemStack(MLItems.joint), MLItems.cannabis_shredded, Items.paper);
        // register grinder recipes
        GameRegistry.addShapedRecipe(new ItemStack(MLItems.grinder), "AAA", "BBB", "AAA", 'A', Item.getItemFromBlock(Blocks.planks), 'B', Items.iron_ingot);
        for (int i = 0; i < ItemDye.field_150921_b.length; i++)
            GameRegistry.addShapelessRecipe(new ItemStack(MLItems.grinder, 1, i), MLItems.grinder, new ItemStack(Items.dye, 1, i));
        // continue with other recipes
        GameRegistry.addShapelessRecipe(new ItemStack(MLItems.potassium_hydroxide), Item.getItemFromBlock(MLBlocks.potash), MLItems.calcium_hydroxide);
        GameRegistry.addShapelessRecipe(new ItemStack(MLItems.potassium_permanganate), MLItems.sulfuric_acid, MLItems.potassium_manganate);
        GameRegistry.addSmelting(Items.water_bucket, new ItemStack(MLItems.salt), 1F);
        GameRegistry.addSmelting(MLItems.sulfur, new ItemStack(MLItems.sulfuric_acid), 0.3F);
        GameRegistry.addSmelting(MLItems.potassium_hydroxide_pyrolusite_mixture, new ItemStack(MLItems.potassium_manganate), 0.3F);
        GameRegistry.addSmelting(MLItems.potassium_hydroxide_pyrolusite_mixture, new ItemStack(MLItems.potassium_manganate), 0.3F);
        GameRegistry.addSmelting(MLItems.waxy_cocaine, new ItemStack(MLItems.heated_cocaine), 0.3F);
        GameRegistry.addShapelessRecipe(new ItemStack(MLItems.potassium_hydroxide_pyrolusite_mixture), MLItems.potassium_hydroxide, MLItems.pyrolusite);

        TileEntityCementMixer.add_recipe(Recipe.build(new ItemStack(MLItems.waxy_cocaine), new ItemStack(MLItems.coca_leaf_shredded), new ItemStack(MLItems.lime), new ItemStack(MLItems.salt)).addLiquids(new FluidStack(BuildCraftEnergy.fluidFuel, 100)));
        TileEntityCementMixer.add_recipe(Recipe.build(new ItemStack(MLItems.cocaine_sulfate), new ItemStack(MLItems.pressed_cocaine)).addLiquids(new FluidStack(MLBlocks.sulfuric_acid.getFluid(), 100)));
        TileEntityCementMixer.add_recipe(Recipe.build(new ItemStack(MLItems.purple_cocaine), new ItemStack(MLItems.coca_paste)).addLiquids(new FluidStack(MLBlocks.potassium_permanganate.getFluid(), 100)));
        TileEntityCementMixer.add_recipe(Recipe.build(new ItemStack(MLItems.processed_cocaine), new ItemStack(MLItems.coca_paste)).addLiquids(new FluidStack(MLBlocks.ammonia.getFluid(), 100)));
    }

    @Override
    public void init(FMLInitializationEvent event) {
        FluidContainerRegistry.registerFluidContainer(FluidRegistry.getFluidStack("ammonia", 1000), new ItemStack(MLItems.ammonia), new ItemStack(Items.bucket));
        FluidContainerRegistry.registerFluidContainer(FluidRegistry.getFluidStack("potassium_permanganate", 1000), new ItemStack(MLItems.potassium_permanganate), new ItemStack(Items.bucket));
        FluidContainerRegistry.registerFluidContainer(FluidRegistry.getFluidStack("sulfuric_acid", 1000), new ItemStack(MLItems.sulfuric_acid), new ItemStack(Items.bucket));
    }

    @Override
    public Class<? extends CommonProxy> getClientProxyClass() {
        return com.minelife.drug.client.ClientProxy.class;
    }

    @Override
    public Class<? extends CommonProxy> getServerProxyClass() {
        return com.minelife.drug.server.ServerProxy.class;
    }

    @Override
    public AbstractGuiHandler gui_handler() {
        return new DrugsGuiHandler();
    }

    @Override
    public void textureHook(TextureStitchEvent.Post event) {
        if (event.map.getTextureType() == 0) {
            BlockAmmonia.fluid().setIcons(MLBlocks.ammonia.getBlockTextureFromSide(1), MLBlocks.ammonia.getBlockTextureFromSide(2));
            BlockSulfuricAcid.fluid().setIcons(MLBlocks.sulfuric_acid.getBlockTextureFromSide(1), MLBlocks.sulfuric_acid.getBlockTextureFromSide(2));
            BlockPotassiumPermanganate.fluid().setIcons(MLBlocks.potassium_permanganate.getBlockTextureFromSide(1), MLBlocks.potassium_permanganate.getBlockTextureFromSide(2));
        }
    }

    @SideOnly(Side.SERVER)
    public static boolean check_for_marijuana(EntityPlayer player) {
//        if (!player.getEntityData().hasKey("marijuana")) return false;
//
//        try {
//            Date last_use = ItemDrugTest.df.parse(player.getEntityData().getString("marijuana"));
//            int days_since_last_use = convert_to_mc_days(Calendar.getInstance().getTime(), last_use);
//            return days_since_last_use < days_in_minecraft;
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }

        return player.isPotionActive(marijuana_potion);
    }

    @SideOnly(Side.SERVER)
    public static boolean check_for_cocaine(EntityPlayer player) {
//        if (!player.getEntityData().hasKey("cocaine")) return false;
//
//        try {
//            Date last_use = ItemDrugTest.df.parse(player.getEntityData().getString("cocaine"));
//            int days_since_last_use = convert_to_mc_days(Calendar.getInstance().getTime(), last_use);
//            return days_since_last_use < days_in_minecraft;
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }

        return player.isPotionActive(cocaine_potion);
    }

    public static int convert_to_mc_days(Date later, Date earlier) {
        // gets the minute difference between the two dates
        long result = ((earlier.getTime() / 60000) - (later.getTime() / 60000));
        return (int) result / 20;
    }
}
