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
import com.minelife.util.NumberConversions;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dan200.computercraft.shared.util.InventoryUtil;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MoneyHandler {

    @SideOnly(Side.SERVER)
    public static int getBalanceVault(UUID playerUUID) {
        int total = 0;

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
                                total += ((TileEntityCash) te).getHoldings();
                                alreadyChecked.add((TileEntityCash) te);
                            }
                        }
                    }
                }
            }
        }

        return total;
    }

    @SideOnly(Side.SERVER)
    public static int getBalanceVault(EntityPlayerMP player) {
        return getBalanceVault(player.getUniqueID());
    }

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

    // TODO: Do these methods.
    @SideOnly(Side.SERVER)
    public static void takeMoneyInventory(EntityPlayerMP player, int amount) {
        Vector3 playerVec = new Vector3(player.posX, player.posY, player.posZ);

        amount = takeMoney(player.inventory, amount, player.worldObj, playerVec);

        if (amount > 0) {
            for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
                ItemStack stack = player.inventory.getStackInSlot(i);
                if (stack != null && stack.getItem() instanceof ItemWallet) {
                    amount = takeMoney(new InventoryWallet(stack), amount, player.worldObj, playerVec);
                    if (amount <= 0) return;
                }
            }
        }

    }

    @SideOnly(Side.SERVER)
    public static void addMoneyInventory(EntityPlayerMP player, int amount) {

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

    private static Map<Integer, ItemStack> getMoneyStacks(IInventory inventory) {
        Map<Integer, ItemStack> map = Maps.newHashMap();
        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            if (inventory.getStackInSlot(i) != null && inventory.getStackInSlot(i).getItem() instanceof ItemMoney) {
                map.put(i, inventory.getStackInSlot(i));
            }
        }

        return map;
    }

    private static int takeMoney(IInventory inventory, int amount, World toDropWorld, Vector3 toDropVec) {
        Map<Integer, ItemStack> inventoryMoney = getMoneyStacks(inventory);

        List<Integer> toRemove = Lists.newArrayList();
        ItemStack largeStack = null;

        for (Integer slot : inventoryMoney.keySet()) {
            ItemStack stack = inventoryMoney.get(slot);
            int stackAmount = ((ItemMoney) stack.getItem()).amount * stack.stackSize;

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
            }

        }

        for (Integer slot : toRemove) {
            inventory.setInventorySlotContents(slot, null);
        }

        if (largeStack != null) {
            int stackAmount = ((ItemMoney) largeStack.getItem()).amount * largeStack.stackSize;
            System.out.println("StackAmount: " + stackAmount);
            System.out.println("StackAmount - Amount: " + (stackAmount - amount));
            List<ItemStack> stacks = ItemMoney.getDrops(stackAmount - amount);
            System.out.println("STACKS: " + stacks.size());
            for (ItemStack s : stacks) {
                System.out.println("$" + ((ItemMoney) s.getItem()).amount);
                int couldNotAdd = InventoryUtils.insertItem(inventory, s, false);
                System.out.println(couldNotAdd);
                if (couldNotAdd > 0) {
                    ItemStack toDrop = s.copy();
                    toDrop.stackSize = 2;
                    System.out.println("CALLED " + ((ItemMoney) toDrop.getItem()).amount);
                    // TODO: For some reason it's not dropping the item stack
                    InventoryUtils.dropItem(toDrop, toDropWorld, toDropVec);
                }
            }

            amount = 0;
        }

        return amount;
    }

    private static int addMoney(IInventory inventory, int amount) {
        List<ItemStack> stacks = ItemMoney.getDrops(amount);

        int couldNotAdd = 0;
        for (ItemStack stack : stacks) couldNotAdd += InventoryUtils.insertItem(inventory, stack, true);

        return couldNotAdd;
    }

    private int getAmount(List<ItemStack> stacks) {
        int total = 0;
        for (ItemStack stack : stacks) {
            if (stack != null && stack.getItem() instanceof ItemMoney) {
                total += ((ItemMoney) stack.getItem()).amount * stack.stackSize;
            }
        }

        return total;
    }

}
