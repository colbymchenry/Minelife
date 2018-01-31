package com.minelife.economy;

import codechicken.lib.inventory.InventoryUtils;
import codechicken.lib.vec.Vector3;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.minelife.MLItems;
import com.minelife.economy.cash.TileEntityCash;
import com.minelife.realestate.Estate;
import com.minelife.realestate.EstateHandler;
import com.minelife.util.NumberConversions;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;

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
        Map<Integer, ItemStack> playerInventoryMoney = getMoneyStacks(player.inventory);

        List<Integer> toRemove = Lists.newArrayList();
        ItemStack largeStack = null;

        for (Integer slot : playerInventoryMoney.keySet()) {
            ItemStack stack = playerInventoryMoney.get(slot);
            int stackAmount = ((ItemMoney) stack.getItem()).amount * stack.stackSize;

            if(stackAmount <= 0) break;

            if (stackAmount >= amount) {
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
            player.inventory.setInventorySlotContents(slot, null);
        }

        if(largeStack != null) {
            int stackAmount = ((ItemMoney) largeStack.getItem()).amount * largeStack.stackSize;
            List<ItemStack> stacks = ItemMoney.getDrops(stackAmount - amount);
            for (ItemStack s : stacks) {
                int couldNotAdd = InventoryUtils.insertItem(player.inventory, s, false);
                if (couldNotAdd > 0) {
                    ItemStack toDrop = s.copy();
                    toDrop.stackSize = couldNotAdd;
                    InventoryUtils.dropItem(toDrop, player.worldObj, new Vector3(player.posX, player.posY, player.posZ));
                }
            }
        }

//        for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
//            ItemStack stack = player.inventory.getStackInSlot(i);
//            if (stack != null) {
//                if (stack.getItem() instanceof ItemMoney) {
//
//                } else if (stack.getItem() == MLItems.wallet) {
//
//                } else if (stack.getItem() == MLItems.bagOCash) {
//
//                }
//            }
//        }
    }

    @SideOnly(Side.SERVER)
    public static void addMoneyInventory(EntityPlayerMP player, int amount) {

    }

    @SideOnly(Side.SERVER)
    public static List<ItemStack> takeMoneyVault(EntityPlayerMP player, int amount) {
        return takeMoneyVault(player.getUniqueID(), amount);
    }

    @SideOnly(Side.SERVER)
    public static void addMoneyVault(EntityPlayerMP player, int amount) {
        addMoneyVault(player.getUniqueID(), amount);
    }

    @SideOnly(Side.SERVER)
    public static List<ItemStack> takeMoneyVault(UUID playerUUID, int amount) {
        return null;
    }

    @SideOnly(Side.SERVER)
    public static void addMoneyVault(UUID playerUUID, int amount) {

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

}
