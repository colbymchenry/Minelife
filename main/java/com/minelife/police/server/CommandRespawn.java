package com.minelife.police.server;

import com.minelife.essentials.server.commands.Spawn;
import com.minelife.police.ModPolice;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public class CommandRespawn extends CommandBase {

    @Override
    public String getName() {
        return "respawn";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/respawn";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        EntityPlayer player = (EntityPlayer) sender;
        for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
            if (player.inventory.getStackInSlot(i) != ItemStack.EMPTY) {
                player.entityDropItem(player.inventory.getStackInSlot(i), 0.5f);
            }
        }

        player.inventory.clear();
        player.experienceTotal = 0;
        player.inventoryContainer.detectAndSendChanges();

        ModPolice.setUnconscious(player, false, false);

        if(Spawn.GetSpawn() != null) {
            player.setPositionAndUpdate(Spawn.GetSpawn().getX(), Spawn.GetSpawn().getY(), Spawn.GetSpawn().getZ());
        } else {
            BlockPos worldSpawn = player.world.getSpawnPoint();
            player.setPositionAndUpdate(worldSpawn.getX() + 0.5, worldSpawn.getY() + 0.5, worldSpawn.getZ() + 0.5);
        }
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return true;
    }
}
