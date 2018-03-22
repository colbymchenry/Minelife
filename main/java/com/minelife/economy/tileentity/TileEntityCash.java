package com.minelife.economy.tileentity;

import codechicken.lib.inventory.InventoryRange;
import codechicken.lib.inventory.InventoryUtils;
import com.google.common.collect.Lists;
import com.minelife.economy.ModEconomy;
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
        InventoryRange inventoryRange = new InventoryRange(this.inventory, 0, this.inventory.getSizeInventory());

        int hundreds = amount / 100;
        if (hundreds > 0) {
            amount -= 100 * hundreds;
            int didNotFit = InventoryUtils.insertItem(inventoryRange, new ItemStack(ModEconomy.itemCash, hundreds, 5), false);
            amount += 100 * didNotFit;
        }

        int fifties = amount / 50;
        if (fifties > 0) {
            amount -= 50 * fifties;
            int didNotFit = InventoryUtils.insertItem(inventoryRange, new ItemStack(ModEconomy.itemCash, fifties, 4), false);
            amount += 50 * didNotFit;
        }

        int twenties = amount / 20;
        if (twenties > 0) {
            amount -= 20 * twenties;
            int didNotFit = InventoryUtils.insertItem(inventoryRange, new ItemStack(ModEconomy.itemCash, twenties, 3), false);
            amount += 20 * didNotFit;
        }

        int tens = amount / 10;
        if (tens > 0) {
            amount -= 10 * tens;
            int didNotFit = InventoryUtils.insertItem(inventoryRange, new ItemStack(ModEconomy.itemCash, tens, 2), false);
            amount += 10 * didNotFit;
        }

        int fives = amount / 5;
        if (fives > 0) {
            amount -= 5 * fives;
            int didNotFit = InventoryUtils.insertItem(inventoryRange, new ItemStack(ModEconomy.itemCash, fives, 1), false);
            amount += 5 * didNotFit;
        }

        int ones = (amount);
        if (ones > 0) {
            amount -= ones;
            int didNotFit = InventoryUtils.insertItem(inventoryRange, new ItemStack(ModEconomy.itemCash, ones, 0), false);
            amount += didNotFit;
        }

        return amount;
    }

    public List<ItemStack> withdraw(int amount) {
        List<ItemStack> cashItems = Lists.newArrayList();
        List<Integer> emptySlots = Lists.newArrayList();

        for (int i = 0; i < this.inventory.getSizeInventory(); i++) {
            ItemStack itemStack = this.inventory.getStackInSlot(i);
            if (itemStack.getItem() == ModEconomy.itemCash) {
                amount -= ItemCash.getAmount(itemStack);
                emptySlots.add(i);
                cashItems.add(itemStack);
                if (amount < 1) break;
            }
        }

        for (Integer emptySlot : emptySlots) inventory.setInventorySlotContents(emptySlot, ItemStack.EMPTY);

        if (amount < 0) {
            int addBack = Math.abs(amount);

            int hundreds = addBack / 100;
            if (hundreds > 0) {
                addBack -= 100 * hundreds;
                deposit(new ItemStack(ModEconomy.itemCash, hundreds, 5));
            }

            int fifties = addBack / 50;
            if (fifties > 0) {
                addBack -= 50 * fifties;
                deposit(new ItemStack(ModEconomy.itemCash, fifties, 4));
            }

            int twenties = addBack / 20;
            if (twenties > 0) {
                addBack -= 20 * twenties;
                deposit(new ItemStack(ModEconomy.itemCash, twenties, 3));
            }

            int tens = addBack / 10;
            if (tens > 0) {
                addBack -= 10 * tens;
                deposit(new ItemStack(ModEconomy.itemCash, tens, 2));
            }

            int fives = addBack / 5;
            if (fives > 0) {
                addBack -= 5 * fives;
                deposit(new ItemStack(ModEconomy.itemCash, fives, 1));
            }

            int ones = addBack;
            if (ones > 0) {
                addBack -= ones;
                deposit(new ItemStack(ModEconomy.itemCash, ones, 0));
            }
        }

        return cashItems;
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
