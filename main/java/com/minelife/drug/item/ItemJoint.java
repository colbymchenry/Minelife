package com.minelife.drug.item;

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
        player.addPotionEffect(new PotionEffect(Potion.digSpeed.id, 20 * 300, 3, false));
        player.addPotionEffect(new PotionEffect(Potion.moveSpeed.id, 20 * 300, 3, false));
        player.addPotionEffect(new PotionEffect(Potion.jump.id, 20 * 300, 2, false));
        player.addPotionEffect(new PotionEffect(Potion.resistance.id, 20 * 300, 1, false));
        player.addPotionEffect(new PotionEffect(Potion.fireResistance.id, 20 * 300, 1, false));
        player.addPotionEffect(new PotionEffect(Potion.hunger.id, 20 * 300, 0, false));
        return true;
    }

}
