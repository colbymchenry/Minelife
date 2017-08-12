package com.minelife.drug.tileentity;

import buildcraft.api.tiles.IHasWork;
import buildcraft.api.transport.IItemPipe;
import buildcraft.api.transport.IPipeConnection;
import buildcraft.api.transport.IPipeTile;
import buildcraft.core.lib.RFBattery;
import buildcraft.core.lib.block.TileBuildCraft;
import buildcraft.core.lib.fluids.TankUtils;
import buildcraft.core.lib.inventory.SimpleInventory;
import buildcraft.core.lib.utils.Utils;
import com.minelife.Minelife;
import com.minelife.drug.DrugsGuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.*;

import java.util.Iterator;

public abstract class TileEntityElectricMachine extends TileBuildCraft implements IHasWork, IInventory, ISidedInventory, IPipeConnection {

    private boolean isRedstonePowered = false;
    private final SimpleInventory inv;
    private int rfPrev = 0;
    private int rfUnchangedCycles = 0;
    private int progress = 0;
    private int update;

    public TileEntityElectricMachine(int invSize, String name)
    {
        this.inv = new SimpleInventory(invSize, name, 64);
        this.update = Utils.RANDOM.nextInt();
        this.setBattery(new RFBattery(max_energy_storage(), max_energy_receive(), max_energy_extract()));
    }

    public abstract int gui_id();

    public abstract int max_energy_storage();

    public abstract int max_energy_receive();

    public abstract int max_energy_extract();

    public abstract int update_time();

    public abstract int processing_time();

    public abstract int min_energy_extract();

    @Override
    public void readFromNBT(NBTTagCompound data)
    {
        super.readFromNBT(data);
        this.inv.readFromNBT(data);
        this.rfPrev = this.getBattery().getEnergyStored();
        this.rfUnchangedCycles = 0;
    }

    @Override
    public void writeToNBT(NBTTagCompound data)
    {
        super.writeToNBT(data);
        this.inv.writeToNBT(data);
    }

    @Override
    public int getSizeInventory()
    {
        return inv.getSizeInventory();
    }

    @Override
    public ItemStack getStackInSlot(int slot)
    {
        return inv.getStackInSlot(slot);
    }

    @Override
    public ItemStack decrStackSize(int slot, int count)
    {
        return inv.decrStackSize(slot, count);
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot)
    {
        return inv.getStackInSlotOnClosing(slot);
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack)
    {
        inv.setInventorySlotContents(slot, stack);
    }

    @Override
    public String getInventoryName()
    {
        return inv.getInventoryName();
    }

    @Override
    public boolean hasCustomInventoryName()
    {
        return true;
    }

    @Override
    public int getInventoryStackLimit()
    {
        return inv.getInventoryStackLimit();
    }

    @Override
    public void openInventory()
    {

    }

    @Override
    public void closeInventory()
    {

    }

    public abstract boolean has_work();

    public abstract void work();

    public abstract void on_progress_increment();

    @Override
    public boolean hasWork()
    {
        return has_work();
    }

    public abstract boolean can_pipe_connect(IPipeTile.PipeType pipe_type, ForgeDirection direction);

    @Override
    public ConnectOverride overridePipeConnection(IPipeTile.PipeType pipeType, ForgeDirection forgeDirection)
    {
        return can_pipe_connect(pipeType, forgeDirection) ? ConnectOverride.DEFAULT : ConnectOverride.DISCONNECT;
    }

    @Override
    public void updateEntity()
    {
        super.updateEntity();
        this.isRedstonePowered = this.worldObj.isBlockIndirectlyGettingPowered(this.xCoord, this.yCoord, this.zCoord);

        RFBattery battery = this.getBattery();
        if (this.rfPrev != battery.getEnergyStored()) {
            this.rfPrev = battery.getEnergyStored();
            this.rfUnchangedCycles = 0;
        }

        ++this.rfUnchangedCycles;
        if (this.rfUnchangedCycles > 100) {
            battery.useEnergy(0, 1000, false);
            this.rfPrev = battery.getEnergyStored();
        }


        if (has_work()) {
            int updateNext = this.update + this.getBattery().getEnergyStored() + 1;
            int updateThreshold = (this.update & -update_time()) + update_time();
            this.update = Math.min(updateThreshold, updateNext);
            if (this.update % update_time() == 0) {
                int energyUsed = this.getBattery().useEnergy(min_energy_extract(), (int) Math.ceil(min_energy_extract() + (double) this.getBattery().getEnergyStored() / (min_energy_extract() / 2)), false);
                if (energyUsed >= min_energy_extract()) {
                    if (requires_redstone_power())
                        if (!this.isRedstonePowered) {
                            return;
                        }

                    this.progress += update_time();
                    on_progress_increment();
                    if (this.progress >= processing_time()) {
                        this.progress = 0;
                        work();
                    }
                }
            }
        } else {
            this.progress = 0;
        }
    }

    public abstract boolean requires_redstone_power();

    public abstract int[] get_accessible_slots_from_side(int side);

    @Override
    public int[] getAccessibleSlotsFromSide(int side)
    {
        return get_accessible_slots_from_side(side);
    }

    public abstract boolean can_insert_item(int slot, ItemStack stack, int side);

    @Override
    public boolean canInsertItem(int slot, ItemStack stack, int side)
    {
        return can_insert_item(slot, stack, side);
    }

    public abstract boolean can_extract_item(int slot, ItemStack stack, int side);

    @Override
    public boolean canExtractItem(int slot, ItemStack stack, int side)
    {
        return can_extract_item(slot, stack, side);
    }

    public abstract boolean is_useable_by_player(EntityPlayer player);

    @Override
    public boolean isUseableByPlayer(EntityPlayer player)
    {
        return is_useable_by_player(player);
    }

    public abstract boolean is_item_valid_for_slot(int slot, ItemStack stack);

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack)
    {
        return is_item_valid_for_slot(slot, stack);
    }

    public boolean onBlockActivated(EntityPlayer player)
    {
        if (!this.worldObj.isRemote && gui_id() != -1) {
            player.openGui(Minelife.instance, gui_id(), this.worldObj, this.xCoord, this.yCoord, this.zCoord);
        }
        return true;
    }

    public void getGUINetworkData(int id, int value)
    {
        if (id == 0) progress = value;
    }

    public void sendGUINetworkData(Container container, ICrafting iCrafting)
    {
        iCrafting.sendProgressBarUpdate(container, 0, progress);
    }

    public int progress_scaled(int i)
    {
        return this.progress * i / processing_time();
    }

    public int progress()
    {
        return progress;
    }

    public void set_progress(int progress)
    {
        this.progress = progress;
    }

}