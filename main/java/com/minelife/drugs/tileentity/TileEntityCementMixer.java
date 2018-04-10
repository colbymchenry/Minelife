package com.minelife.drugs.tileentity;

import buildcraft.api.core.EnumPipePart;
import buildcraft.api.core.IFluidFilter;
import buildcraft.api.core.IFluidHandlerAdv;
import buildcraft.api.fuels.BuildcraftFuelRegistry;
import buildcraft.api.fuels.IFuel;
import buildcraft.api.tiles.IHasWork;
import buildcraft.api.tiles.TilesAPI;
import buildcraft.energy.BCEnergyFluids;
import buildcraft.lib.fluid.Tank;
import buildcraft.lib.fluid.TankManager;
import buildcraft.lib.fluid.TankProperties;
import buildcraft.lib.misc.CapUtil;
import buildcraft.lib.net.PacketBufferBC;
import buildcraft.lib.tile.TileBC_Neptune;
import buildcraft.lib.tile.item.ItemHandlerManager;
import buildcraft.lib.tile.item.ItemHandlerSimple;
import com.google.common.collect.Lists;
import com.minelife.drugs.ModDrugs;
import com.minelife.util.ItemHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.List;

public class TileEntityCementMixer extends TileBC_Neptune implements ITickable, IHasWork {

    public static final int MAX_FLUID = 10_000;
    public final Tank tankFuel = new Tank("fuel", MAX_FLUID, this, this::isValidFuel);
    public final Tank tankSolvent = new Tank("solvent", MAX_FLUID, this, this::isValidSolvent);
    private final IFluidHandlerAdv fluidHandler = new InternalFluidHandler();
    private IFuel currentFuel;

    public int progress = 0, processingTime = 512;

    public final ItemHandlerSimple invMaterials;
    public final ItemHandlerSimple invResult;

    private static List<Recipe> recipes = Lists.newArrayList();

    public TileEntityCementMixer() {
        invMaterials = itemManager.addInvHandler("input", 9, ItemHandlerManager.EnumAccess.INSERT, EnumPipePart.VALUES);
        invResult = itemManager.addInvHandler("result", 1, ItemHandlerManager.EnumAccess.EXTRACT, EnumPipePart.VALUES);
        caps.addCapabilityInstance(TilesAPI.CAP_HAS_WORK, this, EnumPipePart.VALUES);
        caps.addCapabilityInstance(CapUtil.CAP_FLUIDS, fluidHandler, EnumPipePart.VALUES);
        tankFuel.setCanDrain(true);
        tankSolvent.setCanDrain(true);
        tankManager.addAll(tankFuel, tankSolvent);

        recipes.add(new Recipe(new ItemStack(ModDrugs.itemWaxyCocaine), Lists.newArrayList(new ItemStack(ModDrugs.itemCocaLeafShredded), new ItemStack(ModDrugs.itemLime), new ItemStack(ModDrugs.itemSalt)), new FluidStack(BCEnergyFluids.fuelLight[0], 100)));
        recipes.add(new Recipe(new ItemStack(ModDrugs.itemCocaPaste), Lists.newArrayList(new ItemStack(ModDrugs.itemPressedCocaine)), new FluidStack(ModDrugs.fluidSulfuricAcid, 100)));
        recipes.add(new Recipe(new ItemStack(ModDrugs.itemPurpleCocaine), Lists.newArrayList(new ItemStack(ModDrugs.itemCocaPaste)), new FluidStack(ModDrugs.fluidPotassiumPermanganate, 100)));
        recipes.add(new Recipe(new ItemStack(ModDrugs.itemProcessedCocaine), Lists.newArrayList(new ItemStack(ModDrugs.itemPurpleCocaine)), new FluidStack(ModDrugs.fluidAmmonia, 100)));
    }

    private boolean isValidFuel(FluidStack fluid) {
        return BuildcraftFuelRegistry.fuel.getFuel(fluid) != null;
    }

    private boolean isValidSolvent(FluidStack fluid) {
        return fluid.getFluid() == ModDrugs.fluidAmmonia || fluid.getFluid() == ModDrugs.fluidPotassiumPermanganate ||
                fluid.getFluid() == ModDrugs.fluidSulfuricAcid;
    }

    @Override
    protected void onSlotChange(IItemHandlerModifiable handler, int slot, @Nonnull ItemStack before,
                                @Nonnull ItemStack after) {
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setTag("tanks", tankManager.serializeNBT());
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        tankManager.deserializeNBT(compound.getCompoundTag("tanks"));
    }

