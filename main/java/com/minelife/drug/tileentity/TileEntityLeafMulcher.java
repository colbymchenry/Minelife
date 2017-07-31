package com.minelife.drug.tileentity;

import buildcraft.api.core.StackKey;
import buildcraft.api.fuels.BuildcraftFuelRegistry;
import buildcraft.api.fuels.IFuel;
import buildcraft.api.tiles.IHasWork;
import buildcraft.api.transport.IItemPipe;
import buildcraft.core.lib.engines.TileEngineWithInventory;
import buildcraft.core.lib.fluids.Tank;
import buildcraft.core.lib.fluids.TankManager;
import buildcraft.core.lib.fluids.TankUtils;
import buildcraft.core.lib.inventory.StackHelper;
import buildcraft.core.lib.utils.Utils;
import com.minelife.drug.client.gui.GuiLeafMulcher;
import com.minelife.drug.item.ItemCocaLeaf;
import com.minelife.drug.item.ItemCocaLeafShredded;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.*;

public class TileEntityLeafMulcher extends TileEngineWithInventory implements ISidedInventory, IHasWork, IFluidHandler {

    private static int slot_input = 0, slot_output = 1, slot_fuel = 2;
    public static int max_liquid = 10000;
    public Tank tank_fuel;
    private int burn_time;
    private int tank_fuel_amount_cache;
    private TankManager<Tank> tank_manager;
    private IFuel current_fuel;

    public TileEntityLeafMulcher()
    {
        super(3);
        this.tank_fuel = new Tank("tank_fuel", max_liquid, this);
        this.burn_time = 0;
        this.tank_fuel_amount_cache = 0;
        this.tank_manager = new TankManager<Tank>();
        this.tank_manager.add(this.tank_fuel);
    }

    @Override
    public boolean onBlockActivated(EntityPlayer player, ForgeDirection side)
    {
        if (super.onBlockActivated(player, side)) {
            return true;
        } else {
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

            if (!this.worldObj.isRemote) {
                Minecraft.getMinecraft().displayGuiScreen(new GuiLeafMulcher(player.inventory, this));
            }

            return true;
        }
    }

    @Override
    public boolean isBurning()
    {
        FluidStack fuel = this.tank_fuel.getFluid();
        return fuel != null && fuel.amount > 0 && this.isRedstonePowered;
    }

    @Override
    public int getMaxEnergy()
    {
        return 0;
    }

    @Override
    public int getIdealOutput()
    {
        return 0;
    }

    @Override
    public boolean hasWork()
    {
        ItemStack stack = this.getStackInSlot(slot_input);
        return stack != null && stack.getItem() == ItemCocaLeaf.instance(true);
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int side)
    {
        return new int[]{slot_input, slot_output, slot_fuel};
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack stack, int side)
    {
        return (slot == slot_input || slot == slot_fuel) && StackHelper.canStacksMerge(stack, this.getStackInSlot(slot));
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack stack, int side)
    {
        return slot == slot_output && stack != null && stack.getItem() == ItemCocaLeafShredded.instance();
    }

    @Override
    public String getInventoryName()
    {
        return "leaf_mulcher";
    }

    @Override
    public boolean hasCustomInventoryName()
    {
        return true;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player)
    {
        return true;
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack)
    {
        if (stack == null) {
            return false;
        }

        if (slot == slot_fuel) {
            FluidStack fluidStack = FluidContainerRegistry.getFluidForFilledItem(stack);
            return fluidStack != null && this.canFill(ForgeDirection.UNKNOWN, fluidStack.getFluid());
        }

        if (slot == slot_output && stack.getItem() == ItemCocaLeafShredded.instance()) return true;

        return true;
    }

    // ----- fluid handler methods -----

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
    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
        return this.tank_fuel.drain(maxDrain, doDrain);
    }

    @Override
    public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
        if (resource == null) {
            return null;
        } else {
            return this.tank_fuel.getFluidType() == resource.getFluid() ? this.tank_fuel.drain(resource.amount, doDrain) : null;
        }
    }

    @Override
    public boolean canFill(ForgeDirection from, Fluid fluid)
    {
        return from != this.orientation && fluid != null && (BuildcraftFuelRegistry.coolant.getCoolant(fluid) != null || BuildcraftFuelRegistry.fuel.getFuel(fluid) != null);
    }

    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid)
    {
        return from != this.orientation;
    }

    @Override
    public FluidTankInfo[] getTankInfo(ForgeDirection from)
    {
        return this.tank_manager.getTankInfo(from);
    }
}
