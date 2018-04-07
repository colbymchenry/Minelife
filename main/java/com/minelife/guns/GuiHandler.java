package com.minelife.guns;

import com.minelife.AbstractGuiHandler;
import com.minelife.guns.turret.ContainerTurret;
import com.minelife.guns.turret.GuiTurret;
import com.minelife.guns.turret.TileEntityTurret;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class GuiHandler extends AbstractGuiHandler {

    public static int TURRET = 9873;

    @Nullable
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if(ID == TURRET) {
            TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
            if (tile != null && tile instanceof TileEntityTurret) {
                return new ContainerTurret(player.inventory, ((TileEntityTurret) tile).getInventory());
            }
        }
        return null;
    }

    @Nullable
    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if(ID == TURRET) {
            TileEntity tile = world.getTileEntity(new BlockPos(x, y, z));
            if (tile != null && tile instanceof TileEntityTurret) {
                return new GuiTurret(player.inventory, (TileEntityTurret) tile);
            }
        }
        return null;
    }
}
