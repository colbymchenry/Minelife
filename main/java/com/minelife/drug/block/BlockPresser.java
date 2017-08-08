package com.minelife.drug.block;

import buildcraft.core.lib.block.BlockBuildCraft;
import com.minelife.Minelife;
import com.minelife.drug.ModDrugs;
import com.minelife.drug.tileentity.TileEntityCementMixer;
import com.minelife.drug.tileentity.TileEntityPresser;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class BlockPresser extends BlockBuildCraft {

    private IIcon icon;

    public BlockPresser()
    {
        super(Material.iron);
        setCreativeTab(ModDrugs.tab_drugs);
        GameRegistry.registerTileEntity(TileEntityPresser.class, "presser");
        setBlockName("presser");
        setBlockTextureName(Minelife.MOD_ID + ":presser");
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
        TileEntityPresser tile = (TileEntityPresser) world.getTileEntity(x, y, z);
        tile.onBlockActivated(player);
        return true;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta)
    {
        return new TileEntityPresser();
    }

    @Override
    public void registerBlockIcons(IIconRegister iconRegister) {
        icon = iconRegister.registerIcon(Minelife.MOD_ID + ":presser");
    }

    @Override
    public IIcon getIcon(int p_149691_1_, int p_149691_2_) {
        return icon;
    }

}