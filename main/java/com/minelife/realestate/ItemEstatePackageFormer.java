package com.minelife.realestate;

import com.google.common.collect.Lists;
import com.minelife.Minelife;
import com.minelife.util.ArrayUtil;
import com.minelife.util.ListToString;
import com.minelife.util.StringToList;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import java.util.List;
import java.util.UUID;

public class ItemEstatePackageFormer extends Item {

    public static ItemEstatePackageFormer instance;

    public ItemEstatePackageFormer()
    {
        setCreativeTab(CreativeTabs.tabTools);
        setUnlocalizedName("EstatePackageFormer");
        setTextureName(Minelife.MOD_ID + ":EstatePackageFormer");
        instance = this;
    }

    @Override
    public boolean onItemUse(ItemStack heldItem, EntityPlayer player, World world, int x, int y, int z, int side, float f, float f1, float f2)
    {
        if (world.isRemote) return false;

        Estate estate = Estate.getEstate(world, x, z);

        if (estate == null) {
            player.addChatComponentMessage(new ChatComponentText("There is no estate in this chunk."));
            return false;
        }

        if (!estate.getOwner().equals(player.getUniqueID())) {
            player.addChatComponentMessage(new ChatComponentText("You do not own this estate."));
            return false;
        }

        NBTTagCompound tagCompound = heldItem.hasTagCompound() ? heldItem.getTagCompound() : new NBTTagCompound();

        List<Estate> estates = Lists.newArrayList();


        if (tagCompound.hasKey("estates")) {
            StringToList<Estate> stringToList = new StringToList<Estate>(tagCompound.getString("estates")) {
                @Override
                public Estate parse(String s)
                {
                    return Estate.getEstate(UUID.fromString(s));
                }
            };

           stringToList.getList().add(estate);
           estates.addAll(stringToList.getList());

           if(estates.stream().filter(e -> e.getUUID().equals(estate.getUUID())).findFirst().orElse(null) == null) {
               player.addChatComponentMessage(new ChatComponentText("That estate has already been added to this package."));
               return false;
           }
        }

        estates.add(estate);

        ListToString<Estate> estatesStringList = new ListToString<Estate>(estates) {
            @Override
            public String toString(Estate o)
            {
                return o.getUUID().toString();
            }
        };

        tagCompound.setString("estates", estatesStringList.getListAsString());
        heldItem.setTagCompound(tagCompound);

        player.addChatComponentMessage(new ChatComponentText("Estate added to package."));
        return true;
    }

}
