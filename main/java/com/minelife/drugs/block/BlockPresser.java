package com.minelife.drugs.block;

import buildcraft.lib.block.BlockBCTile_Neptune;
import buildcraft.lib.tile.TileBC_Neptune;
import com.minelife.Minelife;
import com.minelife.drugs.DrugsGuiHandler;
import com.minelife.drugs.tileentity.TileEntityPresser;
import com.minelife.drugs.tileentity.TileEntityVacuum;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public class BlockPresser extends BlockBCTile_Neptune {

    public BlockPresser() {
        super(Material.IRON, null);
        setRegistryName(Minelife.MOD_ID, "presser");
        setUnlocalizedName(Minelife.MOD_ID + ":presser");
        setHardness(3);
        setResistance(15);
        setCreativeTab(CreativeTabs.MISC);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if(worldIn.isRemote) return false;
        playerIn.openGui(Minelife.getInstance(), DrugsGuiHandler.PRESSER_GUI, worldIn, pos.getX(), pos.getY(), pos.getZ());
        return false;
    }

    public EnumBlockRenderType getRenderType(IBlockState state)
    {
        return EnumBlockRenderType.MODEL;
    }

    @SideOnly(Side.CLIENT)
    public void registerModel(ItemModelMesher mesher) {
        Item item = Item.getItemFromBlock(this);
        ModelResourceLocation model = new ModelResourceLocation(Minelife.MOD_ID + ":presser", "inventory");
        ModelLoader.registerItemVariants(item, model);
        mesher.register(item, 0, model);
    }

    @Nullable
    @Override
    public TileBC_Neptune createTileEntity(World world, IBlockState iBlockState) {
        return new TileEntityPresser();
    }
}
