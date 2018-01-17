package com.minelife.gun.turrets;

import com.minelife.Minelife;
import com.minelife.economy.TileEntityATM;
import com.minelife.gun.ModGun;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class BlockTurret extends BlockContainer {

    private IIcon icon;

    public BlockTurret() {
        super(Material.iron);
        setCreativeTab(ModGun.tabGuns);
        GameRegistry.registerTileEntity(TileEntityTurret.class, "turret");
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack p_149689_6_) {
        int l = MathHelper.floor_double((double) (player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
        TileEntityTurret tileEntityATM = (TileEntityTurret) world.getTileEntity(x, y, z);
        tileEntityATM.setDirection(l == 0 ? EnumFacing.NORTH : l == 1 ? EnumFacing.EAST : l == 2 ? EnumFacing.SOUTH : EnumFacing.WEST);
    }

    @Override
    public TileEntity createNewTileEntity(World World, int Meta) {
        return new TileEntityTurret();
    }

    @Override
    public void registerBlockIcons(IIconRegister iconRegister) {
        icon = iconRegister.registerIcon(Minelife.MOD_ID + ":cement_mixer");
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
