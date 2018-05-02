package com.minelife.util.server;

import com.google.common.collect.Lists;
import com.minelife.Minelife;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public abstract class MLCommand extends CommandBase {

    public static ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    protected static List<Runnable> scheduledTasks = Lists.newArrayList();

    @Override
    public final synchronized void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        Runnable runnableTask = () -> {
            try {
                runAsync(server, sender, args);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        pool.execute(runnableTask);

        try {
            runSync(server, sender, args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void runAsync(MinecraftServer server, ICommandSender sender, String[] args) throws Exception {
    }

    public void runSync(MinecraftServer server, ICommandSender sender, String[] args) throws Exception {

    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        return isUsernameIndex(args, args.length) ? CommandBase.getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames()) : null;
    }

    public static class Ticker {
        @SubscribeEvent
        public void onServerTick(TickEvent.ServerTickEvent event) {
            if (!MLCommand.scheduledTasks.isEmpty()) {
                MLCommand.scheduledTasks.forEach(Runnable::run);
                MLCommand.scheduledTasks.clear();
            }
        }
    }
}
