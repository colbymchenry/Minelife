package com.minelife.drug.tileentity;

import buildcraft.BuildCraftEnergy;
import buildcraft.api.transport.IPipeTile;
import com.minelife.drug.DrugsGuiHandler;
import com.minelife.drug.item.ItemCannabisBuds;
import com.minelife.drug.item.ItemCannabisShredded;
import com.minelife.drug.item.ItemCocaLeaf;
import com.minelife.drug.item.ItemCocaLeafShredded;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEntityLeafMulcher extends TileMachine {

    private static final int slot_fuel = 0, slot_input = 1, slot_output = 2;

    public TileEntityLeafMulcher()
    {
        super(3, "Leaf Mulcher");
    }

    @Override
    public int gui_id()
    {
        return DrugsGuiHandler.leaf_mulcher_id;
    }

    @Override
    public int max_fuel()
    {
        return 10000;
    }

    @Override
    public boolean can_fill_from_side(ForgeDirection from)
    {
        return from != ForgeDirection.UP && from != ForgeDirection.DOWN;
    }

    @Override
    public boolean can_drain_from_side(ForgeDirection from)
    {
        return from != ForgeDirection.UP && from != ForgeDirection.DOWN;
    }

    @Override
    public boolean has_work()
    {
        ItemStack input = getStackInSlot(slot_input);
        ItemStack output = getStackInSlot(slot_output);

        if(input == null) return false;

        if(input.getItem() == ItemCannabisBuds.instance()) {
            if(output == null || (output.stackSize < 64 && output.getItem() == ItemCannabisShredded.instance())) return true;
        } else if (input.getItem() == ItemCocaLeaf.instance(true)) {
            if(output == null || (output.stackSize < 64 && output.getItem() == ItemCocaLeafShredded.instance())) return true;
        }

        return false;
    }

    // TODO: Need to work on progress and do the actual "work" Nice!
    @Override
    public void work()
    {
        System.out.println("CALLED");
    }

    @Override
    public boolean can_pipe_connect(IPipeTile.PipeType pipe_type, ForgeDirection direction)
    {
        return true;
    }

    @Override
    public int[] get_accessible_slots_from_side(int side)
    {
        if(ForgeDirection.getOrientation(side) == ForgeDirection.UP) return new int[]{slot_input};
        if(ForgeDirection.getOrientation(side) == ForgeDirection.DOWN) return new int[]{slot_output};
        return new int[]{slot_fuel};
    }

    @Override
    public boolean can_insert_item(int slot, ItemStack stack, int side)
    {
        return true;
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
        return slot != slot_fuel || stack.getItem() == BuildCraftEnergy.bucketFuel;
    }

}
