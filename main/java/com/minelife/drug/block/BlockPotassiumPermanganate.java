package com.minelife.drug.block;

import buildcraft.core.lib.block.BlockBuildCraftFluid;
import com.minelife.Minelife;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

public class BlockPotassiumPermanganate extends BlockBuildCraftFluid {

    private static Fluid fluid = new Fluid("potassium_permanganate");

    public BlockPotassiumPermanganate()
    {
        super(fluid, Material.water, MapColor.purpleColor);
        setParticleColor(0.7F, 0.7F, 0.0F);
        setBlockName("potassium_permanganate").setLightOpacity(3);
        setBlockTextureName(Minelife.MOD_ID + ":potassium_permanganate_still");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        this.theIcon = new IIcon[]{iconRegister.registerIcon(Minelife.MOD_ID + ":potassium_permanganate_still"), iconRegister.registerIcon(Minelife.MOD_ID + ":potassium_permanganate_flow")};
    }

    public static void register_fluid() {
        FluidRegistry.registerFluid(fluid);
    }

    public static Fluid fluid() {
        return fluid;
    }

}