package com.minelife.drug.item;

import com.minelife.MLItems;
import com.minelife.Minelife;
import com.minelife.drug.ModDrugs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

public class ItemDrugTest extends Item {

    public ItemDrugTest() {
        setUnlocalizedName("drug_test");
        setTextureName(Minelife.MOD_ID + ":drug_test");
        setCreativeTab(ModDrugs.tab_drugs);
    }

    @Override
    public boolean itemInteractionForEntity(ItemStack item_stack, EntityPlayer player, EntityLivingBase entity_clicked) {
        if(!(entity_clicked instanceof EntityPlayer)) return false;
        if(player.worldObj.isRemote) return true;

        if(ModDrugs.check_for_cocaine((EntityPlayer) entity_clicked, 7)) {
            player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.RED + "Cocaine detected!"));
        } else {
            player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.GREEN + "Cocaine not detected."));
        }

        if(ModDrugs.check_for_marijuana((EntityPlayer) entity_clicked, 7)) {
            player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.RED + "Marijuana detected!"));
        } else {
            player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.GREEN + "Marijuana not detected."));
        }

        return true;
    }
}
