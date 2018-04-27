package com.minelife.jobs;

import com.minelife.guns.ModGuns;
import com.minelife.guns.item.EnumGun;
import com.minelife.jobs.job.bountyhunter.BountyHunterHandler;
import com.minelife.jobs.job.bountyhunter.BountyHunterListener;
import com.minelife.jobs.job.drugproducer.DrugProducerHandler;
import com.minelife.jobs.job.drugproducer.DrugProducerListener;
import com.minelife.jobs.job.farmer.FarmerHandler;
import com.minelife.jobs.job.farmer.FarmerListener;
import com.minelife.jobs.job.fisherman.FishermanHandler;
import com.minelife.jobs.job.fisherman.FishermanListener;
import com.minelife.jobs.job.lumberjack.LumberjackHandler;
import com.minelife.jobs.job.lumberjack.LumberjackListener;
import com.minelife.jobs.job.miner.MinerHandler;
import com.minelife.jobs.job.miner.MinerListener;
import com.minelife.jobs.job.restaurateur.RestaurateurHandler;
import com.minelife.jobs.job.restaurateur.RestaurateurListener;
import com.pam.harvestcraft.item.ItemRegistry;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

public enum EnumJob {

    FARMER(new ItemStack(Items.IRON_HOE), TextFormatting.YELLOW),
    FISHERMAN(new ItemStack(Items.FISHING_ROD), TextFormatting.AQUA),
    MINER(new ItemStack(Items.IRON_PICKAXE), TextFormatting.GOLD),
    BOUNTY_HUNTER(new ItemStack(Items.BOW), TextFormatting.RED),
    RESTAURATEUR(new ItemStack(ItemRegistry.baconcheeseburgerItem), TextFormatting.GREEN),
    LUMBERJACK(new ItemStack(Items.IRON_AXE), TextFormatting.DARK_GREEN),
    POLICE(new ItemStack(ModGuns.itemGun, 1, 1), TextFormatting.BLUE),
    DRUG_PRODUCER(new ItemStack(ModGuns.itemGun, 1, EnumGun.AK47_BLOODBATH.ordinal()), TextFormatting.DARK_RED);

    public ItemStack heldStack;
    public TextFormatting coloredName;

    EnumJob(ItemStack heldStack, TextFormatting coloredName) {
        this.heldStack = heldStack;
        this.coloredName = coloredName;
    }

    public NPCHandler getHandler() {
        switch (this) {
            case FARMER: return FarmerHandler.INSTANCE;
            case FISHERMAN: return FishermanHandler.INSTANCE;
            case MINER: return MinerHandler.INSTANCE;
            case BOUNTY_HUNTER: return BountyHunterHandler.INSTANCE;
            case RESTAURATEUR: return RestaurateurHandler.INSTANCE;
            case LUMBERJACK: return LumberjackHandler.INSTANCE;
            case DRUG_PRODUCER: return DrugProducerHandler.INSTANCE;
        }
        return null;
    }

    public Object getListener() {
        switch (this) {
            case FARMER: return new FarmerListener();
            case FISHERMAN: return new FishermanListener();
            case MINER: return new MinerListener();
            case BOUNTY_HUNTER: return new BountyHunterListener();
            case RESTAURATEUR: return new RestaurateurListener();
            case LUMBERJACK: return new LumberjackListener();
            case DRUG_PRODUCER: return new DrugProducerListener();
        }
        return null;
    }
}
