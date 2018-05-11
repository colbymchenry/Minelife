package com.minelife.police.server;

import com.google.common.collect.Sets;
import com.minelife.Minelife;
import com.minelife.essentials.server.commands.Spawn;
import com.minelife.police.ItemHandcuff;
import com.minelife.police.ModPolice;
import com.minelife.tdm.SavedInventory;
import com.minelife.util.client.PacketDropEntity;
import com.minelife.util.configuration.InvalidConfigurationException;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemBucketMilk;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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

        Set<Potion> p =  player.getActivePotionMap().keySet();
        Set<Potion> potions = Sets.newConcurrentHashSet();
        potions.addAll(p);
        Iterator<Potion> iterator = potions.iterator();
        while(iterator.hasNext()) player.removePotionEffect(iterator.next());

        ItemHandcuff.setHandcuffed(player, false, false);
        ModPolice.setUnconscious(player, false, false);
        Minelife.getNetwork().sendToAll(new PacketDropEntity(player.getEntityId()));
        player.dismountRidingEntity();

        try {
            SavedInventory savedInventory = new SavedInventory(player.getUniqueID());
            if(savedInventory != null) savedInventory.delete();
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }

        if (Spawn.GetSpawn() != null) {
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
