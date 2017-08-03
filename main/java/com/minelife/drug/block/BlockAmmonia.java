package com.minelife.drug.block;

import buildcraft.core.lib.block.BlockBuildCraftFluid;
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

public class BlockAmmonia extends BlockBuildCraftFluid {

    private static BlockAmmonia instance;
    private static Fluid fluid;

    private BlockAmmonia()
    {
        super(fluid, Material.water, MapColor.greenColor);
        setParticleColor(0.7F, 0.7F, 0.0F);
        setBlockName("ammonia").setLightOpacity(3);
        setBlockTextureName(Minelife.MOD_ID + ":ammonia_still");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        this.theIcon = new IIcon[]{iconRegister.registerIcon(Minelife.MOD_ID + ":ammonia_still"), iconRegister.registerIcon(Minelife.MOD_ID + ":ammonia_flow")};
    }

    public static BlockAmmonia instance()
    {
        if (instance == null) instance = new BlockAmmonia();
        return instance;
    }

    public static void register_fluid() {
        FluidRegistry.registerFluid(fluid = new Fluid("ammonia"));
    }

    public static Fluid fluid() {
        return fluid;
    }

}
