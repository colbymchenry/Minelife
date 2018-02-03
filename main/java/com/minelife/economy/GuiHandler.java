package com.minelife.economy;

import com.minelife.AbstractGuiHandler;
import com.minelife.MLItems;
import com.minelife.economy.cash.ContainerBlockCash;
import com.minelife.economy.cash.GuiBlockCash;
import com.minelife.economy.cash.TileEntityCash;
import com.minelife.economy.client.wallet.ContainerWallet;
import com.minelife.economy.client.wallet.GuiWallet;
import com.minelife.economy.client.wallet.InventoryWallet;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class GuiHandler extends AbstractGuiHandler {


    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if (ID == 80098) {
            return new ContainerWallet(player.inventory, new InventoryWallet(player.getHeldItem()));
        }
        if (ID == 80099) {
            if (world.getTileEntity(x, y, z) != null && world.getTileEntity(x, y, z) instanceof TileEntityCash)
                return new ContainerBlockCash(player.inventory, (TileEntityCash) world.getTileEntity(x, y, z));
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if (ID == 80098) {
            return new GuiWallet(player.inventory, new InventoryWallet(player.getHeldItem()));
        }
        if (ID == 80099) {
            if (world.getTileEntity(x, y, z) != null && world.getTileEntity(x, y, z) instanceof TileEntityCash)
                return new GuiBlockCash(player.inventory, (TileEntityCash) world.getTileEntity(x, y, z));
        }
        return null;
    }
}
