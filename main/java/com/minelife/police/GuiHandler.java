package com.minelife.police;

import com.minelife.AbstractGuiHandler;
import com.minelife.police.client.ContainerTicketInventory;
import com.minelife.police.client.gui.ticket.GuiTicketInventory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class GuiHandler extends AbstractGuiHandler {

    public static int ticketInventoryID = 4000;

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if(ID == ticketInventoryID) {
            return new ContainerTicketInventory(player.inventory, new TicketInventory(player.inventory.getStackInSlot(x), x));
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if(ID == ticketInventoryID) {
            GuiScreen previousScreen = Minecraft.getMinecraft().currentScreen;
            return new GuiTicketInventory(player.inventory, new TicketInventory(player.inventory.getStackInSlot(x), x), previousScreen);
        }
        return null;
    }
}
