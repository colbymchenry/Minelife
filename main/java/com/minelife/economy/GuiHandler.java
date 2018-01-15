package com.minelife.economy;

import com.minelife.AbstractGuiHandler;
import com.minelife.MLItems;
import com.minelife.economy.client.wallet.ContainerWallet;
import com.minelife.economy.client.wallet.GuiWallet;
import com.minelife.economy.client.wallet.InventoryWallet;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class GuiHandler extends AbstractGuiHandler {


    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if(ID == 80098) {
            if(player.getHeldItem() == null || !(player.getHeldItem().getItem() instanceof ItemWallet)) return null;
            return new ContainerWallet(player.inventory, new InventoryWallet(player.getHeldItem()));
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if(ID == 80098) {
            if(player.getHeldItem() == null || !(player.getHeldItem().getItem() instanceof ItemWallet)) return null;
            return new GuiWallet(player.inventory,  new InventoryWallet(player.getHeldItem()));
        }
        return null;
    }
}
