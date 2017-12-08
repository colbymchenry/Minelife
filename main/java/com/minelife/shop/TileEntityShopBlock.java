package com.minelife.shop;

import buildcraft.core.lib.block.TileBuildCraft;
import buildcraft.core.lib.inventory.SimpleInventory;
import codechicken.lib.inventory.InventoryUtils;
import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.minelife.Minelife;
import com.minelife.economy.ModEconomy;
import com.minelife.util.NumberConversions;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockChest;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
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
        if (toSale != null) toSale.stackSize = amount;
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
    public void doTransaction(EntityPlayerMP player) {
        List<Stock> stock = getStock();
        Map<Stock, Integer> toRemove = Maps.newHashMap();
        ItemStack toSale = getStackToSale().copy();

        int amount = 0;
        for (Stock s : stock) {
            ItemStack stack = s.chest.getStackInSlot(s.slot);
            if(stack.stackSize >= toSale.stackSize) {
                toRemove.put(s, stack.stackSize - toSale.stackSize);
                amount = toSale.stackSize;
                break;
            } else {
                toRemove.put(s, 0);
                amount += stack.stackSize;
            }

            if (amount >= toSale.stackSize) {
                amount = toSale.stackSize;
                break;
            }
        }

        if(amount == 0) {
            player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.RED + "Out of stock."));
            return;
        }

        try {
            toRemove.forEach((stock1, stackSize) -> {
                if(stackSize <= 0) {
                    stock1.chest.setInventorySlotContents(stock1.slot, null);
                } else {
                    ItemStack s = stock1.chest.getStackInSlot(stock1.slot);
                    s.stackSize = stackSize;
                    stock1.chest.setInventorySlotContents(stock1.slot, s);
                }
            });

            ModEconomy.withdraw(player.getUniqueID(), getPrice() * ((double) amount / (double) toSale.stackSize), true);
            ModEconomy.deposit(getOwner(), getPrice() * ((double) amount / (double) toSale.stackSize), false);

            ItemStack toDrop = toSale.copy();
            toDrop.stackSize = amount;
            EntityItem entity_item = player.dropPlayerItemWithRandomChoice(toDrop, false);
            entity_item.delayBeforeCanPickup = 0;

            player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.RED + "-$" + NumberConversions.formatter.format(getPrice() * ((double) amount / (double) toSale.stackSize))));
        } catch (Exception e) {
            e.printStackTrace();
            player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.RED + Minelife.default_error_message));
        }
    }

    public List<Stock> getStock() {
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

        ItemStack toSale = getStackToSale();
        List<Stock> stock = Lists.newArrayList();
        // add the items from all the chests that are identical to stackToSale
        chests.forEach(tileEntityChest -> {
            for (int slot = 0; slot < tileEntityChest.getSizeInventory(); slot++) {
                if (tileEntityChest.getStackInSlot(slot) != null) {
                    if(areStacksIdentical(tileEntityChest.getStackInSlot(slot), toSale)) {
                     stock.add(new Stock(tileEntityChest, slot));
                 }
                }
            }
        });

        return stock;
    }


    public float getRotationDegree() {
        return this.facing == EnumFacing.NORTH ? 180 : this.facing == EnumFacing.EAST ? 0 :
                this.facing == EnumFacing.SOUTH ? 180 : 0;
    }

    public boolean areStacksIdentical(ItemStack stack1, ItemStack stack2) {
        if (stack1 != null && stack2 != null) {
            return stack1.getItem() == stack2.getItem() && stack1.getItemDamage() == stack2.getItemDamage() && Objects.equal(stack1.getTagCompound(), stack2.getTagCompound());
        } else {
            return stack1 == stack2;
        }
    }

    class Stock {
        TileEntityChest chest;
        int slot;

        public Stock(TileEntityChest chest, int slot) {
            this.chest = chest;
            this.slot = slot;
        }
    }

}
