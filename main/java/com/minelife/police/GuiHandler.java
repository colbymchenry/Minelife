package com.minelife.police;

import com.minelife.AbstractGuiHandler;
import com.minelife.police.client.ContainerPlayerInventory;
import com.minelife.police.client.GuiPlayerInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class GuiHandler extends AbstractGuiHandler {

    public static int GUI_PLAYER_INVENTORY = 873;

    @Nullable
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if(ID == GUI_PLAYER_INVENTORY) {
            EntityPlayer beingLooted = (EntityPlayer) world.getEntityByID(x);
            if(beingLooted != null) {
                return new ContainerPlayerInventory(player.inventory, beingLooted.inventory, true);
            }
        }
        return null;
    }

    @Nullable
    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if(ID == GUI_PLAYER_INVENTORY) {
            EntityPlayer beingLooted = (EntityPlayer)world.getEntityByID(x);
            if(beingLooted != null) {
                return new GuiPlayerInventory(player.inventory, beingLooted.inventory, true);
            }
        }
        return null;
    }
}
