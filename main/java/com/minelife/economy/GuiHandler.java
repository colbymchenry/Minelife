package com.minelife.economy;

import com.minelife.AbstractGuiHandler;
import com.minelife.economy.client.gui.cash.ContainerCashBlock;
import com.minelife.economy.client.gui.cash.GuiCashBlock;
import com.minelife.economy.client.gui.wallet.ContainerWallet;
import com.minelife.economy.client.gui.wallet.GuiWallet;
import com.minelife.economy.client.gui.wallet.InventoryWallet;
import com.minelife.economy.tileentity.TileEntityCash;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class GuiHandler extends AbstractGuiHandler {

    public static int CASH_BLOCK_ID = 9876;
    public static int WALLET_ID = 9877;

    @Nullable
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if (ID == CASH_BLOCK_ID && world.getTileEntity(new BlockPos(x, y, z)) instanceof TileEntityCash) {
            TileEntityCash tile = (TileEntityCash) world.getTileEntity(new BlockPos(x, y, z));
            return new ContainerCashBlock(player.inventory, tile.getInventory(), tile, player);
        }
        if (ID == WALLET_ID && player.getHeldItem(EnumHand.MAIN_HAND) != null &&
                player.getHeldItem(EnumHand.MAIN_HAND).getItem() == ModEconomy.itemWallet) {
            return new ContainerWallet(player.inventory,  new InventoryWallet(player.getHeldItem(EnumHand.MAIN_HAND)), player);
        }
            return null;
    }

    @Nullable
    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if (ID == CASH_BLOCK_ID && world.getTileEntity(new BlockPos(x, y, z)) instanceof TileEntityCash) {
            TileEntityCash tile = (TileEntityCash) world.getTileEntity(new BlockPos(x, y, z));
            return new GuiCashBlock(player.inventory, tile.getInventory(), tile, player);
        }
        if (ID == WALLET_ID && player.getHeldItem(EnumHand.MAIN_HAND) != null &&
                player.getHeldItem(EnumHand.MAIN_HAND).getItem() == ModEconomy.itemWallet) {
            return new GuiWallet(player.inventory, new InventoryWallet(player.getHeldItem(EnumHand.MAIN_HAND)), player);
        }
        return null;
    }
}
