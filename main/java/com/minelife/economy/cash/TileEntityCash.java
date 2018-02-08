package com.minelife.economy.cash;

import buildcraft.core.lib.inventory.SimpleInventory;
import codechicken.lib.inventory.InventoryUtils;
import com.minelife.Minelife;
import com.minelife.economy.ItemMoney;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TileEntityCash extends TileEntity {

    private SimpleInventory inventory;
    private EnumFacing direction;

    public TileEntityCash() {
        inventory = new SimpleInventory(54, "cash", 64);
    }

    // just used to ItemMoney
    public int addCash(ItemStack cash) {
        return InventoryUtils.insertItem(inventory, cash, false);
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);
        this.inventory.readFromNBT(tagCompound, "Items");
        direction = EnumFacing.valueOf(tagCompound.getString("facing"));
        System.out.println("CALLED readFromNBT: " + getHoldings());
    }

    @Override
    public void writeToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);
        this.inventory.writeToNBT(tagCompound, "Items");
        if (direction != null) tagCompound.setString("facing", direction.name());
        System.out.println("CALLED writeToNBT: " + getHoldings());
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

    public void Sync() {
        if(!worldObj.isRemote) {
            this.getWorldObj().markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
            this.markDirty();
            try {
                ResultSet result = Minelife.SQLITE.query("SELECT * FROM cash_blocks WHERE x='" + xCoord + "' AND y='" + yCoord + "' AND z='" + zCoord + "' AND dimension='" + worldObj.provider.dimensionId + "'");
                if (!result.next()) {
                    Minelife.SQLITE.query("INSERT INTO cash_blocks (x, y, z, dimension) VALUES ('" + xCoord + "', '" + yCoord + "', '" + zCoord + "', '" + worldObj.provider.dimensionId + "')");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public SimpleInventory getInventory() {
        return inventory;
    }

    public boolean isItemValidForSlot(int p_94041_1_, ItemStack p_94041_2_) {
        return p_94041_2_ != null && p_94041_2_.getItem() instanceof ItemMoney;
    }

    public void setFacing(EnumFacing facing) {
        this.direction = facing;
        Sync();
    }

    public EnumFacing getDirection() {
        return direction;
    }

    public int getHoldings() {
        int total = 0;
        for (int i = 0; i < getInventory().getSizeInventory(); i++) {
            if (getInventory().getStackInSlot(i) != null) {
                total += (((ItemMoney) getInventory().getStackInSlot(i).getItem())).amount * getInventory().getStackInSlot(i).stackSize;
            }
        }

        return total;
    }

}
