package com.minelife.drug.item;

import buildcraft.energy.ItemBucketBuildcraft;
import com.minelife.Minelife;
import com.minelife.drug.ModDrugs;
import com.minelife.drug.block.BlockAmmonia;
import com.minelife.drug.block.BlockPotassiumPermanganate;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;

public class ItemPotassiumPermanganate extends ItemBucketBuildcraft {

    private static ItemPotassiumPermanganate instance;

    private ItemPotassiumPermanganate()
    {
        super(BlockPotassiumPermanganate.instance());
        setCreativeTab(ModDrugs.tab_drugs);
        setUnlocalizedName("potassium_permanganate");
        setContainerItem(Items.bucket);
        setTextureName(Minelife.MOD_ID + ":potassium_permanganate");
        FluidContainerRegistry.registerFluidContainer(FluidRegistry.getFluidStack("potassium_permanganate", 1000), new ItemStack(this), new ItemStack(Items.bucket));
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister par1IconRegister) {
        this.itemIcon = par1IconRegister.registerIcon(Minelife.MOD_ID + ":potassium_permanganate");
    }

    public static ItemPotassiumPermanganate instance()
    {
        if (instance == null) instance = new ItemPotassiumPermanganate();
        return instance;
    }

}