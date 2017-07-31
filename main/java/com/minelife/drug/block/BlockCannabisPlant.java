package com.minelife.drug.block;

import com.minelife.Minelife;
import com.minelife.drug.item.ItemCannabisBuds;
import com.minelife.drug.item.ItemCocaLeaf;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import java.util.Random;

public class BlockCannabisPlant extends AbstractCrop {

    private static BlockCannabisPlant instance;
    private static Random random = new Random();

    private BlockCannabisPlant()
    {
    }

    public static BlockCannabisPlant instance() {
        if(instance == null) instance = new BlockCannabisPlant();
        return instance;
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float f1, float f2, float f3)
    {
        if (world.isRemote) return true;

        if (get_growth_stage(world, x, y, z) < max_growth_stage()) return false;

        // stop if player is holding bone meal
        if (player.getHeldItem() != null && player.getHeldItem().getItem() == Items.dye && player.getHeldItem().getItemDamage() == 15)
            return false;

        EntityItem entityitem = player.dropPlayerItemWithRandomChoice(new ItemStack(ItemCannabisBuds.instance(), MathHelper.getRandomIntegerInRange(random, 4, 6)), false);
        entityitem.delayBeforeCanPickup = 0;
        set_growth_stage(world, x, y, z, max_growth_stage() - 6);
        return true;
    }


    @Override
    public int chance_for_growth()
    {
        return 80;
    }

    @Override
    public int[] bonemeal_growth_range()
    {
        return new int[]{1, 1};
    }

    @Override
    public int max_growth_stage()
    {
        return 9;
    }

    @Override
    public void register_icons(IIcon[] icon_array, IIconRegister icon_register)
    {
        icon_array[0] = icon_register.registerIcon(Minelife.MOD_ID + ":cannabis_stage_0");
        icon_array[1] = icon_register.registerIcon(Minelife.MOD_ID + ":cannabis_stage_0");
        icon_array[2] = icon_register.registerIcon(Minelife.MOD_ID + ":cannabis_stage_1");
        icon_array[3] = icon_register.registerIcon(Minelife.MOD_ID + ":cannabis_stage_1");
        icon_array[4] = icon_register.registerIcon(Minelife.MOD_ID + ":cannabis_stage_2");
        icon_array[5] = icon_register.registerIcon(Minelife.MOD_ID + ":cannabis_stage_2");
        icon_array[6] = icon_register.registerIcon(Minelife.MOD_ID + ":cannabis_stage_3");
        icon_array[7] = icon_register.registerIcon(Minelife.MOD_ID + ":cannabis_stage_3");
        icon_array[8] = icon_register.registerIcon(Minelife.MOD_ID + ":cannabis_stage_4");
    }
}
