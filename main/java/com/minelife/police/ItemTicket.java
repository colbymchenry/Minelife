package com.minelife.police;

import com.google.common.collect.Lists;
import com.minelife.Minelife;
import com.minelife.minebay.ItemListing;
import com.minelife.police.client.GuiCreateTicket;
import com.minelife.police.client.GuiTicket;
import com.minelife.util.ArrayUtil;
import com.minelife.util.ItemUtil;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class ItemTicket extends Item {

    public ItemTicket() {
        setUnlocalizedName("ticket");
        setTextureName(Minelife.MOD_ID + ":ticket");
        setCreativeTab(CreativeTabs.tabMisc);

    }

    @SideOnly(Side.CLIENT)
    private void openCreateGui(int slot) {
        Minecraft.getMinecraft().displayGuiScreen(new GuiCreateTicket(slot));
    }

    @SideOnly(Side.CLIENT)
    private void openTicketGui(int slot) {
        Minecraft.getMinecraft().displayGuiScreen(new GuiTicket(slot));
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player) {
        if (!world.isRemote) return super.onItemRightClick(itemStack, world, player);

        if (getPlayerForTicket(itemStack) == null) {
            NBTTagCompound tagCompound = itemStack.hasTagCompound() ? itemStack.stackTagCompound : new NBTTagCompound();
            tagCompound.setInteger("id", MathHelper.getRandomIntegerInRange(world.rand, 1000000, 1999999));
            itemStack.stackTagCompound = tagCompound;
            openCreateGui(player.inventory.currentItem);
        } else {
            openTicketGui(player.inventory.currentItem);
        }

        return super.onItemRightClick(itemStack, world, player);
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int i, boolean b) {
        if (getTicketID(stack) == 0) {
            NBTTagCompound tagCompound = stack.hasTagCompound() ? stack.stackTagCompound : new NBTTagCompound();
            tagCompound.setInteger("id", MathHelper.getRandomIntegerInRange(world.rand, 1000000, 1999999));
            stack.stackTagCompound = tagCompound;
        }
    }

    @SideOnly(Side.CLIENT)
    public String getItemStackDisplayName(ItemStack stack) {
        return getTicketID(stack) == 0 ? super.getItemStackDisplayName(stack) : super.getItemStackDisplayName(stack) + " #" + getTicketID(stack);
    }

    public static int getTicketID(ItemStack stack) {
        NBTTagCompound tagCompound = stack.hasTagCompound() ? stack.stackTagCompound : new NBTTagCompound();
        return tagCompound.hasKey("id") ? tagCompound.getInteger("id") : 0;
    }

    public static int getTimeToPay(ItemStack stack) {
        NBTTagCompound tagCompound = stack.hasTagCompound() ? stack.stackTagCompound : new NBTTagCompound();
        try {
            if(tagCompound.hasKey("dueDate")) {
                Date dueDate = ItemListing.df.parse(tagCompound.getString("dueDate"));
                Date now = Calendar.getInstance().getTime();
                long diff = dueDate.getTime() - now.getTime();
                long diffSeconds = diff / 1000 % 60;
                long diffMinutes = diff / (60 * 1000) % 60;
                return (int) diffMinutes;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public static UUID getPlayerForTicket(ItemStack stack) {
        NBTTagCompound tagCompound = stack.hasTagCompound() ? stack.stackTagCompound : new NBTTagCompound();
        return tagCompound.hasKey("playerUUID") ? UUID.fromString(tagCompound.getString("playerUUID")) : null;
    }

    public static UUID getOfficerForTicket(ItemStack stack) {
        NBTTagCompound tagCompound = stack.hasTagCompound() ? stack.stackTagCompound : new NBTTagCompound();
        return tagCompound.hasKey("officerUUID") ? UUID.fromString(tagCompound.getString("officerUUID")) : null;
    }

    public static List<Charge> getChargesForTicket(ItemStack stack) {
        NBTTagCompound tagCompound = stack.hasTagCompound() ? stack.stackTagCompound : new NBTTagCompound();
        List<Charge> chargeList = Lists.newArrayList();
        if (tagCompound.hasKey("charges")) {
            String[] charges = ArrayUtil.fromString(tagCompound.getString("charges"));
            for (String charge : charges) {
                if (!charge.isEmpty())
                    chargeList.add(Charge.fromString(charge));
            }
        }

        return chargeList;
    }

    public static List<ItemStack> getItemsForTicket(ItemStack stack) {
        NBTTagCompound tagCompound = stack.hasTagCompound() ? stack.stackTagCompound : new NBTTagCompound();
        List<ItemStack> itemList = Lists.newArrayList();
        if (tagCompound.hasKey("items")) {
            String[] items = ArrayUtil.fromString(tagCompound.getString("items"));
            for (String item : items) {
                if (!item.isEmpty())
                    itemList.add(ItemUtil.itemFromString(item));
            }
        }

        return itemList;
    }

    public static void setTimeToPay(ItemStack stack, int minutes) {
        Calendar now = Calendar.getInstance();
        now.add(Calendar.MINUTE, minutes);
        NBTTagCompound tagCompound = stack.hasTagCompound() ? stack.stackTagCompound : new NBTTagCompound();
        tagCompound.setString("dueDate", ItemListing.df.format(now.getTime()));
        stack.stackTagCompound = tagCompound;
    }

    public static void setPlayerForTicket(ItemStack stack, UUID playerUUID) {
        NBTTagCompound tagCompound = stack.hasTagCompound() ? stack.stackTagCompound : new NBTTagCompound();
        tagCompound.setString("playerUUID", playerUUID.toString());
        stack.stackTagCompound = tagCompound;
    }

    public static void setOfficerForTicket(ItemStack stack, UUID officerUUID) {
        NBTTagCompound tagCompound = stack.hasTagCompound() ? stack.stackTagCompound : new NBTTagCompound();
        tagCompound.setString("officerUUID", officerUUID.toString());
        stack.stackTagCompound = tagCompound;
    }

    public static void setChargesForTicket(ItemStack stack, List<Charge> charges) {
        NBTTagCompound tagCompound = stack.hasTagCompound() ? stack.stackTagCompound : new NBTTagCompound();
        String[] chargesArray = new String[charges.size()];
        for (int i = 0; i < charges.size(); i++) chargesArray[i] = charges.get(i).toString();
        tagCompound.setString("charges", ArrayUtil.toString(chargesArray));
        stack.stackTagCompound = tagCompound;
    }

}
