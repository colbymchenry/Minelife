package com.minelife.drug.tileentity;

import buildcraft.api.transport.IPipeTile;
import com.google.common.collect.Lists;
import com.minelife.drug.DrugsGuiHandler;
import com.minelife.drug.Recipe;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.List;

public class TileEntityCementMixer extends TileEntityElectricMachine {

    private static int[] input_slots = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8};
    private static int output_slot = 9;
    private static final List<Recipe> recipes = Lists.newArrayList();

    public TileEntityCementMixer()
    {
        super(10, "cement_mixer");
    }

    public static void add_recipe(ItemStack input, ItemStack... output) {
        recipes.add(new Recipe(input, output));
    }

    @Override
    public int gui_id()
    {
        return DrugsGuiHandler.cement_mixer_id;
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
        ItemStack[] items = new ItemStack[input_slots.length];
        for (int i = 0; i < input_slots.length; i++) {
            items[i] = getStackInSlot(i);
        }
        Recipe recipe = recipes.stream().filter(r -> r.matches(items)).findFirst().orElse(null);
        return recipe != null;
    }

    @Override
    public void work()
    {

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
}
