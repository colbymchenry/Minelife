package com.minelife.drug.item;

import buildcraft.energy.ItemBucketBuildcraft;
import com.minelife.Minelife;
import com.minelife.drug.block.BlockSulfuricAcid;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;

public class ItemAmmonia extends ItemBucketBuildcraft {

    private static ItemAmmonia instance;

    private ItemAmmonia()
    {
        super(BlockSulfuricAcid.instance());
        setCreativeTab(CreativeTabs.tabBrewing);
        setUnlocalizedName("sulfuric_acid");
        setContainerItem(Items.bucket);
        setTextureName(Minelife.MOD_ID + ":ammonia");
        FluidContainerRegistry.registerFluidContainer(FluidRegistry.getFluidStack("sulfuric_acid", 1000), new ItemStack(this), new ItemStack(Items.bucket));
    }


    public static ItemAmmonia instance()
    {
        if (instance == null) instance = new ItemAmmonia();
        return instance;
    }

}

