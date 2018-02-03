package com.minelife.economy;

import codechicken.lib.inventory.InventoryUtils;
import codechicken.lib.vec.Vector3;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.minelife.MLItems;
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
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

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
    @SideOnly(Side.SERVER)
    public static void takeMoneyInventory(EntityPlayerMP player, int amount) {
        Vector3 playerVec = new Vector3(player.posX, player.posY + 1, player.posZ);
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

        // drop what could not be added to inventory
        ItemMoney.getDrops(couldNotAdd).forEach(stack -> InventoryUtils.dropItem(stack, player.worldObj, playerVec));
    }

    @SideOnly(Side.SERVER)
    public static void addMoneyInventory(EntityPlayerMP player, int amount) {
        // first add to player's inventory
        Map<Integer, ItemStack> moneyStacks = getMoneyStacks(player.inventory);
        moneyStacks.forEach((slot, stack) -> player.inventory.setInventorySlotContents(slot, null));
        int toAdd = amount;
        for (ItemStack itemStack : moneyStacks.values()) toAdd += getAmount(itemStack);

        int couldNotAdd = 0;

        List<ItemStack> stacksToInsert = ItemMoney.getDrops(toAdd);
        for (ItemStack stack : stacksToInsert)
            couldNotAdd += InventoryUtils.insertItem(player.inventory, stack, false) * ((ItemMoney) stack.getItem()).amount;

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


        // drop what could not be added to inventory
        ItemMoney.getDrops(couldNotAdd).forEach(stack -> InventoryUtils.dropItem(stack, player.worldObj, new Vector3(player.posX, player.posY, player.posZ)));
    }

    @SideOnly(Side.SERVER)
    public static List<ItemStack> takeMoneyVault(EntityPlayerMP player, int amount) {
        return takeMoneyVault(player.getUniqueID(), amount);
    }

    @SideOnly(Side.SERVER)
    public static int addMoneyVault(EntityPlayerMP player, int amount) {
        return addMoneyVault(player.getUniqueID(), amount);
    }

    @SideOnly(Side.SERVER)
    public static List<ItemStack> takeMoneyVault(UUID playerUUID, int amount) {
        return null;
    }

    // TODO: Need to test this
    @SideOnly(Side.SERVER)
    public static int addMoneyVault(UUID playerUUID, int amount) {
        int couldNotAdd = 0;

        List<TileEntityCash> alreadyChecked = Lists.newArrayList();

        for (Estate estate : EstateHandler.getEstates(playerUUID)) {
            Vec3 min = Vec3.createVectorHelper(estate.getBounds().minX, estate.getBounds().minY, estate.getBounds().minZ);
            Vec3 max = Vec3.createVectorHelper(estate.getBounds().maxX, estate.getBounds().maxY, estate.getBounds().maxZ);
            AxisAlignedBB bounds = AxisAlignedBB.getBoundingBox(min.xCoord, min.yCoord, min.zCoord, max.xCoord, max.yCoord, max.zCoord);

            for (int x = (int) min.xCoord; x <= (int) max.xCoord + 16; x += 16) {
                for (int z = (int) min.zCoord; z <= (int) max.zCoord + 16; z += 16) {
                    Iterator<TileEntity> iterator = estate.getWorld().getChunkFromBlockCoords(x, z).chunkTileEntityMap.values().iterator();

                    while (iterator.hasNext()) {
                        TileEntity te = iterator.next();
                        if (te instanceof TileEntityCash) {
                            if (!alreadyChecked.contains(te) && bounds.isVecInside(Vec3.createVectorHelper(te.xCoord, te.yCoord, te.zCoord))) {
                                couldNotAdd += addMoney(((TileEntityCash) te), amount + couldNotAdd);
                                amount -= couldNotAdd;
                            }
                        }
                    }
                }
            }
        }

        return amount;
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
            Vec3 min = Vec3.createVectorHelper(estate.getBounds().minX, estate.getBounds().minY, estate.getBounds().minZ);
            Vec3 max = Vec3.createVectorHelper(estate.getBounds().maxX, estate.getBounds().maxY, estate.getBounds().maxZ);
            AxisAlignedBB bounds = AxisAlignedBB.getBoundingBox(min.xCoord, min.yCoord, min.zCoord, max.xCoord, max.yCoord, max.zCoord);

            for (int x = (int) min.xCoord; x <= (int) max.xCoord + 16; x += 16) {
                for (int z = (int) min.zCoord; z <= (int) max.zCoord + 16; z += 16) {
                    Iterator<TileEntity> iterator = estate.getWorld().getChunkFromBlockCoords(x, z).chunkTileEntityMap.values().iterator();

                    while (iterator.hasNext()) {
                        TileEntity te = iterator.next();
                        if (te instanceof TileEntityCash) {
                            if (!alreadyChecked.contains(te) && bounds.isVecInside(Vec3.createVectorHelper(te.xCoord, te.yCoord, te.zCoord))) {
                                alreadyChecked.add((TileEntityCash) te);
                            }
                        }
                    }
                }
            }
        }

        return alreadyChecked;
    }

}
