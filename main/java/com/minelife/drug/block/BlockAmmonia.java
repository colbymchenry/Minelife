package com.minelife.drug.block;

import buildcraft.BuildCraftEnergy;
import buildcraft.core.lib.block.BlockBuildCraftFluid;
import buildcraft.core.recipes.RefineryRecipeManager;
import buildcraft.energy.BucketHandler;
import com.minelife.Minelife;
import com.minelife.drug.item.ItemAmmonia;
import com.minelife.drug.item.ItemSulfuricAcid;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public class BlockAmmonia extends BlockBuildCraftFluid {

    private static Fluid fluid = new Fluid("ammonia");

    public BlockAmmonia()
    {
        super(fluid, Material.water, MapColor.greenColor);
        setParticleColor(0.7F, 0.7F, 0.0F);
        setBlockName("ammonia").setLightOpacity(3);
        setBlockTextureName(Minelife.MOD_ID + ":ammonia_still");
        RefineryRecipeManager.INSTANCE.addRecipe(Minelife.MOD_ID + ":ammonia", new FluidStack(BuildCraftEnergy.fluidFuel, 1), new FluidStack(BlockAmmonia.fluid(), 1), 120, 1);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        this.theIcon = new IIcon[]{iconRegister.registerIcon(Minelife.MOD_ID + ":ammonia_still"), iconRegister.registerIcon(Minelife.MOD_ID + ":ammonia_flow")};
    }

    public static void register_fluid() {
        FluidRegistry.registerFluid(fluid);
    }

    public static Fluid fluid() {
        return fluid;
    }

}
