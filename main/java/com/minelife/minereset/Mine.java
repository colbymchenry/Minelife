package com.minelife.minereset;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.minelife.realestate.Estate;
import com.minelife.realestate.ModRealEstate;
import com.minelife.util.PlayerHelper;
import com.minelife.util.StringHelper;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.forge.ForgeWorldEdit;
import com.sk89q.worldedit.patterns.BlockChance;
import com.sk89q.worldedit.patterns.RandomFillPattern;
import com.sk89q.worldedit.regions.CuboidRegion;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.server.FMLServerHandler;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Mine {

    private boolean tenMinuteNotification, fiveMinuteNotification, twoMinuteNotification, oneMinuteNotification;

    private UUID estateID;
    private int duration;
    private long lastGeneration;

    public Mine(UUID estateID, int duration) {
        this.estateID = estateID;
        this.duration = duration;
    }

    public UUID getEstateID() {
        return estateID;
    }

    public int getDuration() {
        return duration;
    }

    public long getLastGeneration() {
        return lastGeneration;
    }

    public void generate() {
        Estate estate = ModRealEstate.getEstate(estateID);

        if (estate == null) {
            return;
        }

        if (!ModMineReset.getConfig().contains("blocks")) return;

        long seconds = (lastGeneration - System.currentTimeMillis()) / 1000L;

        if (seconds > 550 && seconds < 600 && !tenMinuteNotification) {
            PlayerHelper.sendMessageToAll(StringHelper.ParseFormatting("&6&lOre generating in &4&l10 minutes&6&l!", '&'));
            tenMinuteNotification = true;
            return;
        }

        if (seconds > 250 && seconds < 300 && !fiveMinuteNotification) {
            PlayerHelper.sendMessageToAll(StringHelper.ParseFormatting("&6&lOre generating in &4&l5 minutes&6&l!", '&'));
            fiveMinuteNotification = true;
            return;
        }

        if (seconds > 90 && seconds < 120 && !tenMinuteNotification) {
            PlayerHelper.sendMessageToAll(StringHelper.ParseFormatting("&6&lOre generating in &4&l2 minutes&6&l!", '&'));
            twoMinuteNotification = true;
            return;
        }

        if (seconds > 50 && seconds < 60 && !tenMinuteNotification) {
            PlayerHelper.sendMessageToAll(StringHelper.ParseFormatting("&6&lOre generating in &4&l1 minute&6&l!", '&'));
            oneMinuteNotification = true;
            return;
        }


        if (seconds > 0) {
            return;
        }

        lastGeneration = System.currentTimeMillis() + (60000L * duration);

        Map<Block, Double> chance = Maps.newHashMap();
        List<BlockChance> blockChances = Lists.newArrayList();

        for (String block : ModMineReset.getConfig().getStringList("blocks")) {
            chance.put(Block.getBlockFromName(block.split(";")[0]), Double.parseDouble(block.split(";")[1]));
            blockChances.add(new BlockChance(new BaseBlock(Block.getIdFromBlock(Block.getBlockFromName(block.split(";")[0]))),
                    Double.parseDouble(block.split(";")[1])));
        }

        World world = FMLServerHandler.instance().getServer().getWorld(0);
        BlockPos min = estate.getMinimum();
        BlockPos max = estate.getMaximum();
        try {
            int w = max.getX() - min.getX();
            int h = max.getY() - min.getY();
            int l = max.getZ() - min.getZ();
            WorldEdit.getInstance().getEditSessionFactory().getEditSession(ForgeWorldEdit.inst.getWorld(world), (w * h * l) * 2).setBlocks(new CuboidRegion(new Vector(min.getX(), min.getY(), min.getZ()), new Vector(max.getX(), max.getY(), max.getZ())), new RandomFillPattern(blockChances));
        } catch (WorldEditException e) {
            e.printStackTrace();
        }

        PlayerHelper.sendMessageToAll(StringHelper.ParseFormatting("&6&lOre generated!", '&'));
    }
}
