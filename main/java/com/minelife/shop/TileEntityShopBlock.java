package com.minelife.shop;

import buildcraft.core.lib.block.TileBuildCraft;
import buildcraft.core.lib.inventory.SimpleInventory;
import codechicken.lib.inventory.InventoryUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockChest;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EnumFacing;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class TileEntityShopBlock extends TileEntity {

    private SimpleInventory simpleInventory;
    private UUID owner;
    private double price;
    private int amount;
    private EnumFacing facing;

    public TileEntityShopBlock() {
        simpleInventory = new SimpleInventory(1, "shop_block_inventory", 64);
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        simpleInventory.writeToNBT(nbt);
        nbt.setDouble("price", price);
        nbt.setInteger("amount", amount);
        if (owner != null) nbt.setString("owner", owner.toString());
        if (facing != null) nbt.setInteger("facing", facing.ordinal());
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        simpleInventory.readFromNBT(nbt);
        if (nbt.hasKey("price")) price = nbt.getDouble("price");
        if (nbt.hasKey("amount")) amount = nbt.getInteger("amount");
        if (nbt.hasKey("owner")) owner = UUID.fromString(nbt.getString("owner"));
        if (nbt.hasKey("facing")) facing = EnumFacing.values()[nbt.getInteger("facing")];
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound nbtTag = new NBTTagCompound();
        this.writeToNBT(nbtTag);
        return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 1, nbtTag);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
        this.readFromNBT(pkt.func_148857_g());
    }

    private void sync() {
        this.getWorldObj().markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
        this.markDirty();
    }

    public void setFacing(EnumFacing facing) {
        this.facing = facing;
        sync();
    }

    public EnumFacing getFacing() {
        return facing;
    }

    public void setOwner(UUID player) {
        owner = player;
        sync();
    }

    public UUID getOwner() {
        return owner;
    }

    public void setStackToSale(ItemStack stack) {
        simpleInventory.setInventorySlotContents(0, stack);
        amount = stack.stackSize;
        sync();
    }

    public ItemStack getStackToSale() {
        ItemStack toSale = simpleInventory.getStackInSlot(0);
        if(toSale != null) toSale.stackSize = amount;
        return toSale;
    }

    public void setPrice(double price) {
        this.price = price;
        sync();
    }

    public double getPrice() {
        return price;
    }

    @SideOnly(Side.SERVER)
    public void doTransaction(EntityPlayerMP player, int amount) {
        Map<TileEntityChest, ItemStack> stock = getStock();


//        ItemStack to_give = item_stack().copy();
//        to_give.stackSize = amount;
//        item_stack.stackSize -= amount;
//        EntityItem entity_item = player.dropPlayerItemWithRandomChoice(to_give, false);
//        entity_item.delayBeforeCanPickup = 0;
    }

    public Map<TileEntityChest, ItemStack> getStock() {
        List<TileEntityChest> chests = Lists.newArrayList();
        // get the chest underneath
        if (getWorldObj().getBlock(xCoord, yCoord - 1, zCoord) instanceof BlockChest) {
            chests.add((TileEntityChest) getWorldObj().getTileEntity(xCoord, yCoord - 1, zCoord));
        }

        // get the chest above
        if (getWorldObj().getBlock(xCoord, yCoord + 1, zCoord) instanceof BlockChest) {
            chests.add((TileEntityChest) getWorldObj().getTileEntity(xCoord, yCoord + 1, zCoord));
        }

        // get the chest left
        if (getWorldObj().getBlock(xCoord - 1, yCoord, zCoord) instanceof BlockChest) {
            chests.add((TileEntityChest) getWorldObj().getTileEntity(xCoord - 1, yCoord, zCoord));
        }

        // get the chest right
        if (getWorldObj().getBlock(xCoord + 1, yCoord, zCoord) instanceof BlockChest) {
            chests.add((TileEntityChest) getWorldObj().getTileEntity(xCoord + 1, yCoord, zCoord));
        }

        // get the chest behind
        if (getWorldObj().getBlock(xCoord, yCoord, zCoord - 1) instanceof BlockChest) {
            chests.add((TileEntityChest) getWorldObj().getTileEntity(xCoord, yCoord, zCoord - 1));
        }

        // get the chest front
        if (getWorldObj().getBlock(xCoord, yCoord, zCoord + 1) instanceof BlockChest) {
            chests.add((TileEntityChest) getWorldObj().getTileEntity(xCoord, yCoord, zCoord + 1));
        }

        Map<TileEntityChest, ItemStack> items = Maps.newHashMap();

        // add the items from all the chests that are identical to stackToSale
        chests.forEach(tileEntityChest -> {
            for (int slot = 0; slot < tileEntityChest.getSizeInventory(); slot++) {
                if (tileEntityChest.getStackInSlot(slot) != null && InventoryUtils.areStacksIdentical(tileEntityChest.getStackInSlot(slot), getStackToSale())) {
                    items.put(tileEntityChest, tileEntityChest.getStackInSlot(slot));
                }
            }
        });

        return items;
    }


    public float getRotationDegree() {
        return this.facing == EnumFacing.NORTH ? 180 : this.facing == EnumFacing.EAST ? 0 :
                this.facing == EnumFacing.SOUTH ? 180 : 0;
    }


}