    @Override
    public void update() {
        if (getWorld().isRemote) {
            return;
        }

        sendNetworkUpdate(NET_GUI_DATA);

        if (!hasWork()) {
            progress = 0;
            return;
        }

        if (!burn()) return;

        this.progress++;

        if (this.progress >= this.processingTime) {
            this.progress = 0;
            Recipe recipe = getCurrentRecipe();
            for(int i = 0; i < invMaterials.getSlots(); i++) this.invMaterials.extractItem(i, 1, false);
            this.invResult.insertItem(0, recipe.getResult().copy(), false);

            if(recipe.getFluid().getFluid() == BCEnergyFluids.fuelLight[0]) {
                if (tankFuel.getFluid().amount - recipe.getFluid().amount > 0) {
                    tankFuel.getFluid().amount -= recipe.getFluid().amount;
                } else {
                    tankFuel.setFluid(null);
                }
            } else {
                if (tankSolvent.getFluid().amount - recipe.getFluid().amount > 0) {
                    tankSolvent.getFluid().amount -= recipe.getFluid().amount;
                } else {
                    tankSolvent.setFluid(null);
                }
            }
        }

    }

    public boolean burn() {
        final FluidStack fuel = this.tankFuel.getFluid();
        if (currentFuel == null || !currentFuel.getFluid().isFluidEqual(fuel)) {
            currentFuel = BuildcraftFuelRegistry.fuel.getFuel(fuel);
        }

        if (fuel == null || currentFuel == null) {
            return false;
        }

        if (fuel.amount > 0) {
            fuel.amount--;
            return true;
        } else {
            tankFuel.setFluid(null);
            currentFuel = null;
            return false;
        }
    }

    @Override
    public void writePayload(int id, PacketBufferBC buffer, Side side) {
        super.writePayload(id, buffer, side);
        if (side == Side.SERVER) {
            if (id == NET_GUI_DATA) {
                buffer.writeInt(this.progress);
                tankManager.writeData(buffer);
            }
        }
    }

    @Override
    public void readPayload(int id, PacketBufferBC buffer, Side side, MessageContext ctx) throws IOException {
        super.readPayload(id, buffer, side, ctx);
        if (side == Side.CLIENT) {
            if (id == NET_GUI_DATA) {
                this.progress = buffer.readInt();
                tankManager.readData(buffer);
            }
        }
    }

    @Override
    public boolean hasWork() {
        return getCurrentRecipe() != null;
    }

    public Recipe getCurrentRecipe() {
        for (Recipe recipe : recipes) {
            searchItems: for (ItemStack itemStack : recipe.getItems()) {
                boolean itemStackFound = false;
                for (ItemStack stack : invMaterials.stacks) {
                    if(ItemHelper.areStacksIdentical(stack, itemStack))
                        itemStackFound = true;
                }

                if(!itemStackFound) break searchItems;
                else {
                    if(tankSolvent.getFluid() != null && tankSolvent.getFluid().getFluid() == recipe.getFluid().getFluid()
                            && tankSolvent.getFluid().amount >= recipe.getFluid().amount) {
                        return recipe;
                    }

                    if(tankFuel.getFluid() != null && tankFuel.getFluid().getFluid() == recipe.getFluid().getFluid()
                            && tankFuel.getFluid().amount >= recipe.getFluid().amount) {
                        return recipe;
                    }
                }
            }
        }
        return null;
    }

    public int progressScaled(int i) {
        return this.progress * i / this.processingTime;
    }

    public TankManager getTankManager() {
        return this.tankManager;
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(getPos().getX(), getPos().getY() - 1, getPos().getZ(), getPos().getX() + 1, getPos().getY() + 1, getPos().getZ() + 1);
    }


    private class InternalFluidHandler implements IFluidHandlerAdv {

        private final IFluidTankProperties[] properties = { //
                new TankProperties(tankFuel, true, false), //
                new TankProperties(tankSolvent, true, false), //
        };

        @Override
        public IFluidTankProperties[] getTankProperties() {
            return properties;
        }

        @Override
        public int fill(FluidStack resource, boolean doFill) {
            int filled = tankFuel.fill(resource, doFill);
            if (filled == 0) filled = tankSolvent.fill(resource, doFill);
            return filled;
        }

        @Override
        public FluidStack drain(FluidStack resource, boolean doDrain) {
            if(BuildcraftFuelRegistry.fuel.getFuel(resource) != null) {
                return tankFuel.drain(resource, doDrain);
            } else {
                return tankSolvent.drain(resource, doDrain);
            }
        }

        @Override
        public FluidStack drain(int maxDrain, boolean doDrain) {
            return tankFuel.drain(maxDrain, doDrain);
        }

        @Override
        public FluidStack drain(IFluidFilter filter, int maxDrain, boolean doDrain) {
            return tankFuel.drain(filter, maxDrain, doDrain);
        }
    }


    private class Recipe {
        private List<ItemStack> items;
        private FluidStack fluid;
        private ItemStack result;

        public Recipe(ItemStack result, List<ItemStack> items, FluidStack fluid) {
            this.result = result;
            this.items = items;
            this.fluid = fluid;
        }

        public ItemStack getResult() {
            return result;
        }

        public List<ItemStack> getItems() {
            return items;
        }

        public FluidStack getFluid() {
            return fluid;
        }

    }

}