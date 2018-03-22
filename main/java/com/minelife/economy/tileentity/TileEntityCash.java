package com.minelife.economy.tileentity;

import codechicken.lib.inventory.InventoryRange;
import codechicken.lib.inventory.InventoryUtils;
import com.google.common.collect.Lists;
import com.minelife.economy.ModEconomy;
import com.minelife.economy.WithdrawlResult;
import com.minelife.economy.item.ItemCash;
import com.minelife.realestate.Estate;
import com.minelife.realestate.ModRealEstate;
import com.minelife.util.MLInventory;
import com.minelife.util.MLTileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class TileEntityCash extends MLTileEntity {

    private MLInventory inventory = new MLInventory(54, null, 64);

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setTag("Inventory", this.inventory.writeToNBT(new NBTTagCompound()));
        return super.writeToNBT(compound);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.inventory.readFromNBT((NBTTagCompound) compound.getTag("Inventory"));
    }

    public MLInventory getInventory() {
        return this.inventory;
    }

    public int deposit(ItemStack stack) {
        return InventoryUtils.insertItem(this.getInventory(), stack, false);
    }

    public int deposit(int amount) {
        return ModEconomy.deposit(new InventoryRange(this.getInventory(), 0, this.getInventory().getSizeInventory()), amount);
    }

    public WithdrawlResult withdraw(int amount) {
        return ModEconomy.withdraw(new InventoryRange(this.getInventory(), 0, this.getInventory().getSizeInventory()), amount);
    }

    public int getBalance() {
        int total = 0;
        for (ItemStack itemStack : inventory.getItems()) {
            if (itemStack.getItem() == ModEconomy.itemCash)
                total += ItemCash.getAmount(itemStack);
        }
        return total;
    }

    public static List<TileEntityCash> getCashPiles(UUID playerID) {
        List<TileEntityCash> list = Lists.newArrayList();
        ResultSet result;
        for (Estate estate : ModRealEstate.getEstates(playerID)) {
            try {
                result = ModEconomy.getDatabase().query("SELECT * FROM cashpiles WHERE dimension='" + estate.getWorld().provider.getDimension() + "' " +
                        "AND x >= '" + estate.getMinimum().getX() + "' AND y >= '" + estate.getMinimum().getY() + "' " +
                        "AND z >= '" + estate.getMinimum().getZ() + "' AND x <= '" + estate.getMaximum().getX() + "' " +
                        "AND y <= '" + estate.getMaximum().getY() + "' AND z <= '" + estate.getMaximum().getZ() + "'");

                while (result.next()) {
                    TileEntity tile = estate.getWorld().getTileEntity(new BlockPos(result.getInt("x"),
                            result.getInt("y"), result.getInt("z")));

                    if (tile != null && tile instanceof TileEntityCash) {
                        list.add((TileEntityCash) tile);
                    } else {
                        ModEconomy.getDatabase().query("DELETE FROM cashpiles WHERE dimension='" + result.getInt("dimension") + "' " +
                                "AND x='" + result.getInt("x") + "' AND y='" + result.getInt("y") + "' " +
                                "AND z='" + result.getInt("z") + "'");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return list;
    }
}
