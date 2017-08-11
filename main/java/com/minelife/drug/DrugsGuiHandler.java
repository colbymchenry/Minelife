package com.minelife.drug;

import com.minelife.AbstractGuiHandler;
import com.minelife.drug.client.gui.*;
import com.minelife.drug.tileentity.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class DrugsGuiHandler extends AbstractGuiHandler {

    public static int leaf_mulcher_id = 9090;
    public static int drying_rack_id = 9092;
    public static int cement_mixer_id = 9093;
    public static int presser_id = 9094;
    public static int vacuum_id = 9095;

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        if(ID == leaf_mulcher_id) return new ContainerLeafMulcher(player.inventory, (TileEntityLeafMulcher) world.getTileEntity(x, y, z));
        if(ID == drying_rack_id) return new ContainerDryingRack(player.inventory, (TileEntityDryingRack) world.getTileEntity(x, y, z));
        if(ID == cement_mixer_id) return new ContainerCementMixer(player.inventory, (TileEntityCementMixer) world.getTileEntity(x, y, z));
        if(ID == presser_id) return new ContainerPresser(player.inventory, (TileEntityPresser) world.getTileEntity(x, y, z));
        if(ID == vacuum_id) return new ContainerVacuum(player.inventory, (TileEntityVacuum) world.getTileEntity(x, y, z));
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        if(ID == leaf_mulcher_id) return new GuiLeafMulcher(player.inventory, (TileEntityLeafMulcher) world.getTileEntity(x, y, z));
        if(ID == drying_rack_id) return new GuiDryingRack(player.inventory, (TileEntityDryingRack) world.getTileEntity(x, y, z));
        if(ID == cement_mixer_id) return new GuiCementMixer(player.inventory, (TileEntityCementMixer) world.getTileEntity(x, y, z));
        if(ID == presser_id) return new GuiPresser(player.inventory, (TileEntityPresser) world.getTileEntity(x, y, z));
        if(ID == vacuum_id) return new GuiVacuum(player.inventory, (TileEntityVacuum) world.getTileEntity(x, y, z));
        return null;
    }

}
