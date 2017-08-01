package com.minelife.drug;

import com.minelife.AbstractGuiHandler;
import com.minelife.drug.client.gui.ContainerLeafMulcher;
import com.minelife.drug.client.gui.GuiLeafMulcher;
import com.minelife.drug.tileentity.TileEntityLeafMulcher;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class DrugsGuiHandler extends AbstractGuiHandler {

    public static int leaf_mulcher_id = 9090;

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        if(ID == leaf_mulcher_id) return new ContainerLeafMulcher(player.inventory, (TileEntityLeafMulcher) world.getTileEntity(x, y, z));
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        if(ID == leaf_mulcher_id) return new GuiLeafMulcher(player.inventory, (TileEntityLeafMulcher) world.getTileEntity(x, y, z));
        return null;
    }

}
