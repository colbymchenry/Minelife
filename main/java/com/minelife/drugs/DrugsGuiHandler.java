package com.minelife.drugs;

import com.minelife.AbstractGuiHandler;
import com.minelife.drugs.client.gui.*;
import com.minelife.drugs.tileentity.TileEntityCementMixer;
import com.minelife.drugs.tileentity.TileEntityLeafMulcher;
import com.minelife.drugs.tileentity.TileEntityPresser;
import com.minelife.drugs.tileentity.TileEntityVacuum;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class DrugsGuiHandler extends AbstractGuiHandler {

    public static final int VACUUM_GUI = 51312;
    public static final int LEAF_MULCHER_GUI = 51313;
    public static final int PRESSER_GUI = 51314;
    public static final int CEMENT_MIXER_GUI = 51315;

    @Nullable
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));

        if(ID == VACUUM_GUI) {
            if(tileEntity != null && tileEntity instanceof TileEntityVacuum) {
                return new ContainerVacuum(player, (TileEntityVacuum) tileEntity);
            }
        }

        if(ID == LEAF_MULCHER_GUI) {
            if(tileEntity != null && tileEntity instanceof TileEntityLeafMulcher) {
                return new ContainerLeafMulcher(player, (TileEntityLeafMulcher) tileEntity);
            }
        }

        if(ID == PRESSER_GUI) {
            if(tileEntity != null && tileEntity instanceof TileEntityPresser) {
                return new ContainerPresser(player, (TileEntityPresser) tileEntity);
            }
        }

        if(ID == CEMENT_MIXER_GUI) {
            if(tileEntity != null && tileEntity instanceof TileEntityCementMixer) {
                return new ContainerCementMixer(player, (TileEntityCementMixer) tileEntity);
            }
        }
        return null;
    }

    @Nullable
    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity tileEntity = world.getTileEntity(new BlockPos(x, y, z));

        if(ID == VACUUM_GUI) {
            if(tileEntity != null && tileEntity instanceof TileEntityVacuum) {
                return new GuiVacuum(player, (TileEntityVacuum) tileEntity);
            }
        }

        if(ID == LEAF_MULCHER_GUI) {
            if(tileEntity != null && tileEntity instanceof TileEntityLeafMulcher) {
                return new GuiLeafMulcher(player, (TileEntityLeafMulcher) tileEntity);
            }
        }

        if(ID == PRESSER_GUI) {
            if(tileEntity != null && tileEntity instanceof TileEntityPresser) {
                return new GuiPresser(player, (TileEntityPresser) tileEntity);
            }
        }

        if(ID == CEMENT_MIXER_GUI) {
            if(tileEntity != null && tileEntity instanceof TileEntityCementMixer) {
                return new GuiCementMixer(player, (TileEntityCementMixer) tileEntity);
            }
        }
        return null;
    }
}
