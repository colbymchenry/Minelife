package com.minelife.drugs.tileentity;

import buildcraft.api.core.EnumPipePart;
import buildcraft.api.fuels.BuildcraftFuelRegistry;
import buildcraft.api.fuels.IFuel;
import buildcraft.api.tiles.IHasWork;
import buildcraft.api.tiles.TilesAPI;
import buildcraft.lib.fluid.Tank;
import buildcraft.lib.fluid.TankManager;
import buildcraft.lib.misc.CapUtil;
import buildcraft.lib.net.PacketBufferBC;
import buildcraft.lib.tile.TileBC_Neptune;
import buildcraft.lib.tile.item.ItemHandlerManager;
import buildcraft.lib.tile.item.ItemHandlerSimple;
import com.minelife.drugs.ModDrugs;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;
import java.io.IOException;

public class TileEntityLeafMulcher extends TileBC_Neptune implements ITickable, IHasWork {

    public static final int MAX_FLUID = 10_000;
    public final Tank tankFuel = new Tank("fuel", MAX_FLUID, this, this::isValidFuel);
    private IFuel currentFuel;

    public int progress = 0, processingTime = 32;

    public final ItemHandlerSimple invMaterials;
    public final ItemHandlerSimple invResult;

    public TileEntityLeafMulcher() {
        invMaterials = itemManager.addInvHandler("input", 1, ItemHandlerManager.EnumAccess.INSERT, EnumPipePart.VALUES);
        invResult = itemManager.addInvHandler("result", 1, ItemHandlerManager.EnumAccess.EXTRACT, EnumPipePart.VALUES);
        caps.addCapabilityInstance(TilesAPI.CAP_HAS_WORK, this, EnumPipePart.VALUES);
        caps.addCapabilityInstance(CapUtil.CAP_FLUIDS, tankFuel, EnumPipePart.VALUES);
        tankFuel.setCanDrain(true);
        tankManager.add(tankFuel);
    }

    private boolean isValidFuel(FluidStack fluid) {
        return BuildcraftFuelRegistry.fuel.getFuel(fluid) != null;
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

        this.progress += 2;

        if (this.progress >= this.processingTime) {
            this.progress = 0;
            this.invMaterials.extractItem(0, 1, false);
            this.invResult.insertItem(0, new ItemStack(ModDrugs.itemCocaLeafShredded), false);
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
        return invMaterials.stacks.get(0).getItem() == ModDrugs.itemCocaLeaf && invMaterials.stacks.get(0).getItemDamage() == 100 && invResult.stacks.get(0).getCount() < 64;
    }

    public int progressScaled(int i) {
        return this.progress * i / processingTime;
    }

    public TankManager getTankManager() {
        return this.tankManager;
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(getPos().getX(), getPos().getY() - 1, getPos().getZ(), getPos().getX() + 1, getPos().getY() + 1, getPos().getZ() + 1);
    }

}