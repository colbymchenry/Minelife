package com.minelife.drug.tileentity;

import buildcraft.BuildCraftCore;
import buildcraft.api.transport.IPipeTile;
import buildcraft.core.lib.RFBattery;
import com.minelife.MLItems;
import com.minelife.Minelife;
import com.minelife.drug.DrugsGuiHandler;
import com.minelife.drug.ModDrugs;
import com.minelife.drug.item.ItemCannabisBuds;
import com.minelife.drug.item.ItemCannabisShredded;
import com.minelife.drug.item.ItemCocaLeaf;
import com.minelife.drug.item.ItemCocaLeafShredded;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEntityLeafMulcher extends TileEntityElectricMachine {

    private static final int slot_input = 0, slot_output = 1;

    public TileEntityLeafMulcher()
    {
        super(2, "leaf_mulcher");
        this.setBattery(new RFBattery((int)(20480.0F * BuildCraftCore.miningMultiplier), (int)(1000.0F * BuildCraftCore.miningMultiplier), 0));
    }

    @Override
    public int gui_id()
    {
        return DrugsGuiHandler.leaf_mulcher_id;
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
        return 16;
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
        ItemStack input = getStackInSlot(slot_input);
        ItemStack output = getStackInSlot(slot_output);

        if (input == null) return false;
        if (input.getItem() == MLItems.cannabis_buds) {
            if (output == null || (output.stackSize < 64 && output.getItem() == MLItems.cannabis_shredded))
                return true;
        } else if (input.getItem() == MLItems.coca_leaf && MLItems.coca_leaf.get_moisture_level(input) == 0) {
            if (output == null || (output.stackSize < 64 && output.getItem() == MLItems.coca_leaf_shredded))
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

        if (input.getItem() == MLItems.cannabis_buds) {
            output_final = new ItemStack(MLItems.cannabis_shredded, output_stack_size);
        } else if (input.getItem() == MLItems.coca_leaf  && MLItems.coca_leaf.get_moisture_level(input) == 0) {
            output_final = new ItemStack(MLItems.coca_leaf_shredded, output_stack_size);
        }

        if(output_final != null) {
            this.decrStackSize(slot_input, 1);
            setInventorySlotContents(slot_output, output_final);
            sendNetworkUpdate();
            worldObj.playSoundEffect(xCoord, yCoord, zCoord, Minelife.MOD_ID + ":leaf_mulcher", 0.5F, worldObj.rand.nextFloat() * 0.1F + 0.9F);
        }

        // TODO: Remove output slot and just make the product go straight into the pipe or into the world. Checkout TileQuary and BlockMiner
    }

    @Override
    public boolean can_pipe_connect(IPipeTile.PipeType pipe_type, ForgeDirection direction)
    {
        return pipe_type == IPipeTile.PipeType.ITEM;
    }

    @Override
    public boolean requires_redstone_power()
    {
        return false;
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
        return stack != null && ((stack.getItem() == MLItems.coca_leaf  && MLItems.coca_leaf.get_moisture_level(stack) == 0) || stack.getItem() == MLItems.cannabis_buds);
    }

}
