package com.minelife;

import com.minelife.drug.block.*;
import com.minelife.economy.BlockATM;
import com.minelife.economy.BlockATMTop;
import com.minelife.economy.cash.BlockCash;
import com.minelife.gangs.BlockVaultCreator;
import com.minelife.gun.block.BlockZincOre;
import com.minelife.gun.turrets.BlockTurret;
import com.minelife.locks.BlockLock;
import com.minelife.locks.LockType;
import com.minelife.shop.BlockShopBlock;
import com.minelife.util.blocks.BlockRedstoneLampOn;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;

import java.util.logging.Level;

public class MLBlocks {

    public static BlockAmmonia ammonia;
    public static BlockCannabisPlant cannabis_plant;
    public static BlockCocaPlant coca_plant;
    public static BlockLeafMulcher leaf_mulcher;
    public static BlockLimePlant lime_plant;
    public static BlockLimestone limestone;
    public static BlockPotash potash;
    public static BlockPotassiumPermanganate potassium_permanganate;
    public static BlockPyrolusiteOre pyrolusite_ore;
    public static BlockPyrolusiteBlock pyrolusite;
    public static BlockSulfuricAcid sulfuric_acid;
    public static BlockSulfurOre sulfur_ore;
    public static BlockVacuum vacuum;
    public static BlockATM atm;
    public static BlockATMTop atm_top;
    public static BlockZincOre zinc_ore;
    public static BlockPresser presser;
    public static BlockDryingRack drying_rack;
    public static BlockCementMixer cement_mixer;
    public static BlockShopBlock shopBlock;
    public static BlockRedstoneLampOn redstoneLampOn;
    public static BlockTurret turret;
    public static BlockCash cash;
    public static BlockLock ironLock, goldLock, diamondLock, obsidianLock;
//    public static BlockVaultCreator vaultCreator;

    protected static void init()
    {
        // register fluids
        BlockAmmonia.register_fluid();
        BlockSulfuricAcid.register_fluid();
        BlockPotassiumPermanganate.register_fluid();

        register_block(ammonia = new BlockAmmonia());
        register_block(cannabis_plant = new BlockCannabisPlant());
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
        register_block(cement_mixer = new BlockCementMixer());
        register_block(shopBlock = new BlockShopBlock());
        register_block(redstoneLampOn = new BlockRedstoneLampOn());
        register_block(turret = new BlockTurret());
        register_block(cash = new BlockCash());
        register_block(ironLock = new BlockLock(LockType.IRON));
        register_block(goldLock = new BlockLock(LockType.GOLD));
        register_block(diamondLock = new BlockLock(LockType.DIAMOND));
        register_block(obsidianLock = new BlockLock(LockType.OBSIDIAN));
//        register_block(vaultCreator = new BlockVaultCreator());
    }

    private static void register_block(Block block)
    {
        try {
            GameRegistry.registerBlock(block, block.getUnlocalizedName());
        } catch (Exception e) {
            Minelife.getLogger().log(Level.SEVERE, "Failed to register block! " + block.getClass().getSimpleName() + "\nError Message: " + e.getMessage());
        }

    }


}
