package com.minelife.police;

import com.minelife.AbstractGuiHandler;
import com.minelife.Minelife;
import com.minelife.police.client.ContainerTicketInventory;
import com.minelife.police.client.gui.ticket.GuiTicketInventory;
import com.minelife.util.NBTUtil;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.sql.ResultSet;
import java.sql.SQLException;

public class GuiHandler extends AbstractGuiHandler {

    public static final int ticketInventoryID = 4000;

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        switch(ID) {
            case ticketInventoryID: {
                // TODO: This may affect the client in sync issue.
                ItemStack ticketStack = player.inventory.getStackInSlot(x);
                if( y == 1) {
                    player.inventory.setInventorySlotContents(x, null);
                }
                return new ContainerTicketInventory(player.inventory, new TicketInventory(ticketStack));
            }
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        switch (ID) {
            case ticketInventoryID: {
                GuiScreen previousScreen = Minecraft.getMinecraft().currentScreen;
                return new GuiTicketInventory(player.inventory, new TicketInventory(player.inventory.getStackInSlot(x)), previousScreen);
            }
        }
        return null;
    }
}
