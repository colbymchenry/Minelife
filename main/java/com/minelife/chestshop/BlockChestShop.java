package com.minelife.chestshop;

import com.minelife.Minelife;
import com.minelife.chestshop.client.gui.GuiBuyShop;
import com.minelife.chestshop.client.gui.GuiSetupShop;
import com.minelife.economy.ModEconomy;
import com.minelife.util.NumberConversions;
import com.minelife.util.client.MLParticleDigging;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.Objects;

public class BlockChestShop extends BlockContainer {

    public BlockChestShop() {
        super(Material.IRON);
        setRegistryName(Minelife.MOD_ID, "chest_shop");
        setUnlocalizedName(Minelife.MOD_ID + ":chest_shop");
        setCreativeTab(CreativeTabs.MISC);
        setHardness(10F);
        setSoundType(SoundType.METAL);
    }

    @SideOnly(Side.CLIENT)
    private void openSetupGui(TileEntityChestShop tile) {
        Minecraft.getMinecraft().displayGuiScreen(new GuiSetupShop(tile));
    }

    @SideOnly(Side.CLIENT)
    private void openBuyGui(TileEntityChestShop tile) {
        Minecraft.getMinecraft().displayGuiScreen(new GuiBuyShop(tile));
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
       if(hand != EnumHand.MAIN_HAND) return super.onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);

        TileEntityChestShop tile = (TileEntityChestShop) worldIn.getTileEntity(pos);

        if (Objects.equals(tile.getOwner(), playerIn.getUniqueID())) {
            if (worldIn.isRemote) {
                this.openSetupGui(tile);
                return false;
            }
        } else {
            if(tile.getItem() == null) return false;

            if (playerIn.isSneaking()) {
                if (worldIn.isRemote) this.openBuyGui(tile);
                return false;
            }

            if (!worldIn.isRemote) {
                int balance = ModEconomy.getBalanceInventory((EntityPlayerMP) playerIn);

                if (tile.getItem() == null) {
                    playerIn.sendMessage(new TextComponentString(TextFormatting.LIGHT_PURPLE + "[Shop] " + TextFormatting.GOLD + "Item not set."));
                    return false;
                }

                if (balance < tile.getPrice()) {
                    playerIn.sendMessage(new TextComponentString(TextFormatting.LIGHT_PURPLE + "[Shop] " + TextFormatting.GOLD + "Insufficient funds in inventory."));
                    return false;
                }

                if (tile.getStockCount() < tile.getItem().getCount()) {
                    playerIn.sendMessage(new TextComponentString(TextFormatting.LIGHT_PURPLE + "[Shop] " + TextFormatting.GOLD + "Out of stock."));
                    return false;
                }

                tile.doPurchase((EntityPlayerMP) playerIn, 1);
                int didNotFitCash = ModEconomy.depositCashPiles(tile.getOwner(), tile.getPrice());
                int didNotFitInv = ModEconomy.withdrawInventory((EntityPlayerMP) playerIn, tile.getPrice());

                if(didNotFitCash > 0) ModEconomy.depositATM(tile.getOwner(), didNotFitCash);
                if(didNotFitInv > 0){
                    ModEconomy.depositATM(playerIn.getUniqueID(), didNotFitInv);
                    playerIn.sendMessage(new TextComponentString(TextFormatting.LIGHT_PURPLE + "[Shop] " + TextFormatting.GOLD + "$" + NumberConversions.format(didNotFitInv) + " did not fit in your inventory and was deposited into your ATM."));
                    return true;
                }
            }
        }

        return false;
    }

    @SideOnly(Side.SERVER)
    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        int l = MathHelper.floor((double) (placer.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;

        TileEntityChestShop tile = (TileEntityChestShop) worldIn.getTileEntity(pos);
        tile.setFacing(l == 0 ? EnumFacing.NORTH : l == 1 ? EnumFacing.EAST : l == 2 ? EnumFacing.SOUTH : EnumFacing.WEST);
        tile.setOwner(placer.getUniqueID());
        tile.sendUpdates();
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityChestShop();
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean addHitEffects(IBlockState state, World worldObj, RayTraceResult target, ParticleManager manager) {
        MLParticleDigging.addBreakEffect(worldObj, target.getBlockPos(), target.sideHit, manager, "minelife:textures/block/chest_shop.png");
        return true;
    }

    @Override
    public boolean addDestroyEffects(World world, BlockPos pos, ParticleManager manager) {
        MLParticleDigging.addDestroyEffect(world, pos, manager, "minelife:textures/block/chest_shop.png");
        return true;
    }

}
