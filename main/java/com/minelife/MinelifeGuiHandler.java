package com.minelife;

import cpw.mods.fml.common.network.IGuiHandler;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.world.World;


public class MinelifeGuiHandler implements IGuiHandler {

    @Override
    public Container getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        for (MLMod mod : Minelife.MODS) {
            if(mod.gui_handler() != null) {
                Container container = (Container) mod.gui_handler().getServerGuiElement(ID, player, world, x, y, z);
                if(container != null) return container;
            }
        }
        return null;
    }

    @Override
    public GuiScreen getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        for (MLMod mod : Minelife.MODS) {
            if(mod.gui_handler() != null) {
                GuiScreen screen = (GuiScreen) mod.gui_handler().getClientGuiElement(ID, player, world, x, y, z);
                if(screen != null) return screen;
            }
        }
        return null;
    }

}
