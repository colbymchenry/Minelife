package com.minelife.drug.tileentity;

import buildcraft.api.transport.IPipeTile;
import com.minelife.drug.DrugsGuiHandler;
import com.minelife.drug.item.ItemAmmonia;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEntityAmmoniaExtractor extends TileEntityMachine
{

    public static final int extraction_time = 256;
    private static final int slot_output = 0;
    private int progress;
    private int update;

    public TileEntityAmmoniaExtractor()
    {
        super(1, "ammonia_extractor");
    }

    @Override
    public int gui_id()
    {
        return DrugsGuiHandler.ammonia_extractor_id;
    }

    @Override
    public int max_fuel()
    {
        return 10000;
    }

    @Override
    public boolean can_fill_from_side(ForgeDirection from)
    {
        return true;
    }

    @Override
    public boolean can_drain_from_side(ForgeDirection from)
    {
        return true;
    }

    @Override
    public boolean has_work()
    {
        ItemStack output_stack = getStackInSlot(slot_output);
        if(output_stack != null && output_stack.stackSize > 63) return false;

        if(fuel() != null && fuel().amount > 20) {
            int updateNext = this.update + this.getBattery().getEnergyStored() + 1;
            int updateThreshold = (this.update & -16) + 16;
            this.update = Math.min(updateThreshold, updateNext);
            if (this.update % 16 == 0) return true;
        }

        return false;
    }

    @Override
    public void work()
    {
        this.progress += 16;

        if (this.progress >= extraction_time) {
            this.progress = 0;
            ItemStack output_stack = getStackInSlot(slot_output);
            ItemStack output_stack_final = new ItemStack(ItemAmmonia.instance(), output_stack == null ? 1 : output_stack.stackSize + 1);
            setInventorySlotContents(slot_output, output_stack_final);
            sendNetworkUpdate();
        }
    }

    @Override
    public boolean can_pipe_connect(IPipeTile.PipeType pipe_type, ForgeDirection direction)
    {
        return pipe_type == IPipeTile.PipeType.FLUID;
    }

    @Override
    public int[] get_accessible_slots_from_side(int side)
    {
        return new int[]{slot_output};
    }

    @Override
    public boolean can_insert_item(int slot, ItemStack stack, int side)
    {
        return false;
    }

    @Override
    public boolean can_extract_item(int slot, ItemStack stack, int side)
    {
        return true;
    }

    @Override
    public boolean is_useable_by_player(EntityPlayer player)
    {
        return true;
    }

    @Override
    public boolean is_item_valid_for_slot(int slot, ItemStack stack)
    {
        return stack != null && stack.getItem() == ItemAmmonia.instance();
    }

    public int get_progress_scaled(int i) {
        return this.progress * i / extraction_time;
    }

    public int progress() {
        return progress;
    }

    public void set_progress(int progress) {
        this.progress = progress;
    }
}
