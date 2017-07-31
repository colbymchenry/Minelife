package com.minelife.drug.client.gui;

import buildcraft.core.lib.gui.GuiBuildCraft;
import com.minelife.Minelife;
import com.minelife.drug.tileentity.TileEntityLeafMulcher;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

public class GuiLeafMulcher extends GuiBuildCraft {

    private TileEntityLeafMulcher tile_leaf_mulcher;

    // grab the texture for the tank fill up from here
    // new ResourceLocation("buildcraftenergy:textures/gui/combustion_engine_gui.png");
    // copy a lot of the code from the GuiCombustionEngine and TileEngineIron from here
    // buildcraft.energy

    public GuiLeafMulcher(InventoryPlayer player_inventory, TileEntityLeafMulcher tile_leaf_mulcher)
    {
        super(new ContainerLeafMulcher(player_inventory, tile_leaf_mulcher), tile_leaf_mulcher, new ResourceLocation(Minelife.MOD_ID + ":textures/gui/leaf_mulcher.png"));
        this.tile_leaf_mulcher = tile_leaf_mulcher;
        this.xSize = 100;
        this.ySize = 50;
    }
}
