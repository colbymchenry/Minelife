package com.minelife.realestate;

import com.minelife.Minelife;
import com.minelife.realestate.network.PacketRequestToOpenEstateCreationForm;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemEstateForm extends Item {

    public ItemEstateForm()
    {
        setCreativeTab(CreativeTabs.tabMisc);
        setUnlocalizedName("estateForm");
        setTextureName(Minelife.MOD_ID + ":estateForm");
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player)
    {
        if(world.isRemote) Minelife.NETWORK.sendToServer(new PacketRequestToOpenEstateCreationForm());
        return super.onItemRightClick(stack, world, player);
    }
}
