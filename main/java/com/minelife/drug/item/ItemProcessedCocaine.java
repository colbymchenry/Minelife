package com.minelife.drug.item;

import com.minelife.Minelife;
import com.minelife.drug.ModDrugs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

import java.util.Calendar;

public class ItemProcessedCocaine extends Item {

    public ItemProcessedCocaine() {
        setCreativeTab(ModDrugs.tab_drugs);
        setUnlocalizedName("processed_cocaine");
        setTextureName(Minelife.MOD_ID + ":processed_cocaine");
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
        // 60 * 20 = 1 minute, 20 * 60 * 20 = 1 mc day, 7 * 20 * 60 * 20 = 7 mc days
        player.addPotionEffect(new PotionEffect(ModDrugs.cocaine_potion.id, 1 *(20 * (60 * 20)) , 0, false));
        player.worldObj.playSoundEffect(x, y, z, Minelife.MOD_ID + ":snort", 0.5F, 0.5F);
//        player.getEntityData().setString("cocaine", ItemDrugTest.df.format(Calendar.getInstance().getTime()));
        return true;
    }
}
