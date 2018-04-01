package com.minelife.drugs;

import com.minelife.AbstractGuiHandler;
import com.minelife.drugs.client.gui.ContainerVacuum;
import com.minelife.drugs.client.gui.GuiVacuum;
import com.minelife.drugs.tileentity.TileEntityVacuum;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class DrugsGuiHandler extends AbstractGuiHandler {

    public static final int VACUUM_GUI = 51312;

    @Nullable
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if(ID == VACUUM_GUI) {
            TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));
            if(tileEntity != null && tileEntity instanceof TileEntityVacuum) {
                return new ContainerVacuum(player, (TileEntityVacuum) tileEntity);
            }
        }
        return null;
    }

    @Nullable
    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if(ID == VACUUM_GUI) {
            TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));
            if(tileEntity != null && tileEntity instanceof TileEntityVacuum) {
                return new GuiVacuum(player, (TileEntityVacuum) tileEntity);
            }
        }
        return null;
    }
}
