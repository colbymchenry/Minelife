package com.minelife.drug.block;

import buildcraft.core.lib.block.BlockBuildCraft;
import com.minelife.Minelife;
import com.minelife.drug.tileentity.TileEntityAmmoniaExtractor;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockAmmoniaExtractor extends BlockBuildCraft {

    private static BlockAmmoniaExtractor instance;
    private IIcon icon;

    private BlockAmmoniaExtractor()
    {
        super(Material.iron);
        setCreativeTab(CreativeTabs.tabRedstone);
        setBlockTextureName(Minelife.MOD_ID + ":ammonia_extractor");
    }

    public static BlockAmmoniaExtractor instance()
    {
        if (instance == null) instance = new BlockAmmoniaExtractor();
        return instance;
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ)
    {
        TileEntityAmmoniaExtractor tile = (TileEntityAmmoniaExtractor) world.getTileEntity(x, y, z);
        tile.onBlockActivated(player, ForgeDirection.getOrientation(side));
        return true;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta)
    {
        return new TileEntityAmmoniaExtractor();
    }

    @Override
    public void registerBlockIcons(IIconRegister iconRegister)
    {
        icon = iconRegister.registerIcon(Minelife.MOD_ID + ":ammonia_extractor");
    }

    @Override
    public IIcon getIcon(int p_149691_1_, int p_149691_2_)
    {
        return icon;
    }

}