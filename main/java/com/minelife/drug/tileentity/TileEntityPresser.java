package com.minelife.drug.tileentity;

import buildcraft.api.transport.IPipeTile;
import com.minelife.drug.DrugsGuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEntityPresser extends TileEntityElectricMachine {

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
    public boolean has_work()
    {
        return false;
    }

    @Override
    public void work()
    {

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
}
