package com.minelife.drug.tileentity;

import buildcraft.BuildCraftCore;
import buildcraft.BuildCraftEnergy;
import buildcraft.api.fuels.IFuel;
import buildcraft.api.power.IRedstoneEngineReceiver;
import buildcraft.api.transport.IItemPipe;
import buildcraft.api.transport.IPipeTile;
import buildcraft.core.lib.fluids.Tank;
import buildcraft.core.lib.fluids.TankManager;
import buildcraft.core.lib.fluids.TankUtils;
import buildcraft.core.lib.utils.Utils;
import com.google.common.collect.Lists;
import com.minelife.MLItems;
import com.minelife.Minelife;
import com.minelife.drug.DrugsGuiHandler;
import com.minelife.drug.Recipe;
import com.sun.istack.internal.NotNull;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.*;

import java.util.List;

public class TileEntityCementMixer extends TileEntityElectricMachine implements IFluidHandler, IRedstoneEngineReceiver {

    private static int[] input_slots = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8};
    private static int slot_output = 9;
    private static final List<Recipe> recipes = Lists.newArrayList();

    private Tank tank;
    private int tank_fuel_amount_cache;
    private TankManager<Tank> tank_manager;

    public TileEntityCementMixer()
    {
        super(10, "cement_mixer");
        this.tank = new Tank("tank", 10000, this);
        this.tank_fuel_amount_cache = 0;
        this.tank_manager = new TankManager<Tank>();
        this.tank_manager.add(this.tank);
    }

    public static void add_recipe(Recipe recipe)
    {
        recipes.add(recipe);
    }

    @Override
    public void writeToNBT(NBTTagCompound data)
    {
        super.writeToNBT(data);
        this.tank_manager.writeToNBT(data);
    }

    @Override
    public void readFromNBT(NBTTagCompound data)
    {
        super.readFromNBT(data);
        this.tank_manager.readFromNBT(data);
    }

    @Override
    public int gui_id()
    {
        return DrugsGuiHandler.cement_mixer_id;
    }

    @Override
    public int max_energy_storage()
    {
        return 30720;
    }

    @Override
    public int max_energy_receive()
    {
        return 1000;
    }

    @Override
    public int max_energy_extract()
    {
        return 0;
    }

    @Override
    public int update_time()
    {
        return 2;
    }

    @Override
    public int processing_time()
    {
        return 256;
    }

    @Override
    public int min_energy_extract()
    {
        return 20;
    }

    @Override
    public boolean has_work()
    {
        ItemStack[] inputs = get_inputs();
        Recipe recipe = recipes.stream().filter(r -> r.matches(new FluidStack[]{fluid()}, inputs)).findFirst().orElse(null);
        if (recipe != null) {
            if (recipe.output() != null) {
                if (getStackInSlot(slot_output) != null && getStackInSlot(slot_output).getItem() == recipe.output().getItem())
                    return true;
            }
        }
        return recipe != null && recipe.output() != null;
    }

    @Override
    public void work()
    {
        ItemStack[] inputs = get_inputs();
        Recipe recipe = recipes.stream().filter(r -> r.matches(new FluidStack[]{fluid()}, inputs)).findFirst().orElse(null);
        ItemStack output_final = recipe.process(new FluidStack[]{fluid()}, inputs);

        if (output_final != null) {

            output_final.stackSize -= Utils.addToRandomInventoryAround(this.worldObj, this.xCoord, this.yCoord, this.zCoord, output_final);

            if (output_final.stackSize > 0) {
                output_final.stackSize -= Utils.addToRandomInjectableAround(this.worldObj, this.xCoord, this.yCoord, this.zCoord, ForgeDirection.DOWN, output_final);
            }

            output_final.stackSize -= add_to_output_slot(output_final);

            if (output_final.stackSize > 0) {
                float f = this.worldObj.rand.nextFloat() * 0.8F + 0.1F;
                float f1 = this.worldObj.rand.nextFloat() * 0.8F + 0.1F;
                float f2 = this.worldObj.rand.nextFloat() * 0.8F + 0.1F;
                EntityItem entityitem = new EntityItem(this.worldObj, (double) ((float) this.xCoord + f), (double) ((float) this.yCoord + f1 - 0.5F), (double) ((float) this.zCoord + f2), output_final);
                entityitem.lifespan = BuildCraftCore.itemLifespan * 20;
                entityitem.delayBeforeCanPickup = 10;
                float f3 = 0.05F;
                entityitem.motionX = (double) ((float) this.worldObj.rand.nextGaussian() * f3);
                entityitem.motionY = (double) ((float) this.worldObj.rand.nextGaussian() * f3 - 1.0F);
                entityitem.motionZ = (double) ((float) this.worldObj.rand.nextGaussian() * f3);
                this.worldObj.spawnEntityInWorld(entityitem);
            }

            sendNetworkUpdate();
        }
    }

    long startTime = System.currentTimeMillis();

    @Override
    public void on_progress_increment()
    {
        if (System.currentTimeMillis() - startTime > 3000) {
            worldObj.playSoundEffect(xCoord, yCoord, zCoord, Minelife.MOD_ID + ":cement_mixer", 0.5F, worldObj.rand.nextFloat() * 0.1F + 0.9F);
            startTime = System.currentTimeMillis();
        }
    }

    @Override
    public boolean can_pipe_connect(IPipeTile.PipeType pipe_type, ForgeDirection direction)
    {
        return pipe_type == IPipeTile.PipeType.ITEM || pipe_type == IPipeTile.PipeType.FLUID;
    }

    @Override
    public boolean requires_redstone_power()
    {
        return false;
    }

    @Override
    public int[] get_accessible_slots_from_side(int side)
    {
        return new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
    }

    @Override
    public boolean can_insert_item(int slot, ItemStack stack, int side)
    {
        return slot < input_slots.length;
    }

    @Override
    public boolean can_extract_item(int slot, ItemStack stack, int side)
    {
        return slot == slot_output;
    }

    @Override
    public boolean is_useable_by_player(EntityPlayer player)
    {
        return true;
    }

    @Override
    public boolean is_item_valid_for_slot(int slot, ItemStack stack)
    {
        return true;
    }

    public ItemStack[] get_inputs()
    {
        List<ItemStack> items = Lists.newArrayList();
        for (int i = 0; i < input_slots.length; i++) {
            if (getStackInSlot(i) != null) items.add(getStackInSlot(i));
        }
        return items.toArray(new ItemStack[items.size()]);
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox()
    {
        return AxisAlignedBB.getBoundingBox(xCoord, yCoord - 1, zCoord, xCoord + 1, yCoord + 1, zCoord + 1);
    }

    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill)
    {
        if (resource != null && resource.getFluid() != null) {
            return this.tank.fill(resource, doFill);
        }
        return 0;
    }

    @Override
    public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain)
    {
        if (resource == null) {
            return null;
        } else {
            return this.tank.drain(resource.amount, doDrain);
        }
    }

    @Override
    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain)
    {
        return this.tank.drain(maxDrain, doDrain);
    }

    @Override
    public boolean canFill(ForgeDirection from, Fluid fluid)
    {
        return from != ForgeDirection.UP && from != ForgeDirection.DOWN;
    }

    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid)
    {
        return from != ForgeDirection.UP && from != ForgeDirection.DOWN;
    }

    @Override
    public FluidTankInfo[] getTankInfo(ForgeDirection from)
    {
        return this.tank_manager.getTankInfo(from);
    }

    @Override
    public boolean onBlockActivated(EntityPlayer player)
    {
        ItemStack current = player.getCurrentEquippedItem();
        if (current != null) {
            if (current.getItem() instanceof IItemPipe) {
                return false;
            }
// TODO: when right clicking while shifted with ammonia while fuel is in it crashes client
            if (!this.worldObj.isRemote) {
                if (TankUtils.handleRightClick(this, ForgeDirection.EAST, player, true, true)) {
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

    @Override
    public void getGUINetworkData(int id, int value)
    {
        super.getGUINetworkData(id, value);
        switch (id) {
            case 1:
                if (FluidRegistry.getFluid(value) != null) {
                    this.tank.setFluid(new FluidStack(FluidRegistry.getFluid(value), this.tank_fuel_amount_cache));
                } else {
                    this.tank.setFluid((FluidStack) null);
                }
                break;
            case 2:
                this.tank_fuel_amount_cache = value;
                if (this.tank.getFluid() != null) {
                    this.tank.getFluid().amount = value;
                }
                break;
            case 3:
                this.tank.colorRenderCache = value;
                break;
        }

    }

    @Override
    public void sendGUINetworkData(Container container, ICrafting iCrafting)
    {
        super.sendGUINetworkData(container, iCrafting);
        iCrafting.sendProgressBarUpdate(container, 1, this.tank.getFluid() != null && this.tank.getFluid().getFluid() != null ? this.tank.getFluid().getFluid().getID() : 0);
        iCrafting.sendProgressBarUpdate(container, 2, this.tank.getFluid() != null ? this.tank.getFluid().amount : 0);
        iCrafting.sendProgressBarUpdate(container, 3, this.tank.colorRenderCache);
    }

    public FluidStack fluid()
    {
        return this.tank.getFluid();
    }

    private int add_to_output_slot(ItemStack output) {
        ItemStack in_output = getStackInSlot(slot_output);

        if(in_output == null) {
            setInventorySlotContents(slot_output, new ItemStack(output.getItem(), output.stackSize));
            return output.stackSize;
        }

        if(!in_output.isItemEqual(output)) {
            return 0;
        }

        if(in_output.stackSize == 64) {
            return 0;
        }

        if(in_output.stackSize + output.stackSize > 64) {
            int difference = in_output.stackSize - 64;
            setInventorySlotContents(slot_output, new ItemStack(output.getItem(), 64));
            return output.stackSize - difference;
        }

        setInventorySlotContents(slot_output, new ItemStack(output.getItem(), output.stackSize + in_output.stackSize));
        return output.stackSize;
    }

    @Override
    public boolean canConnectRedstoneEngine(ForgeDirection forgeDirection)
    {
        return true;
    }
}
