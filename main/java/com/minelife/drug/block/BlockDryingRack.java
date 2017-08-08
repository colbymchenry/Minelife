package com.minelife.drug.block;

import com.minelife.Minelife;
import com.minelife.drug.DrugsGuiHandler;
import com.minelife.drug.ModDrugs;
import com.minelife.drug.tileentity.TileEntityDryingRack;
import com.minelife.util.ArrayUtil;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import java.util.ArrayList;

public class BlockDryingRack extends BlockContainer {

    private IIcon icon;

    public BlockDryingRack()
    {
        super(Material.wood);
        setBlockName("drying_rack");
        setBlockTextureName(Minelife.MOD_ID + ":drying_rack");
        setCreativeTab(ModDrugs.tab_drugs);
        GameRegistry.registerTileEntity(TileEntityDryingRack.class, "drying_rack");
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

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta)
    {
        TileEntityDryingRack tile = (TileEntityDryingRack) world.getTileEntity(x, y, z);

        for (ItemStack item : tile.get_leaves().values()) {
            if (item != null) {
                float f = world.rand.nextFloat() * 0.8F + 0.1F;
                float f1 = world.rand.nextFloat() * 0.8F + 0.1F;
                EntityItem entityitem;

                for (float f2 = world.rand.nextFloat() * 0.8F + 0.1F; item.stackSize > 0; world.spawnEntityInWorld(entityitem)) {
                    int j1 = world.rand.nextInt(21) + 10;

                    if (j1 > item.stackSize) {
                        j1 = item.stackSize;
                    }

                    item.stackSize -= j1;
                    entityitem = new EntityItem(world, (double) ((float) x + f), (double) ((float) y + f1), (double) ((float) z + f2), new ItemStack(item.getItem(), j1, item.getItemDamage()));
                    float f3 = 0.05F;
                    entityitem.motionX = (double) ((float) world.rand.nextGaussian() * f3);
                    entityitem.motionY = (double) ((float) world.rand.nextGaussian() * f3 + 0.2F);
                    entityitem.motionZ = (double) ((float) world.rand.nextGaussian() * f3);

                    if (item.hasTagCompound()) {
                        entityitem.getEntityItem().setTagCompound((NBTTagCompound) item.getTagCompound().copy());
                    }
                }
            }
        }
    }
}
