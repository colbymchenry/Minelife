package com.minelife.drug.item;

import buildcraft.energy.ItemBucketBuildcraft;
import com.minelife.Minelife;
import com.minelife.drug.block.BlockSulfuricAcid;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;

public class ItemSulfuricAcid extends ItemBucketBuildcraft {

    private static ItemSulfuricAcid instance;

    private ItemSulfuricAcid()
    {
        super(BlockSulfuricAcid.instance());
        setCreativeTab(CreativeTabs.tabBrewing);
        setUnlocalizedName("sulfuric_acid");
        setContainerItem(Items.bucket);
        setTextureName(Minelife.MOD_ID + ":sulfuric_acid");
        FluidContainerRegistry.registerFluidContainer(FluidRegistry.getFluidStack("sulfuric_acid", 1000), new ItemStack(this), new ItemStack(Items.bucket));
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister par1IconRegister) {
        this.itemIcon = par1IconRegister.registerIcon(Minelife.MOD_ID + ":sulfuric_acid");
    }

    public static ItemSulfuricAcid instance() {
        if(instance == null) instance = new ItemSulfuricAcid();
        return instance;
    }

    public static void register_recipe() {
        GameRegistry.addSmelting(ItemSulfur.instance(), new ItemStack(instance()), 0.3F);
    }


}
