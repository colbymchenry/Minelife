package com.minelife.drug.item;

import buildcraft.energy.ItemBucketBuildcraft;
import com.minelife.Minelife;
import com.minelife.drug.block.BlockAmmonia;
import com.minelife.drug.block.BlockSulfuricAcid;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
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
        super(BlockAmmonia.instance());
        setCreativeTab(CreativeTabs.tabBrewing);
        setUnlocalizedName("ammonia");
        setContainerItem(Items.bucket);
        setTextureName(Minelife.MOD_ID + ":ammonia");
        FluidContainerRegistry.registerFluidContainer(FluidRegistry.getFluidStack("ammonia", 1000), new ItemStack(this), new ItemStack(Items.bucket));
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister par1IconRegister) {
        this.itemIcon = par1IconRegister.registerIcon(Minelife.MOD_ID + ":ammonia");
    }

    public static ItemAmmonia instance()
    {
        if (instance == null) instance = new ItemAmmonia();
        return instance;
    }

}

