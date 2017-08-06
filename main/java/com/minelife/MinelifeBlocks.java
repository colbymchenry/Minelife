package com.minelife;

import com.minelife.drug.block.*;
import com.minelife.economy.BlockATM;
import com.minelife.economy.BlockATMTop;
import com.minelife.gun.block.BlockZincOre;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;

import java.util.logging.Level;

public class MinelifeBlocks {

    public BlockAmmonia ammonia;
    public BlockCannabisPlant cannabis_plant;
    public BlockCementMixer cement_mixer;
    public BlockCocaPlant coca_plant;
    public BlockDryingRack drying_rack;
    public BlockLeafMulcher leaf_mulcher;
    public BlockLimePlant lime_plant;
    public BlockLimestone limestone;
    public BlockPotash potash;
    public BlockPotassiumPermanganate potassium_permanganate;
    public BlockPyrolusiteOre pyrolusite_ore;
    public BlockPyrolusiteBlock pyrolusite;
    public BlockSulfuricAcid sulfuric_acid;
    public BlockSulfurOre sulfur_ore;
    public BlockVacuum vacuum;
    public BlockATM atm;
    public BlockATMTop atm_top;
    public BlockZincOre zinc_ore;
    public BlockRoller roller;
    public BlockPresser presser;

    protected void init()
    {
        // register fluids
        BlockAmmonia.register_fluid();
        BlockSulfuricAcid.register_fluid();
        BlockPotassiumPermanganate.register_fluid();

        register_block(ammonia = new BlockAmmonia());
        register_block(cannabis_plant = new BlockCannabisPlant());
        register_block(cement_mixer = new BlockCementMixer());
        register_block(coca_plant = new BlockCocaPlant());
        register_block(drying_rack = new BlockDryingRack());
        register_block(leaf_mulcher = new BlockLeafMulcher());
        register_block(lime_plant = new BlockLimePlant());
        register_block(limestone = new BlockLimestone());
        register_block(potash = new BlockPotash());
        register_block(potassium_permanganate = new BlockPotassiumPermanganate());
        register_block(pyrolusite = new BlockPyrolusiteBlock());
        register_block(pyrolusite_ore = new BlockPyrolusiteOre());
        register_block(sulfuric_acid = new BlockSulfuricAcid());
        register_block(sulfur_ore = new BlockSulfurOre());
        register_block(vacuum = new BlockVacuum());
        register_block(atm = new BlockATM());
        register_block(atm_top = new BlockATMTop());
        register_block(zinc_ore = new BlockZincOre());
        register_block(presser = new BlockPresser());
        register_block(roller = new BlockRoller());
    }

    private void register_block(Block block)
    {
        try {
            GameRegistry.registerBlock(block, block.getUnlocalizedName());
            System.out.println(block.getUnlocalizedName() + " registered!");
        } catch (Exception e) {
            Minelife.getLogger().log(Level.SEVERE, "Failed to register block! " + block.getClass().getSimpleName() + "\nError Message: " + e.getMessage());
        }

    }


}
