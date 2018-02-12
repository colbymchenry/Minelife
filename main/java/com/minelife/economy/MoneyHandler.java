package com.minelife.economy;

import codechicken.lib.inventory.InventoryRange;
import codechicken.lib.inventory.InventoryUtils;
import codechicken.lib.vec.Vector3;
import cofh.lib.util.helpers.InventoryHelper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.minelife.MLBlocks;
import com.minelife.MLItems;
import com.minelife.Minelife;
import com.minelife.economy.cash.TileEntityCash;
import com.minelife.economy.client.wallet.InventoryWallet;
import com.minelife.realestate.Estate;
import com.minelife.realestate.EstateHandler;
import com.minelife.util.ArrayUtil;
import com.minelife.util.NumberConversions;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dan200.computercraft.shared.util.InventoryUtil;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import org.lwjgl.Sys;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class MoneyHandler {

    // DONE
    @SideOnly(Side.SERVER)
    public static int getBalanceVault(UUID playerUUID) {
        int total = 0;
        for (TileEntityCash tileEntityCash : getCashBlocks(playerUUID)) total += tileEntityCash.getHoldings();
        return total;
    }

    // DONE
    @SideOnly(Side.SERVER)
    public static int getBalanceVault(EntityPlayerMP player) {
        return getBalanceVault(player.getUniqueID());
    }

    // DONE
    public static int getBalanceInventory(EntityPlayer player) {
        int amount = 0;
        for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
            if (player.inventory.getStackInSlot(i) != null) {
                if (player.inventory.getStackInSlot(i).getItem() instanceof ItemWallet) {
                    amount += ItemWallet.getHoldings(player.inventory.getStackInSlot(i));
                } else if (player.inventory.getStackInSlot(i).getItem() instanceof ItemMoney) {
                    amount += ((ItemMoney) player.inventory.getStackInSlot(i).getItem()).amount * player.inventory.getStackInSlot(i).stackSize;
                }
            }
        }
        return amount;
    }

    // DONE
    // TODO: May want to do this one the same way we do the addMoneyInventory
    @SideOnly(Side.SERVER)
    public static int takeMoneyInventory(EntityPlayerMP player, int amount) throws SQLException {
        int couldNotAdd = 0;

        // first try with the player's inventory
        int[] attempt = takeMoney(player.inventory, amount);
        int couldTakeAttempt = attempt[0];
        int couldNotAddAttempt = attempt[1];

        couldNotAdd += couldNotAddAttempt;
        amount -= couldTakeAttempt;

        int attemptCount = 0;
        while (couldNotAdd > 0 && attemptCount < 3) {
            // if we could not get all the money we needed from the player's inventory we will try their wallets and bags
            if (amount > 0) {
                for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
                    ItemStack stack = player.inventory.getStackInSlot(i);
                    if (stack != null && stack.getItem() instanceof ItemWallet) {
                        InventoryWallet inventoryWallet = new InventoryWallet(stack);
                        attempt = takeMoney(inventoryWallet, amount);
                        couldTakeAttempt = attempt[0];
                        couldNotAddAttempt = attempt[1];
                        couldNotAdd += couldNotAddAttempt;
                        amount -= couldTakeAttempt;
                        ItemWallet.updateItem(stack, inventoryWallet);
                        player.inventory.setInventorySlotContents(i, stack);
                        if (amount <= 0) break;
                    }
                }
            }

            // try to re-add what we could not
            if (couldNotAdd > 0) {
                for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
                    ItemStack stack = player.inventory.getStackInSlot(i);
                    if (stack != null && stack.getItem() instanceof ItemWallet) {
                        InventoryWallet inventoryWallet = new InventoryWallet(stack);
                        couldNotAdd = addMoney(inventoryWallet, couldNotAdd);
                        ItemWallet.updateItem(stack, inventoryWallet);
                        player.inventory.setInventorySlotContents(i, stack);
                        if (couldNotAdd <= 0) break;
                    }
                }
            }

            attemptCount++;
        }

