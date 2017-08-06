package com.minelife.drug.tileentity;

import buildcraft.api.transport.IPipeTile;
import com.google.common.collect.Lists;
import com.minelife.drug.DrugsGuiHandler;
import com.minelife.drug.Recipe;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.List;

public class TileEntityCementMixer extends TileEntityMachine {

    private static int[] input_slots = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8};
    private static int output_slot = 9;
    private static final List<Recipe> recipes = Lists.newArrayList();
    public static final int max_time = 256;
    public static final int update_time = 16;
    private int progress = 0;

    public TileEntityCementMixer()
    {
        super(9, "cement_mixer");
    }

    @Override
    public int gui_id()
    {
        return DrugsGuiHandler.cement_mixer_id;
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
        ItemStack[] inputs = new ItemStack[input_slots.length];
        for (int i = 0; i < input_slots.length; i++) {
            inputs[i] = getStackInSlot(i);
        }
        Recipe recipe = recipes.stream().filter(r -> r.matches(inputs)).findFirst().orElse(null);
        return recipe != null;
    }

    @Override
    public void work()
    {
        this.progress += update_time;

        if (this.progress >= max_time) {
            this.progress = 0;
        }
    }

    @Override
    public boolean can_pipe_connect(IPipeTile.PipeType pipe_type, ForgeDirection direction)
    {
        return pipe_type == IPipeTile.PipeType.ITEM || pipe_type == IPipeTile.PipeType.FLUID;
    }

    @Override
    public boolean requires_redstone_power()
    {
        return false;
    }

    @Override
    public int[] get_accessible_slots_from_side(int side)
    {
        return new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
    }

    @Override
    public boolean can_insert_item(int slot, ItemStack stack, int side)
    {
        return slot < input_slots.length;
    }

    @Override
    public boolean can_extract_item(int slot, ItemStack stack, int side)
    {
        return slot == output_slot;
    }

    @Override
    public boolean is_useable_by_player(EntityPlayer player)
    {
        return true;
    }

    @Override
    public boolean is_item_valid_for_slot(int slot, ItemStack stack)
    {
        return true;
    }

    public static void add_recipe(ItemStack input, ItemStack... output) {
        recipes.add(new Recipe(input, output));
    }

    public int progress_scaled(int i) {
        return this.progress * i / max_time;
    }

    public int progress()
    {
        return progress;
    }

    public void set_progress(int progress) {
        this.progress = progress;
    }
}
