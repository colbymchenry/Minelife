package com.minelife.drug.block;

import com.minelife.Minelife;
import com.minelife.drug.DrugsGuiHandler;
import com.minelife.drug.tileentity.TileEntityDryingRack;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class BlockDryingRack extends BlockContainer {

    private static BlockDryingRack instance;
    private IIcon icon;

    private BlockDryingRack()
    {
        super(Material.wood);
    }

    public static BlockDryingRack instance() {
        if(instance == null) instance = new BlockDryingRack();
        return instance;
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float f, float f1, float f2)
    {
        if(world.isRemote) return true;

        player.openGui(Minelife.instance, DrugsGuiHandler.drying_rack_id, world, x, y, z);
        return true;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta)
    {
        return new TileEntityDryingRack();
    }

    @Override
    public void registerBlockIcons(IIconRegister iconRegister) {
        icon = iconRegister.registerIcon(Minelife.MOD_ID + ":drying_rack");
    }

    @Override
    public IIcon getIcon(int p_149691_1_, int p_149691_2_) {
        return icon;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getRenderType() {
        return -1;
    }
}
