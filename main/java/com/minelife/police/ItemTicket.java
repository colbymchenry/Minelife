package com.minelife.police;

import com.minelife.Minelife;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

import java.util.UUID;

public class ItemTicket extends Item {

    public ItemTicket() {
        setCreativeTab(CreativeTabs.tabMisc);
        setTextureName(Minelife.MOD_ID + ":ticket");
        setUnlocalizedName("ticket");
    }

}
