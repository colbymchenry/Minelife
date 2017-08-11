package com.minelife.drug.tileentity;

import buildcraft.BuildCraftCore;
import buildcraft.api.power.IRedstoneEngineReceiver;
import buildcraft.api.transport.IPipeTile;
import buildcraft.core.lib.utils.Utils;
import com.minelife.MLItems;
import com.minelife.drug.DrugsGuiHandler;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEntityVacuum extends TileEntityElectricMachine implements IRedstoneEngineReceiver {

    private int slot_input = 0, slot_output = 1;

    public TileEntityVacuum()
    {
        super(2, "vacuum");
    }

    @Override
    public int gui_id()
    {
        return DrugsGuiHandler.vacuum_id;
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
        return 32;
    }

    @Override
    public int min_energy_extract()
    {
        return 20;
    }

    @Override
    public boolean has_work()
    {
        return this.getStackInSlot(slot_input) != null && this.getStackInSlot(slot_input).getItem() == MLItems.coca_leaf;
    }

    @Override
    public void work()
    {
        ItemStack output = new ItemStack(MLItems.coca_leaf, 1, 100);

        if (output != null) {
            this.decrStackSize(slot_input, 1);

            if (getStackInSlot(slot_output) == null || getStackInSlot(slot_output).stackSize < 64) {
                int size = getStackInSlot(slot_output) == null ? 1 : getStackInSlot(slot_output).stackSize + 1;
                setInventorySlotContents(slot_output, new ItemStack(MLItems.coca_leaf, size, 100));
                output.stackSize -= size;
            }

            if (output.stackSize > 0) {
                output.stackSize -= Utils.addToRandomInventoryAround(this.worldObj, this.xCoord, this.yCoord, this.zCoord, output);
            }

            if (output.stackSize > 0) {
                output.stackSize -= Utils.addToRandomInjectableAround(this.worldObj, this.xCoord, this.yCoord, this.zCoord, ForgeDirection.UNKNOWN, output);
            }


            if (output.stackSize > 0) {
                float f = this.worldObj.rand.nextFloat() * 0.8F + 0.1F;
                float f1 = this.worldObj.rand.nextFloat() * 0.8F + 0.1F;
                float f2 = this.worldObj.rand.nextFloat() * 0.8F + 0.1F;
                EntityItem entityitem = new EntityItem(this.worldObj, (double) ((float) this.xCoord + f), (double) ((float) this.yCoord + f1 - 0.5F), (double) ((float) this.zCoord + f2), output);
                entityitem.lifespan = BuildCraftCore.itemLifespan * 20;
                entityitem.delayBeforeCanPickup = 10;
                float f3 = 0.05F;
                entityitem.motionX = (double) ((float) this.worldObj.rand.nextGaussian() * f3);
                entityitem.motionY = (double) ((float) this.worldObj.rand.nextGaussian() * f3 - 1.0F);
                entityitem.motionZ = (double) ((float) this.worldObj.rand.nextGaussian() * f3);
                this.worldObj.spawnEntityInWorld(entityitem);
            }
        }
    }

    @Override
    public void on_progress_increment()
    {

    }

    @Override
    public boolean can_pipe_connect(IPipeTile.PipeType pipe_type, ForgeDirection direction)
    {
        return pipe_type == IPipeTile.PipeType.ITEM || pipe_type == IPipeTile.PipeType.POWER;
    }

    @Override
    public boolean requires_redstone_power()
    {
        return false;
    }

    @Override
    public int[] get_accessible_slots_from_side(int side)
    {
        return new int[]{slot_input, slot_output};
    }

    @Override
    public boolean can_insert_item(int slot, ItemStack stack, int side)
    {
        return slot == slot_input && stack != null;
    }

    @Override
    public boolean can_extract_item(int slot, ItemStack stack, int side)
    {
        return slot == slot_output && stack != null;
    }

    @Override
    public boolean is_useable_by_player(EntityPlayer player)
    {
        return true;
    }

    @Override
    public boolean is_item_valid_for_slot(int slot, ItemStack stack)
    {
        return stack != null && stack.getItem() == MLItems.coca_leaf;
    }

    @Override
    public boolean canConnectRedstoneEngine(ForgeDirection forgeDirection)
    {
        return true;
    }

}