//        if (couldNotAdd > 0) depositATM(player.getUniqueID(), couldNotAdd);
        return couldNotAdd;
    }

    // DONE
    @SideOnly(Side.SERVER)
    public static int addMoneyInventory(EntityPlayerMP player, int amount) throws SQLException {
        // first add to player's inventory
        Map<Integer, ItemStack> moneyStacks = getMoneyStacks(player.inventory);
        moneyStacks.forEach((slot, stack) -> player.inventory.setInventorySlotContents(slot, null));
        int toAdd = amount;
        for (ItemStack itemStack : moneyStacks.values()) toAdd += getAmount(itemStack);

        int couldNotAdd = 0;

        InventoryRange inventoryRange = new InventoryRange(player.inventory, 0, 36);

        List<ItemStack> stacksToInsert = ItemMoney.getDrops(toAdd);
        for (ItemStack stack : stacksToInsert)
            couldNotAdd += InventoryUtils.insertItem(inventoryRange, stack, false) * ((ItemMoney) stack.getItem()).amount;

        amount = couldNotAdd;
        if (couldNotAdd > 0) {
            for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
                ItemStack stack = player.inventory.getStackInSlot(i);
                if (stack != null && stack.getItem() instanceof ItemWallet) {

                    InventoryWallet inventoryWallet = new InventoryWallet(stack);
                    moneyStacks = getMoneyStacks(inventoryWallet);
                    moneyStacks.forEach((slot, s) -> inventoryWallet.setInventorySlotContents(slot, null));
                    toAdd = amount;
                    for (ItemStack itemStack : moneyStacks.values()) toAdd += getAmount(itemStack);

                    stacksToInsert = ItemMoney.getDrops(toAdd);
                    for (ItemStack s : stacksToInsert)
                        couldNotAdd = InventoryUtils.insertItem(inventoryWallet, s, false) * ((ItemMoney) s.getItem()).amount;

                    ItemWallet.updateItem(stack, inventoryWallet);
                    player.inventory.setInventorySlotContents(i, stack);

                    amount = couldNotAdd;
                    if (amount == 0) break;
                }
            }
        }

