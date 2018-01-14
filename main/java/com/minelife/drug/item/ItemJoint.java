package com.minelife.drug.item;

import com.minelife.Minelife;
import com.minelife.drug.ModDrugs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

import java.util.Calendar;

public class ItemJoint extends ItemAbstractDrug {

    public ItemJoint()
    {
        super("joint");
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player) {
        if(world.isRemote) return super.onItemRightClick(itemStack, world, player);
        player.addPotionEffect(new PotionEffect(ModDrugs.x_ray_potion.id, 15 * 20, 0, false));
        player.addPotionEffect(new PotionEffect(Potion.nightVision.id, 60 * 20, 0, false));
        player.addPotionEffect(new PotionEffect(Potion.waterBreathing.id, 60 * 20, 0, false));
        player.addPotionEffect(new PotionEffect(Potion.hunger.id, 60 * 20, 1, false));
        // 60 * 20 = 1 minute, 20 * 60 * 20 = 1 mc day, 7 * 20 * 60 * 20 = 7 mc days
        player.addPotionEffect(new PotionEffect(ModDrugs.marijuana_potion.id, 1 *(20 * (60 * 20)) , 0, false));
        player.worldObj.playSoundEffect(player.posX, player.posY, player.posZ, Minelife.MOD_ID + ":smoke", 0.2F, 1F);

        ItemStack inHand = player.inventory.getCurrentItem();
        if(inHand.stackSize == 1) {
            player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
        } else {
            inHand.stackSize -= 1;
            player.inventory.setInventorySlotContents(player.inventory.currentItem, inHand);
        }
//        player.getEntityData().setString("marijuana", ItemDrugTest.df.format(Calendar.getInstance().getTime()));
        return super.onItemRightClick(itemStack, world, player);
    }

}
