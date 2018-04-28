package com.minelife.chestshop;

import codechicken.lib.inventory.InventoryRange;
import codechicken.lib.inventory.InventoryUtils;
import com.google.common.collect.Lists;
import com.minelife.util.ItemHelper;
import com.minelife.util.MLTileEntity;
import cpw.mods.ironchest.common.blocks.chest.BlockIronChest;
import cpw.mods.ironchest.common.tileentity.chest.TileEntityIronChest;
import net.minecraft.block.BlockChest;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

import java.util.List;
import java.util.UUID;

public class TileEntityChestShop extends MLTileEntity {

    private UUID owner;
    private EnumFacing facing = EnumFacing.NORTH;
    private ItemStack item;
    private int price = 0;

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        this.facing = EnumFacing.values()[tag.getInteger("Facing")];
        this.price = tag.getInteger("Price");
        if (tag.hasKey("Item")) this.item = new ItemStack(tag.getCompoundTag("Item"));
        else this.item = null;
        if (tag.hasKey("Owner")) this.owner = UUID.fromString(tag.getString("Owner"));
        else this.owner = null;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setInteger("Facing", this.facing.ordinal());
        tag.setInteger("Price", this.price);

        if (this.item != null) {
            NBTTagCompound itemTag = new NBTTagCompound();
            this.item.writeToNBT(itemTag);
            tag.setTag("Item", itemTag);
        }
        if (this.owner != null) tag.setString("Owner", this.owner.toString());
        return tag;
    }

    public UUID getOwner() {
        return owner;
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
    }

    public EnumFacing getFacing() {
        return facing;
    }

    public void setFacing(EnumFacing facing) {
        this.facing = facing;
    }

    public ItemStack getItem() {
        return item;
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public List<Stock> getStock() {
        List<Stock> stacks = Lists.newArrayList();

        if (getItem() == null) return stacks;

        getChests().forEach(chest -> {
            for (int i = 0; i < chest.getSizeInventory(); i++) {
                if (ItemHelper.areStacksIdentical(getItem(), chest.getStackInSlot(i)))
                    stacks.add(new Stock(chest.getPos(), i, chest.getStackInSlot(i)));
            }
        });

        getIronChests().forEach(chest -> {
            for (int i = 0; i < chest.chestContents.size(); i++) {
                if (ItemHelper.areStacksIdentical(getItem(), chest.getStackInSlot(i)))
                    stacks.add(new Stock(chest.getPos(), i, chest.getStackInSlot(i)));
            }
        });
        return stacks;
    }

    public List<TileEntityChest> getChests() {
        List<TileEntityChest> chests = Lists.newArrayList();

        // get the chest underneath
        if (getWorld().getBlockState(getPos().add(0, -1, 0)).getBlock() instanceof BlockChest) {
            chests.add((TileEntityChest) getWorld().getTileEntity(getPos().add(0, -1, 0)));
        }

        // get the chest above
        if (getWorld().getBlockState(getPos().add(0, 1, 0)).getBlock() instanceof BlockChest) {
            chests.add((TileEntityChest) getWorld().getTileEntity(getPos().add(0, 1, 0)));
        }

        // get the chest left
        if (getWorld().getBlockState(getPos().add(-1, 0, 0)).getBlock() instanceof BlockChest) {
            chests.add((TileEntityChest) getWorld().getTileEntity(getPos().add(-1, 0, 0)));
        }

        // get the chest right
        if (getWorld().getBlockState(getPos().add(1, 0, 0)).getBlock() instanceof BlockChest) {
            chests.add((TileEntityChest) getWorld().getTileEntity(getPos().add(1, 0, 0)));
        }

        // get the chest behind
        if (getWorld().getBlockState(getPos().add(0, 0, -1)).getBlock() instanceof BlockChest) {
            chests.add((TileEntityChest) getWorld().getTileEntity(getPos().add(0, 0, -1)));
        }

        // get the chest front
        if (getWorld().getBlockState(getPos().add(0, 0, 1)).getBlock() instanceof BlockChest) {
            chests.add((TileEntityChest) getWorld().getTileEntity(getPos().add(0, 0, 1)));
        }

        return chests;
    }

    public List<TileEntityIronChest> getIronChests() {
        List<TileEntityIronChest> ironChests = Lists.newArrayList();

        // get the chest underneath
        if (getWorld().getBlockState(getPos().add(0, -1, 0)).getBlock() instanceof BlockIronChest) {
            ironChests.add((TileEntityIronChest) getWorld().getTileEntity(getPos().add(0, -1, 0)));
        }

        // get the chest above
        if (getWorld().getBlockState(getPos().add(0, 1, 0)).getBlock() instanceof BlockIronChest) {
            ironChests.add((TileEntityIronChest) getWorld().getTileEntity(getPos().add(0, 1, 0)));
        }

        // get the chest left
        if (getWorld().getBlockState(getPos().add(-1, 0, 0)).getBlock() instanceof BlockIronChest) {
            ironChests.add((TileEntityIronChest) getWorld().getTileEntity(getPos().add(-1, 0, 0)));
        }

        // get the chest right
        if (getWorld().getBlockState(getPos().add(1, 0, 0)).getBlock() instanceof BlockIronChest) {
            ironChests.add((TileEntityIronChest) getWorld().getTileEntity(getPos().add(1, 0, 0)));
        }

        // get the chest behind
        if (getWorld().getBlockState(getPos().add(0, 0, -1)).getBlock() instanceof BlockIronChest) {
            ironChests.add((TileEntityIronChest) getWorld().getTileEntity(getPos().add(0, 0, -1)));
        }

        // get the chest front
        if (getWorld().getBlockState(getPos().add(0, 0, 1)).getBlock() instanceof BlockIronChest) {
            ironChests.add((TileEntityIronChest) getWorld().getTileEntity(getPos().add(0, 0, 1)));
        }

        return ironChests;
    }

    public int getStockCount() {
        int count = 0;
        for (Stock stock : getStock()) count += stock.stack.getCount();
        return count;
    }

    public void doPurchase(EntityPlayerMP player, int amount) {
        if(getItem() == null) return;
        amount *= getItem().getCount();

        List<ItemStack> toDrop = Lists.newArrayList();

       for (Stock stock : getStock()) {
            if (world.getTileEntity(stock.pos) instanceof TileEntityChest) {
                TileEntityChest tileChest = (TileEntityChest) world.getTileEntity(stock.pos);
                if (stock.stack.getCount() <= amount) {
                    tileChest.setInventorySlotContents(stock.slot, ItemStack.EMPTY);
                    toDrop.add(stock.stack);
                    amount -= stock.stack.getCount();
                } else {
                    stock.stack.setCount(stock.stack.getCount() - amount);
                    ItemStack clone = stock.stack.copy();
                    clone.setCount(amount);
                    toDrop.add(clone);
                    break;
                }
                stock.updateChest();
            } else if (world.getTileEntity(stock.pos) instanceof TileEntityIronChest) {
                TileEntityIronChest tileChest = (TileEntityIronChest) world.getTileEntity(stock.pos);
                if (stock.stack.getCount() <= amount) {
                    tileChest.setInventorySlotContents(stock.slot, ItemStack.EMPTY);
                    toDrop.add(stock.stack);
                    amount -= stock.stack.getCount();
                } else {
                    stock.stack.setCount(stock.stack.getCount() - amount);
                    ItemStack clone = stock.stack.copy();
                    clone.setCount(amount);
                    toDrop.add(clone);
                    break;
                }
                stock.updateChest();
            }
        }

        InventoryRange range = new InventoryRange(player.inventory, 0, 36);

        toDrop.forEach(stack -> {
            InventoryUtils.insertItem(range, stack, false);
        });
    }

    public boolean canPurchaseFit(EntityPlayerMP player, int amount) {
        if(getItem() == null) return false;
        amount *= getItem().getCount();
        InventoryRange range = new InventoryRange(player.inventory, 0, 36);
        return InventoryUtils.getInsertibleQuantity(range, getItem()) >= amount;
    }

    public class Stock {
        BlockPos pos;
        int slot;
        ItemStack stack;

        public Stock(BlockPos pos, int slot, ItemStack stack) {
            this.pos = pos;
            this.slot = slot;
            this.stack = stack;
        }

        public void updateChest() {
            TileEntity tileEntity = world.getTileEntity(pos);
            world.markBlockRangeForRenderUpdate(tileEntity.getPos(), tileEntity.getPos());
            world.notifyBlockUpdate(tileEntity.getPos(), tileEntity.getWorld().getBlockState(tileEntity.getPos()),
                    tileEntity.getWorld().getBlockState(tileEntity.getPos()), 3);
            world.scheduleBlockUpdate(tileEntity.getPos(), tileEntity.getBlockType(), 0, 0);
            tileEntity.markDirty();
        }
    }

}
