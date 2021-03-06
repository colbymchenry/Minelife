package com.minelife.chestshop;

import codechicken.lib.model.ModelRegistryHelper;
import com.minelife.Minelife;
import com.minelife.chestshop.client.gui.GuiBuyShop;
import com.minelife.chestshop.client.gui.GuiSetupShop;
import com.minelife.chestshop.client.render.RenderChestShopBlock;
import com.minelife.chestshop.client.render.RenderChestShopItem;
import com.minelife.economy.ModEconomy;
import com.minelife.permission.ModPermission;
import com.minelife.util.ItemHelper;
import com.minelife.util.NumberConversions;
import com.minelife.util.PlayerHelper;
import com.minelife.util.client.MLParticleDigging;
import com.minelife.util.client.PacketPopup;
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
import net.minecraft.init.Items;
import net.minecraft.item.Item;
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
import net.minecraftforge.fml.client.registry.ClientRegistry;
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

        if(!worldIn.isRemote) {
            if (playerIn.getHeldItem(hand).getItem() == Items.BLAZE_ROD && ModPermission.hasPermission(playerIn.getUniqueID(), "shop.servershop")) {
                boolean isServerShop = tile.isServerShop();
                tile.setServerShop(!isServerShop);
                playerIn.sendMessage(new TextComponentString("SERVER SHOP: " + !isServerShop));
                tile.sendUpdates();
                return true;
            }

            if (playerIn.getHeldItem(hand).getItem() == Items.BLAZE_POWDER && ModPermission.hasPermission(playerIn.getUniqueID(), "shop.servershop")) {
                boolean isSellingShop = tile.isSellingShop();
                tile.setSellingShop(!isSellingShop);
                playerIn.sendMessage(new TextComponentString("SELLING SHOP: " + !isSellingShop));
                tile.sendUpdates();
                return true;
            }
        }

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

                if(!tile.isSellingShop()) {
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

                    if (!tile.canPurchaseFit((EntityPlayerMP) playerIn, 1)) {
                        playerIn.sendMessage(new TextComponentString(TextFormatting.LIGHT_PURPLE + "[Shop] " + TextFormatting.GOLD + "Insufficient inventory space."));
                        return false;
                    }

                    tile.doPurchase((EntityPlayerMP) playerIn, 1);
                    int didNotFitCash = ModEconomy.depositCashPiles(tile.getOwner(), tile.getPrice());
                    int didNotFitInv = ModEconomy.withdrawInventory((EntityPlayerMP) playerIn, tile.getPrice());

                    if (didNotFitCash > 0) ModEconomy.depositATM(tile.getOwner(), didNotFitCash, true);
                    if (didNotFitInv > 0) {
                        ModEconomy.depositATM(playerIn.getUniqueID(), didNotFitInv, true);
                        playerIn.sendMessage(new TextComponentString(TextFormatting.LIGHT_PURPLE + "[Shop] " + TextFormatting.GOLD + "$" + NumberConversions.format(didNotFitInv) + " did not fit in your inventory and was deposited into your ATM."));
                        return true;
                    }
                } else {
                    if (tile.getItem() == null) {
                        playerIn.sendMessage(new TextComponentString(TextFormatting.LIGHT_PURPLE + "[Shop] " + TextFormatting.GOLD + "Item not set."));
                        return false;
                    }


                    if (ItemHelper.amountInInventory((EntityPlayerMP) playerIn, tile.getItem()) < tile.getItem().getCount()) {
                        playerIn.sendMessage(new TextComponentString(TextFormatting.LIGHT_PURPLE + "[Shop] " + TextFormatting.GOLD + "You do not have enough of that item"));
                        return false;
                    }

                    ItemHelper.removeFromPlayerInventory((EntityPlayerMP) playerIn, tile.getItem(), tile.getItem().getCount());
                    playerIn.inventoryContainer.detectAndSendChanges();

                    int didNotFitInv = ModEconomy.depositInventory((EntityPlayerMP) playerIn, tile.getPrice());

                    if (didNotFitInv > 0) {
                        ModEconomy.depositATM(playerIn.getUniqueID(), didNotFitInv, true);
                        playerIn.sendMessage(new TextComponentString(TextFormatting.LIGHT_PURPLE + "[Shop] " + TextFormatting.GOLD + "$" + NumberConversions.format(didNotFitInv) + " did not fit in your inventory and was deposited into your ATM."));
                        return true;
                    }


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
