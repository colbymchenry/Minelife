package com.minelife.realestate.item;

import com.minelife.realestate.client.ClientRenderer;
import com.minelife.realestate.client.util.PlayerUtil;
import com.minelife.util.Vector;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;

public class ItemEstateClaimingTool extends Item {

    public ItemEstateClaimingTool() {

        this.setCreativeTab(CreativeTabs.tabTools);
        this.setUnlocalizedName("Estate Claiming Tool");
//        TODO: Set Correct Texture
        System.out.println("*** Set Correct Texture For Estate Claiming Tool");
        this.setTextureName("blaze_rod");
//        setTextureName(Minelife.MOD_ID + ":estate_claiming_tool");
        this.setMaxStackSize(1);

    }

    @Override
    public String getItemStackDisplayName(ItemStack p_77653_1_) {

        return "Estate Claiming Tool";

    }

    @Override
    public boolean onItemUse(ItemStack itemstack, EntityPlayer player, World world, int x, int y, int z, int side, float px, float py, float pz) {

        if (world.isRemote) {

            Vector point = new Vector(x, y, z);

            switch (ClientRenderer.getState()) {
                case SELECTING:
                    ClientRenderer.setSelecting(SelectionState.SELECTED);
                    ClientRenderer.setEnd(point);
                    PlayerUtil.sendTo(player, "You selected an estate.");
                    break;
                default:
                    ClientRenderer.setSelecting(SelectionState.SELECTING);
                    ClientRenderer.setStart(point);
                    PlayerUtil.sendTo(player, "You selected a starting block.");
                    break;
            }

        }

        return super.onItemUse(itemstack, player, world, x, y, z, side, px, py, pz);

    }

}