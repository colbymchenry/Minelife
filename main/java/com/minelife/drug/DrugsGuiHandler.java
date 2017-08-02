package com.minelife.drug;

import com.minelife.AbstractGuiHandler;
import com.minelife.drug.client.gui.*;
import com.minelife.drug.tileentity.TileEntityAmmoniaExtractor;
import com.minelife.drug.tileentity.TileEntityDryingRack;
import com.minelife.drug.tileentity.TileEntityEntityLeafMulcher;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class DrugsGuiHandler extends AbstractGuiHandler {

    public static int leaf_mulcher_id = 9090;
    public static int ammonia_extractor_id = 9091;
    public static int drying_rack_id = 9092;

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        if(ID == leaf_mulcher_id) return new ContainerLeafMulcher(player.inventory, (TileEntityEntityLeafMulcher) world.getTileEntity(x, y, z));
        if(ID == ammonia_extractor_id) return new ContainerAmmoniaExtractor(player.inventory, (TileEntityAmmoniaExtractor) world.getTileEntity(x, y, z));
        if(ID == drying_rack_id) return new ContainerDryingRack(player.inventory, (TileEntityDryingRack) world.getTileEntity(x, y, z));
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        if(ID == leaf_mulcher_id) return new GuiLeafMulcher(player.inventory, (TileEntityEntityLeafMulcher) world.getTileEntity(x, y, z));
        if(ID == ammonia_extractor_id) return new GuiAmmoniaExtractor(player.inventory, (TileEntityAmmoniaExtractor) world.getTileEntity(x, y, z));
        if(ID == drying_rack_id) return new GuiDryingRack(player.inventory, (TileEntityDryingRack) world.getTileEntity(x, y, z));
        return null;
    }

}
