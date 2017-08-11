package com.minelife.drug.block;

import buildcraft.core.lib.block.BlockBuildCraft;
import com.minelife.Minelife;
import com.minelife.drug.ModDrugs;
import com.minelife.drug.tileentity.TileEntityPresser;
import com.minelife.drug.tileentity.TileEntityVacuum;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockVacuum extends BlockBuildCraft {

    private IIcon side, top;

    public BlockVacuum()
    {
        super(Material.iron);
        setBlockName("vacuum");
        setBlockTextureName(Minelife.MOD_ID + ":vacuum");
        setCreativeTab(ModDrugs.tab_drugs);
        GameRegistry.registerTileEntity(TileEntityVacuum.class, "vacuum");
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer entityplayer, int par6, float par7, float par8, float par9)
    {
        TileEntityVacuum tile = (TileEntityVacuum) world.getTileEntity(x, y, z);
        tile.onBlockActivated(entityplayer);
        return true;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta)
    {
        return new TileEntityVacuum();
    }

    @Override
    public void registerBlockIcons(IIconRegister iconRegister) {
        side = iconRegister.registerIcon(Minelife.MOD_ID + ":vacuum");
        top = iconRegister.registerIcon(Minelife.MOD_ID + ":vacuum_top");
    }

    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta)
    {
        return ForgeDirection.getOrientation(side) == ForgeDirection.UP || ForgeDirection.getOrientation(side) == ForgeDirection.DOWN ? this.top : this.side;
    }

    @SideOnly(Side.CLIENT)
    public IIcon getIconAbsolute(int side, int metadata) {
        return getIcon(side, metadata);
    }

}
