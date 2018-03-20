package com.minelife;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;


public class MinelifeGuiHandler implements IGuiHandler {

    @Override
    public Container getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        for (MLMod mod : Minelife.getModList()) {
            if(mod.getGuiHandler() != null) {
                Container container = (Container) mod.getGuiHandler().getServerGuiElement(ID, player, world, x, y, z);
                if(container != null) return container;
            }
        }
        return null;
    }

    @Override
    public GuiScreen getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        for (MLMod mod : Minelife.getModList()) {
            if(mod.getGuiHandler() != null) {
                GuiScreen screen = (GuiScreen) mod.getGuiHandler().getClientGuiElement(ID, player, world, x, y, z);
                if(screen != null) return screen;
            }
        }
        return null;
    }

}
