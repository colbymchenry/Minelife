package com.minelife.airdrop;

import com.minelife.MLProxy;
import com.minelife.Minelife;
import com.minelife.util.PlayerHelper;
import com.minelife.util.fireworks.Color;
import com.minelife.util.fireworks.FireworkBuilder;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.server.FMLServerHandler;

import java.util.ListIterator;
import java.util.Random;
import java.util.UUID;

public class ServerProxy extends MLProxy {

    private static Random random = new Random();
    private static long lastDrop = System.currentTimeMillis() + (1000L * 30);

    @Override
    public void preInit(FMLPreInitializationEvent event) throws Exception {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onTick(TickEvent.ServerTickEvent event) {

        if (System.currentTimeMillis() - lastDrop > ((1000L * ModAirdrop.config.getInt("DropDurationMinutes")) * 60)) {
            lastDrop = System.currentTimeMillis() + ((1000L * ModAirdrop.config.getInt("DropDurationMinutes")) * 60);
            Airdrop airdrop = new Airdrop();
            if (random.nextInt(100) > 50) {
                airdrop.x = ModAirdrop.config.getInt("DropCenter.x") + random.nextInt(ModAirdrop.config.getInt("DropRadius"));
            } else {
                airdrop.x = ModAirdrop.config.getInt("DropCenter.x") - random.nextInt(ModAirdrop.config.getInt("DropRadius"));
            }
            airdrop.y = 249;
            if (random.nextInt(100) > 50) {
                airdrop.z = ModAirdrop.config.getInt("DropCenter.z") + random.nextInt(ModAirdrop.config.getInt("DropRadius"));
            } else {
                airdrop.z = ModAirdrop.config.getInt("DropCenter.z") - random.nextInt(ModAirdrop.config.getInt("DropRadius"));
            }

            airdrop.id = UUID.randomUUID();
            airdrop.world = FMLServerHandler.instance().getServer().worlds[0];
            airdrop.initLoot();
            ModAirdrop.airdrops.add(airdrop);
            airdrop.sendToAll();
            PlayerHelper.sendMessageToAll("&4&lAirDrop dropped! &6&lLoot Level: &9&l" + (ModAirdrop.config.getInt("Weight") / airdrop.loot.size()));
        }

        ListIterator<Airdrop> iterator = ModAirdrop.airdrops.listIterator();
        while (iterator.hasNext()) {
            Airdrop airdrop = iterator.next();

            if (airdrop.world.getBlockState(new BlockPos(airdrop.x, airdrop.y, airdrop.z)).getBlock() == Blocks.AIR) {
                airdrop.y -= 0.015;
                airdrop.sendToAll();
            } else {
                if (airdrop.world.getBlockState(new BlockPos(airdrop.x, airdrop.y + 1, airdrop.z)).getBlock() != Blocks.CHEST) {
                    ItemStack fireworkStack = FireworkBuilder.builder().addExplosion(true, true, FireworkBuilder.Type.LARGE_BALL,
                            new int[]{Color.GREEN.asRGB(), Color.ORANGE.asRGB()}, new int[]{Color.OLIVE.asRGB(), Color.WHITE.asRGB()}).getStack(1);

                    EntityFireworkRocket ent = new EntityFireworkRocket(airdrop.world, airdrop.x, airdrop.y + 2, airdrop.z, fireworkStack);
                    airdrop.world.spawnEntity(ent);
                    EntityFireworkRocket ent1 = new EntityFireworkRocket(airdrop.world, airdrop.x, airdrop.y + 2, airdrop.z, fireworkStack);
                    airdrop.world.spawnEntity(ent1);
                    airdrop.spawnChest();
                }
            }
        }
    }

    @SubscribeEvent
    public void onInteract(PlayerInteractEvent.RightClickBlock event) {
        Airdrop airdrop = ModAirdrop.airdrops.stream().filter(a -> new BlockPos(a.x, a.y + 1, a.z).equals(event.getPos())).findFirst().orElse(null);
        if (airdrop != null) {
            Minelife.getNetwork().sendToAll(new PacketRemoveAirdrop(airdrop));
            ModAirdrop.airdrops.remove(airdrop);
            PlayerHelper.sendMessageToAll("&d[AirDrop] &6An airdrop was looted by &c" + event.getEntityPlayer().getName() + "&6!");
        }
    }


}
