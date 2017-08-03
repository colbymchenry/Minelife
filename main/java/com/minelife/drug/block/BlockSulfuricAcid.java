package com.minelife.drug.block;

import buildcraft.core.lib.block.BlockBuildCraftFluid;
import buildcraft.core.lib.utils.ResourceUtils;
import buildcraft.energy.BucketHandler;
import com.minelife.Minelife;
import com.minelife.drug.item.ItemSulfuricAcid;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

public class BlockSulfuricAcid extends BlockBuildCraftFluid {

    private static BlockSulfuricAcid instance;
    private static Fluid fluid;

    private BlockSulfuricAcid()
    {
        super(fluid = new Fluid("sulfuric_acid"), Material.water, MapColor.airColor);
        setParticleColor(0.7F, 0.7F, 0.0F);
        setBlockName("sulfuric_acid").setLightOpacity(3);
        fluid.setBlock(this);
        FluidRegistry.registerFluid(fluid);
        BucketHandler.INSTANCE.buckets.put(this, ItemSulfuricAcid.instance());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        this.theIcon = new IIcon[]{iconRegister.registerIcon(Minelife.MOD_ID + ":sulfuric_acid_still"), iconRegister.registerIcon(Minelife.MOD_ID + ":sulfuric_acid_flow")};
    }

    public static BlockSulfuricAcid instance()
    {
        if (instance == null) instance = new BlockSulfuricAcid();
        return instance;
    }

    public static Fluid fluid() {
        return fluid;
    }

}