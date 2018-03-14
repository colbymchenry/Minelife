package com.minelife.drug.item;

import com.minelife.Minelife;
import com.minelife.drug.ModDrugs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ItemDrugTest extends Item {

    public static final DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public ItemDrugTest() {
        setUnlocalizedName("drug_test");
        setTextureName(Minelife.MOD_ID + ":drug_test");
        setCreativeTab(ModDrugs.tab_drugs);
    }

    @Override
    public boolean itemInteractionForEntity(ItemStack item_stack, EntityPlayer player, EntityLivingBase entity_clicked) {
        if (!(entity_clicked instanceof EntityPlayer)) return false;
        if (player.worldObj.isRemote) return true;
        if(item_stack.hasTagCompound() && item_stack.stackTagCompound.hasKey("player")) {
            player.addChatComponentMessage(new ChatComponentText("This has already been used."));
            return false;
        }

        if (ModDrugs.check_for_cocaine((EntityPlayer) entity_clicked))
            player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.RED + "Cocaine detected!"));
        else
            player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.GREEN + "Cocaine not detected."));


        if (ModDrugs.check_for_marijuana((EntityPlayer) entity_clicked))
            player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.RED + "Marijuana detected!"));
        else
            player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.GREEN + "Marijuana not detected."));


        add_drug_test_results(item_stack, (EntityPlayer) entity_clicked);
        player.inventory.setInventorySlotContents(player.inventory.currentItem, item_stack);
        return true;
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean bool) {
        if (stack.hasTagCompound() && stack.stackTagCompound.hasKey("player") && stack.stackTagCompound.hasKey("player_name")) {
            try {
                list.add("Player: " + stack.stackTagCompound.getString("player_name"));
                Date now = Calendar.getInstance().getTime();

                if (stack.stackTagCompound.hasKey("marijuana")) {
                    list.add(EnumChatFormatting.RED + "Tested POSITIVE for marijuana " + ModDrugs.convert_to_mc_days(now, df.parse(stack.stackTagCompound.getString("marijuana"))) + " MC days ago.");
                } else {
                    list.add(EnumChatFormatting.GREEN + "Tested NEGATIVE for marijuana.");
                }

                if (stack.stackTagCompound.hasKey("cocaine")) {
                    list.add(EnumChatFormatting.RED + "Tested POSITIVE for cocaine " + ModDrugs.convert_to_mc_days(now, df.parse(stack.stackTagCompound.getString("cocaine"))) + " MC days ago.");
                } else {
                    list.add(EnumChatFormatting.GREEN + "Tested NEGATIVE for cocaine.");
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
    }

    private void add_drug_test_results(ItemStack item_stack, EntityPlayer player) {
        NBTTagCompound tag_compound = item_stack.hasTagCompound() ? item_stack.stackTagCompound : new NBTTagCompound();

        tag_compound.setString("player", player.getUniqueID().toString());
        tag_compound.setString("player_name", player.getDisplayName());

        if (ModDrugs.check_for_marijuana(player))
            tag_compound.setString("marijuana", df.format(Calendar.getInstance().getTime()));
        if (ModDrugs.check_for_cocaine(player))
            tag_compound.setString("cocaine", df.format(Calendar.getInstance().getTime()));

        item_stack.stackTagCompound = tag_compound;
    }

}
