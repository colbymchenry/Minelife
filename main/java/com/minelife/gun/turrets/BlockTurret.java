package com.minelife.gun.turrets;

import com.google.common.collect.Lists;
import com.minelife.MLBlocks;
import com.minelife.Minelife;
import com.minelife.economy.TileEntityATM;
import com.minelife.gun.ModGun;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import java.util.List;

public class BlockTurret extends BlockContainer {

    private IIcon icon;
    public BlockTurretTop topTurret;

    public BlockTurret() {
        super(Material.iron);
        setCreativeTab(ModGun.tabGuns);
        setBlockName("turret");
        GameRegistry.registerTileEntity(TileEntityTurret.class, "turret");
        GameRegistry.registerBlock(topTurret = new BlockTurretTop(), "turret_top");
        setBlockBounds(0, 0, 0, 1, 2, 1);
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack p_149689_6_) {
        int l = MathHelper.floor_double((double) (player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
        TileEntityTurret tileTurret = (TileEntityTurret) world.getTileEntity(x, y, z);
        tileTurret.setDirection(l == 0 ? EnumFacing.NORTH : l == 1 ? EnumFacing.EAST : l == 2 ? EnumFacing.SOUTH : EnumFacing.WEST);
        world.setBlock(x, y + 1, z, topTurret);
        tileTurret.setOwner(player.getUniqueID());
    }

    @Override
    public boolean canPlaceBlockAt(World p_149742_1_, int p_149742_2_, int p_149742_3_, int p_149742_4_) {
        return p_149742_1_.getBlock(p_149742_2_, p_149742_3_, p_149742_4_) == Blocks.air;
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int p_149727_6_, float p_149727_7_, float p_149727_8_, float p_149727_9_) {
        if (!world.isRemote) player.openGui(Minelife.instance, 98745, world, x, y, z);
        return true;
    }

    @Override
    public void breakBlock(World p_149749_1_, int p_149749_2_, int p_149749_3_, int p_149749_4_, Block p_149749_5_, int p_149749_6_) {
        if(p_149749_1_.isRemote) return;
        p_149749_1_.setBlockToAir(p_149749_2_, p_149749_3_ + 1, p_149749_4_);
        if(p_149749_6_ != 1)
        breakBlock(p_149749_1_, p_149749_2_, p_149749_3_, p_149749_4_);
        p_149749_1_.setBlockToAir(p_149749_2_, p_149749_3_, p_149749_4_);
    }

    public static void breakBlock(World world, int x, int y, int z) {
        List<ItemStack> toDrop = Lists.newArrayList();
        if(world.getTileEntity(x, y, z) != null && world.getTileEntity(x, y, z) instanceof TileEntityTurret) {
            TileEntityTurret tileEntityTurret = (TileEntityTurret) world.getTileEntity(x, y, z);
            toDrop.addAll(tileEntityTurret.getAmmo().values());
        }

        toDrop.add(new ItemStack(Item.getItemFromBlock(MLBlocks.turret)));

        for (ItemStack itemStack : toDrop) {
            EntityItem item = new EntityItem(world);
            item.setEntityItemStack(itemStack);
            item.setPosition(x, y + 0.5, z);
            world.spawnEntityInWorld(item);
        }
    }

    @Override
    public TileEntity createNewTileEntity(World World, int Meta) {
        return new TileEntityTurret();
    }

    @Override
    public void registerBlockIcons(IIconRegister iconRegister) {
        icon = iconRegister.registerIcon(Minelife.MOD_ID + ":presser");
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
