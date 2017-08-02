package com.minelife.drug.tileentity;

import buildcraft.api.transport.IPipeTile;
import com.minelife.drug.DrugsGuiHandler;
import com.minelife.drug.item.ItemCannabisBuds;
import com.minelife.drug.item.ItemCannabisShredded;
import com.minelife.drug.item.ItemCocaLeaf;
import com.minelife.drug.item.ItemCocaLeafShredded;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEntityEntityLeafMulcher extends TileEntityMachine {

    private static final int slot_input = 0, slot_output = 1;

    public TileEntityEntityLeafMulcher()
    {
        super(3, "leaf_mulcher");
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

        if (input == null) return false;

        if (input.getItem() == ItemCannabisBuds.instance()) {
            if (output == null || (output.stackSize < 64 && output.getItem() == ItemCannabisShredded.instance()))
                return true;
        } else if (input.getItem() == ItemCocaLeaf.instance() && ItemCocaLeaf.get_moisture_level(input) == 0) {
            if (output == null || (output.stackSize < 64 && output.getItem() == ItemCocaLeafShredded.instance()))
                return true;
        }

        return false;
    }

    @Override
    public void work()
    {
        ItemStack input = getStackInSlot(slot_input);
        ItemStack output = getStackInSlot(slot_output);
        int output_stack_size = output == null ? 1 : output.stackSize + 1;
        ItemStack output_final = null;

        if (input.getItem() == ItemCannabisBuds.instance()) {
            output_final = new ItemStack(ItemCannabisShredded.instance(), output_stack_size);
        } else if (input.getItem() == ItemCocaLeaf.instance()  && ItemCocaLeaf.get_moisture_level(input) == 0) {
            output_final = new ItemStack(ItemCocaLeafShredded.instance(), output_stack_size);
        }

        // TODO: Add sound effects and fix gui by removing the fuel bucket slot

        setInventorySlotContents(slot_output, output_final);
        sendNetworkUpdate();
    }

    @Override
    public boolean can_pipe_connect(IPipeTile.PipeType pipe_type, ForgeDirection direction)
    {
        return pipe_type == IPipeTile.PipeType.FLUID || pipe_type == IPipeTile.PipeType.ITEM;
    }

    @Override
    public int[] get_accessible_slots_from_side(int side)
    {
        if (ForgeDirection.getOrientation(side) == ForgeDirection.UP) return new int[]{slot_input};
        if (ForgeDirection.getOrientation(side) == ForgeDirection.DOWN) return new int[]{slot_output};
        return new int[]{};
    }

    @Override
    public boolean can_insert_item(int slot, ItemStack stack, int side)
    {
        return slot == slot_input && ForgeDirection.getOrientation(side) == ForgeDirection.UP;
    }

    @Override
    public boolean can_extract_item(int slot, ItemStack stack, int side)
    {
        return slot == slot_output && ForgeDirection.getOrientation(side) == ForgeDirection.DOWN;
    }

    @Override
    public boolean is_useable_by_player(EntityPlayer player)
    {
        return true;
    }

    @Override
    public boolean is_item_valid_for_slot(int slot, ItemStack stack)
    {
        return stack != null && ((stack.getItem() == ItemCocaLeaf.instance()  && ItemCocaLeaf.get_moisture_level(stack) == 0) || stack.getItem() == ItemCannabisBuds.instance());
    }

}
