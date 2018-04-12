package com.minelife.jobs;

import com.minelife.guns.ModGuns;
import com.minelife.jobs.job.farmer.FarmerHandler;
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
    POLICE(new ItemStack(ModGuns.itemGun, 1, 1), TextFormatting.BLUE);

    public ItemStack heldStack;
    public TextFormatting coloredName;

    EnumJob(ItemStack heldStack, TextFormatting coloredName) {
        this.heldStack = heldStack;
        this.coloredName = coloredName;
    }

    public NPCHandler getHandler() {
        switch (this) {
            case FARMER: return FarmerHandler.INSTANCE;
        }
        return null;
    }
}
