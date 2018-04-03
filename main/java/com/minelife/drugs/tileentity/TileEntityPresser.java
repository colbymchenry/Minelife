package com.minelife.drugs.tileentity;

import buildcraft.api.core.EnumPipePart;
import buildcraft.api.mj.IMjConnector;
import buildcraft.api.mj.IMjRedstoneReceiver;
import buildcraft.api.mj.MjAPI;
import buildcraft.api.mj.MjCapabilityHelper;
import buildcraft.api.tiles.IHasWork;
import buildcraft.api.tiles.TilesAPI;
import buildcraft.lib.net.PacketBufferBC;
import buildcraft.lib.tile.TileBC_Neptune;
import buildcraft.lib.tile.item.ItemHandlerManager;
import buildcraft.lib.tile.item.ItemHandlerSimple;
import com.minelife.drugs.ModDrugs;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ITickable;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;
import java.io.IOException;

public class TileEntityPresser extends TileBC_Neptune implements ITickable, IHasWork, IMjRedstoneReceiver {

    /**
     * A redstone engine generates <code> 1 * {@link MjAPI#MJ}</code> per tick. This makes it a lot slower without one
     * powering it.
     */
    // 1000000L/5
    private static final long POWER_GEN_PASSIVE = MjAPI.MJ / 5;

    /**
     * It takes 3 seconds to craft an item.
     */
    private static final long POWER_REQUIRED = POWER_GEN_PASSIVE * 20 * 3;

    private static final long POWER_LOST = POWER_GEN_PASSIVE * 10;

    public int progress = 0, processingTime = 32;

    public final ItemHandlerSimple invMaterials;
    public final ItemHandlerSimple invResult;

    /**
     * The amount of power that is stored until crafting can begin. When this reaches the minimum power required it
     * will craft the current recipe.
     */
    private long powerStored;

    public TileEntityPresser() {
        invMaterials = itemManager.addInvHandler("input", 1, ItemHandlerManager.EnumAccess.INSERT, EnumPipePart.VALUES);
        invResult = itemManager.addInvHandler("result", 1, ItemHandlerManager.EnumAccess.EXTRACT, EnumPipePart.VALUES);
        caps.addCapabilityInstance(TilesAPI.CAP_HAS_WORK, this, EnumPipePart.VALUES);
        caps.addProvider(new MjCapabilityHelper(this));
    }

    @Override
    protected void onSlotChange(IItemHandlerModifiable handler, int slot, @Nonnull ItemStack before,
                                @Nonnull ItemStack after) {
    }

    @Override
    public void update() {
        if (getWorld().isRemote) {
            return;
        }

        if (powerStored < POWER_REQUIRED) {
            powerStored += POWER_GEN_PASSIVE;
            return;
        }

        if (!hasWork()) return;

        this.progress += 2;

        if (this.progress >= this.processingTime) {
            this.progress = 0;
            this.invMaterials.extractItem(0, 1, false);
            this.invResult.insertItem(0, new ItemStack(ModDrugs.itemPressedCocaine), false);
            this.powerStored = this.invMaterials.stacks.get(0) == ItemStack.EMPTY ? 0 : 1;
        }

        sendNetworkUpdate(NET_GUI_DATA);
    }

    @Override
    public void writePayload(int id, PacketBufferBC buffer, Side side) {
        super.writePayload(id, buffer, side);
        if (side == Side.SERVER) {
           if (id == NET_GUI_DATA) buffer.writeInt(this.progress);
        }
    }

    @Override
    public void readPayload(int id, PacketBufferBC buffer, Side side, MessageContext ctx) throws IOException {
        super.readPayload(id, buffer, side, ctx);
        if (side == Side.CLIENT) {
            if (id == NET_GUI_DATA) this.progress = buffer.readInt();
        }
    }

    @Override
    public boolean hasWork() {
        return this.powerStored > 0 && invMaterials.stacks.get(0).getItem() == ModDrugs.itemHeatedCocaine && invResult.stacks.get(0).getCount() < 64;
    }

    @Override
    public boolean canConnect(@Nonnull IMjConnector other) {
        return true;
    }

    @Override
    public long getPowerRequested() {
        return POWER_REQUIRED - powerStored;
    }

    @Override
    public long receivePower(long microJoules, boolean simulate) {
        long req = getPowerRequested();
        long taken = Math.min(req, microJoules);
        if (!simulate) {
            powerStored += taken;
        }
        return microJoules - taken;
    }

    public int progressScaled(int i) {
        return this.progress * i / processingTime;
    }

}