package com.minelife.drug;

import com.minelife.AbstractGuiHandler;
import com.minelife.drug.client.gui.ContainerAmmoniaExtractor;
import com.minelife.drug.client.gui.ContainerLeafMulcher;
import com.minelife.drug.client.gui.GuiAmmoniaExtractor;
import com.minelife.drug.client.gui.GuiLeafMulcher;
import com.minelife.drug.tileentity.TileEntityAmmoniaExtractor;
import com.minelife.drug.tileentity.TileEntityEntityLeafMulcher;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class DrugsGuiHandler extends AbstractGuiHandler {

    public static int leaf_mulcher_id = 9090;
    public static int ammonia_extractor_id = 9091;

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        if(ID == leaf_mulcher_id) return new ContainerLeafMulcher(player.inventory, (TileEntityEntityLeafMulcher) world.getTileEntity(x, y, z));
        if(ID == ammonia_extractor_id) return new ContainerAmmoniaExtractor(player.inventory, (TileEntityAmmoniaExtractor) world.getTileEntity(x, y, z));
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        if(ID == leaf_mulcher_id) return new GuiLeafMulcher(player.inventory, (TileEntityEntityLeafMulcher) world.getTileEntity(x, y, z));
        if(ID == ammonia_extractor_id) return new GuiAmmoniaExtractor(player.inventory, (TileEntityAmmoniaExtractor) world.getTileEntity(x, y, z));
        return null;
    }

}
