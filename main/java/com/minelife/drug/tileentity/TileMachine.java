package com.minelife.drug.tileentity;

import buildcraft.api.fuels.BuildcraftFuelRegistry;
import buildcraft.api.fuels.IFuel;
import buildcraft.api.tiles.IHasWork;
import buildcraft.api.transport.IItemPipe;
import buildcraft.api.transport.IPipeConnection;
import buildcraft.api.transport.IPipeTile;
import buildcraft.core.lib.block.TileBuildCraft;
import buildcraft.core.lib.engines.TileEngineBase;
import buildcraft.core.lib.fluids.Tank;
import buildcraft.core.lib.fluids.TankManager;
import buildcraft.core.lib.fluids.TankUtils;
import buildcraft.core.lib.inventory.SimpleInventory;
import buildcraft.core.lib.utils.Utils;
import com.minelife.Minelife;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.*;

public abstract class TileMachine extends TileBuildCraft implements IHasWork, IFluidHandler, IInventory, ISidedInventory, IPipeConnection {

    private Tank tank_fuel;
    private int tank_fuel_amount_cache;
    private TankManager<Tank> tank_manager;
    private IFuel current_fuel;
    private boolean isRedstonePowered = false;

    private final SimpleInventory inv;
    private final int[] defaultSlotArray;

    public TileMachine(int invSize, String name)
    {
        this.inv = new SimpleInventory(invSize, name, 64);
        this.defaultSlotArray = Utils.createSlotArray(0, invSize);
        this.tank_fuel = new Tank("tank_fuel", max_fuel(), this);
        this.tank_fuel_amount_cache = 0;
        this.tank_manager = new TankManager<Tank>();
        this.tank_manager.add(this.tank_fuel);
    }

    public abstract int gui_id();

    @Override
    public void readFromNBT(NBTTagCompound data)
    {
        super.readFromNBT(data);
        this.inv.readFromNBT(data);
        this.tank_manager.readFromNBT(data);
    }

    @Override
    public void writeToNBT(NBTTagCompound data)
    {
        super.writeToNBT(data);
        this.inv.writeToNBT(data);
        this.tank_manager.writeToNBT(data);
    }

    public abstract int max_fuel();

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

    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill)
    {
        if (resource != null && resource.getFluid() != null) {
            if (BuildcraftFuelRegistry.fuel.getFuel(resource.getFluid()) == null) {
                return 0;
            } else {
                int filled = this.tank_fuel.fill(resource, doFill);
                if (filled > 0 && this.tank_fuel.getFluid() != null && this.tank_fuel.getFluid().getFluid() != null && (this.current_fuel == null || this.tank_fuel.getFluid().getFluid() != this.current_fuel.getFluid())) {
                    this.current_fuel = BuildcraftFuelRegistry.fuel.getFuel(this.tank_fuel.getFluid().getFluid());
                }

                return filled;
            }
        } else {
            return 0;
        }
    }

    @Override
    public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain)
    {
        if (resource == null) {
            return null;
        } else {
            return this.tank_fuel.getFluidType() == resource.getFluid() ? this.tank_fuel.drain(resource.amount, doDrain) : null;
        }
    }

    @Override
    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain)
    {
        return this.tank_fuel.drain(maxDrain, doDrain);
    }

    public abstract boolean can_fill_from_side(ForgeDirection from);

    @Override
    public boolean canFill(ForgeDirection from, Fluid fluid)
    {
        return can_fill_from_side(from) && fluid != null && BuildcraftFuelRegistry.fuel.getFuel(fluid) != null;
    }

    public abstract boolean can_drain_from_side(ForgeDirection from);

    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid)
    {
        return can_fill_from_side(from);
    }

    @Override
    public FluidTankInfo[] getTankInfo(ForgeDirection from)
    {
        return this.tank_manager.getTankInfo(from);
    }

    public abstract boolean has_work();

    public abstract void work();

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

        FluidStack fuel = this.tank_fuel.getFluid();
        if (this.current_fuel == null && fuel != null) {
            this.current_fuel = BuildcraftFuelRegistry.fuel.getFuel(fuel.getFluid());
        }

        if (this.current_fuel != null) {
            if (this.isRedstonePowered) {
                if (fuel != null && fuel.amount > 0) {
                    if (--fuel.amount <= 0) {
                        this.tank_fuel.setFluid(null);
                    }
                    if (has_work()) work();
                }
            }
        }
    }

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

    public boolean onBlockActivated(EntityPlayer player, ForgeDirection side)
    {
        ItemStack current = player.getCurrentEquippedItem();
        if (current != null) {
            if (current.getItem() instanceof IItemPipe) {
                return false;
            }

            if (!this.worldObj.isRemote) {
                if (TankUtils.handleRightClick(this, side, player, true, true)) {
                    return true;
                }
            } else if (FluidContainerRegistry.isContainer(current)) {
                return true;
            }
        }

        if (!this.worldObj.isRemote && gui_id() != -1) {
            player.openGui(Minelife.instance, gui_id(), this.worldObj, this.xCoord, this.yCoord, this.zCoord);
        }

        return true;
    }

    public void getGUINetworkData(int id, int value) {
        switch(id) {
            case 0:
                if (FluidRegistry.getFluid(value) != null) {
                    this.tank_fuel.setFluid(new FluidStack(FluidRegistry.getFluid(value), this.tank_fuel_amount_cache));
                } else {
                    this.tank_fuel.setFluid((FluidStack)null);
                }
                break;
            case 1:
                this.tank_fuel_amount_cache = value;
                if (this.tank_fuel.getFluid() != null) {
                    this.tank_fuel.getFluid().amount = value;
                }
                break;
            case 2:
                this.tank_fuel.colorRenderCache = value;
                break;
        }

    }

    public void sendGUINetworkData(Container containerEngine, ICrafting iCrafting) {
        iCrafting.sendProgressBarUpdate(containerEngine, 0, this.tank_fuel.getFluid() != null && this.tank_fuel.getFluid().getFluid() != null ? this.tank_fuel.getFluid().getFluid().getID() : 0);
        iCrafting.sendProgressBarUpdate(containerEngine, 1, this.tank_fuel.getFluid() != null ? this.tank_fuel.getFluid().amount : 0);
        iCrafting.sendProgressBarUpdate(containerEngine, 2, this.tank_fuel.colorRenderCache);
    }

    public FluidStack fuel() {
        return this.tank_fuel.getFluid();
    }

}