//        if(couldNotAdd > 0) depositATM(player.getUniqueID(), couldNotAdd);
        return couldNotAdd;
    }

    @SideOnly(Side.SERVER)
    public static int takeMoneyVault(EntityPlayerMP player, int amount) throws SQLException {
        return takeMoneyVault(player.getUniqueID(), amount);
    }

    @SideOnly(Side.SERVER)
    public static int addMoneyVault(EntityPlayerMP player, int amount) throws SQLException {
        return addMoneyVault(player.getUniqueID(), amount);
    }

    @SideOnly(Side.SERVER)
    public static int takeMoneyVault(UUID playerUUID, int amount) throws SQLException {
        List<TileEntityCash> cashBlocks = getCashBlocks(playerUUID);

        int couldNotAdd = 0;

        for (TileEntityCash cashBlock : cashBlocks) {
            int[] attempt = takeMoney(cashBlock.getInventory(), amount);
            int couldTakeAttempt = attempt[0];
            int couldNotAddAttempt = attempt[1];

            couldNotAdd += couldNotAddAttempt;
            amount -= couldTakeAttempt;

            cashBlock.Sync();

            if (amount <= 0) break;
        }

//        if(couldNotAdd > 0) depositATM(playerUUID, couldNotAdd);
        return couldNotAdd;
    }

    @SideOnly(Side.SERVER)
    public static int addMoneyVault(UUID playerUUID, int amount) throws SQLException {
        List<TileEntityCash> cashBlocks = getCashBlocks(playerUUID);

        int couldNotAdd = 0;
        for (TileEntityCash cashBlock : cashBlocks) {
            Map<Integer, ItemStack> moneyStacks = getMoneyStacks(cashBlock.getInventory());
            moneyStacks.forEach((slot, stack) -> cashBlock.getInventory().setInventorySlotContents(slot, null));
            int toAdd = amount;
            for (ItemStack itemStack : moneyStacks.values()) toAdd += getAmount(itemStack);

            List<ItemStack> stacksToInsert = ItemMoney.getDrops(toAdd);
            for (ItemStack stack : stacksToInsert)
                couldNotAdd += InventoryUtils.insertItem(cashBlock.getInventory(), stack, false) * ((ItemMoney) stack.getItem()).amount;

            amount = couldNotAdd;

            cashBlock.Sync();
        }

//        if (couldNotAdd > 0) depositATM(playerUUID, couldNotAdd);
        return couldNotAdd;
    }

    // DONE
    private static int[] takeMoney(IInventory inventory, int amount) {
        int couldNotAdd = 0;
        int couldTake = 0;

        Map<Integer, ItemStack> inventoryMoney = getMoneyStacks(inventory);

        List<Integer> toRemove = Lists.newArrayList();
        ItemStack largeStack = null;

        for (Integer slot : inventoryMoney.keySet()) {
            ItemStack stack = inventoryMoney.get(slot);
            int stackAmount = getAmount(stack);

            if (stackAmount <= 0) break;

            if (stackAmount > amount) {
                // decrease stack size from inventory
                largeStack = stack;
                toRemove.add(slot);
                break;
            } else {
                // remove stack of cash from inventory
                toRemove.add(slot);
                amount -= stackAmount;
                couldTake += stackAmount;
            }

        }

        toRemove.forEach(slot -> inventory.setInventorySlotContents(slot, null));

        if (largeStack != null) {
            int stackAmount = getAmount(largeStack);
            List<ItemStack> stacks = ItemMoney.getDrops(stackAmount - amount);
            couldNotAdd += addMoney(inventory, stackAmount - amount);
            couldTake += getAmount(stacks);
        }

        return new int[]{couldTake, couldNotAdd};
    }

    // DONE
    private static int addMoney(IInventory inventory, int amount) {
        List<ItemStack> stacks = ItemMoney.getDrops(amount);

        int couldNotAdd = 0;
        for (ItemStack stack : stacks)
            couldNotAdd += InventoryUtils.insertItem(inventory, stack, false) * ((ItemMoney) stack.getItem()).amount;

        return couldNotAdd;
    }

    // DONE
    private static int getAmount(List<ItemStack> stacks) {
        int total = 0;
        for (ItemStack stack : stacks) {
            if (stack != null && stack.getItem() instanceof ItemMoney) {
                total += ((ItemMoney) stack.getItem()).amount * stack.stackSize;
            }
        }

        return total;
    }

    // DONE
    private static int getAmount(ItemStack stack) {
        return stack.getItem() instanceof ItemMoney ? ((ItemMoney) stack.getItem()).amount * stack.stackSize : 0;
    }

    // DONE
    private static Map<Integer, ItemStack> getMoneyStacks(IInventory inventory) {
        Map<Integer, ItemStack> map = Maps.newHashMap();
        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            if (inventory.getStackInSlot(i) != null && inventory.getStackInSlot(i).getItem() instanceof ItemMoney) {
                map.put(i, inventory.getStackInSlot(i));
            }
        }

        return map;
    }

    // DONE
    private static List<TileEntityCash> getCashBlocks(UUID playerID) {
        List<TileEntityCash> alreadyChecked = Lists.newArrayList();

        for (Estate estate : EstateHandler.getEstates(playerID)) {
            AxisAlignedBB bounds = AxisAlignedBB.getBoundingBox(estate.getBounds().minX, estate.getBounds().minY, estate.getBounds().minZ,
                    estate.getBounds().maxX, estate.getBounds().maxY, estate.getBounds().maxZ);

            try {
                ResultSet result = Minelife.SQLITE.query("SELECT * FROM cash_blocks WHERE x >= " + bounds.minX + " AND x <= " + bounds.maxX + "" +
                        " AND y >= " + bounds.minY + " AND y <= " + bounds.maxY + " AND z >= " + bounds.minZ + " AND z <= " + bounds.maxZ + "" +
                        " AND dimension='" + estate.getWorld().provider.dimensionId + "'");

                while (result.next()) {
                    TileEntity tileEntity = estate.getWorld().getTileEntity(result.getInt("x"), result.getInt("y"), result.getInt("z"));
                    if (tileEntity != null && tileEntity instanceof TileEntityCash)
                        alreadyChecked.add((TileEntityCash) tileEntity);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return alreadyChecked;
    }

    // TODO: We just use the ATM for the welfare
    public static void depositATM(UUID player, int amount) throws SQLException {
        Minelife.SQLITE.query("UPDATE economy SET amount='" + (getBalanceATM(player) + amount) + "' WHERE player='" + player.toString() + "'");
    }

    public static void withdrawATM(UUID player, int amount) throws SQLException {
        Minelife.SQLITE.query("UPDATE economy SET amount='" + (getBalanceATM(player) - amount) + "' WHERE player='" + player.toString() + "'");
    }

    public static int getBalanceATM(UUID player) {
        ResultSet result = null;
        try {
            result = Minelife.SQLITE.query("SELECT * FROM economy WHERE player='" + player.toString() + "'");
            return result.next() ? result.getInt("amount") : 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static void setATM(UUID player, int amount) throws SQLException {
        Minelife.SQLITE.query("UPDATE economy SET amount='" + amount + "' WHERE player='" + player.toString() + "'");
    }

    public static boolean hasATM(UUID player) throws SQLException {
        return Minelife.SQLITE.query("SELECT * FROM economy WHERE player='" + player.toString() + "'").next();
    }

}
