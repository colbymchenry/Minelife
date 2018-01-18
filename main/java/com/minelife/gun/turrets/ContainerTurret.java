package com.minelife.gun.turrets;

import buildcraft.core.lib.gui.BuildCraftContainer;
import com.minelife.economy.ItemWallet;
import com.minelife.economy.client.wallet.InventoryWallet;
import com.minelife.economy.client.wallet.SlotWallet;
import journeymap.client.render.map.Tile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerTurret extends BuildCraftContainer {

    private InventoryPlayer PlayerInventory;
    private TileEntityTurret TileTurret;

    public ContainerTurret(InventoryPlayer PlayerInventory, TileEntityTurret TileTurret) {
        super(TileTurret.getSizeInventory());
        this.PlayerInventory = PlayerInventory;
        this.TileTurret = TileTurret;

        int numRows = TileTurret.getSizeInventory() / 9;
        int i = (numRows - 4) * 18;
        int j;
        int k;

        for (j = 0; j < numRows; ++j)
        {
            for (k = 0; k < 9; ++k)
            {
                this.addSlotToContainer(new SlotTurret(TileTurret, k + j * 9, 8 + k * 18, 18 + j * 18));
            }
        }

        for (j = 0; j < 3; ++j)
        {
            for (k = 0; k < 9; ++k)
            {
                this.addSlotToContainer(new Slot(PlayerInventory, k + j * 9 + 9, 8 + k * 18, 103 + j * 18 + i));
            }
        }

        for (j = 0; j < 9; ++j)
        {
            this.addSlotToContainer(new Slot(PlayerInventory, j, 8 + j * 18, 161 + i));
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer p_75145_1_) {
        return true;
    }

    @Override
    protected boolean mergeItemStack(ItemStack p_75135_1_, int p_75135_2_, int p_75135_3_, boolean p_75135_4_) {
        boolean value = super.mergeItemStack(p_75135_1_, p_75135_2_, p_75135_3_, p_75135_4_);
        TileTurret.Sync();
        return value;
    }

    @Override
    public void onContainerClosed(EntityPlayer player) {
        TileTurret.Sync();
    }
}
