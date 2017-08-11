package com.minelife.drug.tileentity;

import buildcraft.BuildCraftCore;
import buildcraft.api.power.IRedstoneEngineReceiver;
import buildcraft.api.transport.IPipeTile;
import buildcraft.core.lib.utils.Utils;
import buildcraft.transport.TileGenericPipe;
import com.minelife.MLItems;
import com.minelife.drug.DrugsGuiHandler;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEntityPresser extends TileEntityElectricMachine implements IRedstoneEngineReceiver {

    private int slot_input = 0, slot_output = 1;

    public TileEntityPresser()
    {
        super(2, "presser");
    }

    @Override
    public int gui_id()
    {
        return DrugsGuiHandler.presser_id;
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
    public void updateEntity()
    {
        System.out.println(this.getBattery().getEnergyStored());
        super.updateEntity();
    }

    @Override
    public boolean has_work()
    {
        return this.getStackInSlot(slot_input) != null && this.getStackInSlot(slot_input).getItem() == MLItems.heated_cocaine && (this.getStackInSlot(slot_output) == null || (this.getStackInSlot(slot_output).getItem() == MLItems.pressed_cocaine && this.getStackInSlot(slot_output).stackSize < 64));
    }

    @Override
    public void work()
    {
        ItemStack output = getStackInSlot(slot_output);
        int output_stack_size = output == null ? 1 : output.stackSize + 1;
        ItemStack output_final = new ItemStack(MLItems.pressed_cocaine, output_stack_size);

        if(output_final != null) {
            this.decrStackSize(slot_input, 1);

            output_final.stackSize -= Utils.addToRandomInventoryAround(this.worldObj, this.xCoord, this.yCoord, this.zCoord, output_final);

            if (output_final.stackSize > 0) {
                output_final.stackSize -= Utils.addToRandomInjectableAround(this.worldObj, this.xCoord, this.yCoord, this.zCoord, ForgeDirection.UNKNOWN, output_final);
            }

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
        return false;
    }

    @Override
    public boolean canConnectRedstoneEngine(ForgeDirection forgeDirection)
    {
        return true;
    }
}
