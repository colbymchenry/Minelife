package com.minelife.police;

import com.google.common.collect.Lists;
import com.minelife.Minelife;
import com.minelife.police.client.GuiCreateTicket;
import com.minelife.police.client.GuiTicket;
import com.minelife.util.ArrayUtil;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

import java.util.List;
import java.util.UUID;

public class ItemTicket extends Item {

    public ItemTicket() {
        setUnlocalizedName("ticket");
        setTextureName(Minelife.MOD_ID + ":ticket");
        setCreativeTab(CreativeTabs.tabMisc);
    }

    @SideOnly(Side.CLIENT)
    private void openGui(ItemStack itemStack) {
        Minecraft.getMinecraft().displayGuiScreen(new GuiCreateTicket(itemStack));
    }

    @Override
    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player) {
        if(!world.isRemote) return super.onItemRightClick(itemStack, world, player);

        int ticketID = getTicketID(itemStack);

        if(ticketID == 0) {
            NBTTagCompound tagCompound = itemStack.hasTagCompound() ? itemStack.stackTagCompound : new NBTTagCompound();
            tagCompound.setInteger("id", MathHelper.getRandomIntegerInRange(world.rand, 1000000, 1999999));
            itemStack.stackTagCompound = tagCompound;
        }

        openGui(itemStack);
        return super.onItemRightClick(itemStack, world, player);
    }

    public static int getTicketID(ItemStack stack) {
        NBTTagCompound tagCompound = stack.hasTagCompound() ? stack.stackTagCompound : new NBTTagCompound();
        return tagCompound.hasKey("id") ? tagCompound.getInteger("id") : 0;
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
        if(tagCompound.hasKey("charges")) {
            String[] charges = ArrayUtil.fromString(tagCompound.getString("charges"));
            for (String charge : charges) chargeList.add(Charge.fromString(charge));
        }

        return chargeList;
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
        for(int i = 0; i < charges.size(); i++) chargesArray[i] = charges.get(i).toString();
        tagCompound.setString("charges", ArrayUtil.toString(chargesArray));
    }

}
