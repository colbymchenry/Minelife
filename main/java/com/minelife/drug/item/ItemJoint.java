package com.minelife.drug.item;

import com.minelife.drug.ModDrugs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public class ItemJoint extends ItemAbstractDrug {

    public ItemJoint()
    {
        super("joint");
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int m, float f, float f1, float f2)
    {
        if(world.isRemote) return true;
        player.addPotionEffect(new PotionEffect(ModDrugs.x_ray_potion.id, 15 * 20, 0, false));
        player.addPotionEffect(new PotionEffect(Potion.nightVision.id, 60 * 20, 0, false));
        player.addPotionEffect(new PotionEffect(Potion.waterBreathing.id, 60 * 20, 0, false));
        player.addPotionEffect(new PotionEffect(Potion.hunger.id, 60 * 20, 1, false));
        return true;
    }

}
