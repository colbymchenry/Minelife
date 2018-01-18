package com.minelife.gun;

import com.minelife.AbstractGuiHandler;
import com.minelife.gun.turrets.ContainerTurret;
import com.minelife.gun.turrets.GuiTurret;
import com.minelife.gun.turrets.TileEntityTurret;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class GuiHandler extends AbstractGuiHandler {

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if(ID == 98745) {
            TileEntity tile = world.getTileEntity(x, y, z);
            if(tile != null && tile instanceof TileEntityTurret) {
                return new ContainerTurret(player.inventory, (TileEntityTurret) tile);
            }
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if(ID == 98745) {
            TileEntity tile = world.getTileEntity(x, y, z);
            if(tile != null && tile instanceof TileEntityTurret) {
                return new GuiTurret(player.inventory, (TileEntityTurret) tile);
            }
        }
        return null;
    }
}
