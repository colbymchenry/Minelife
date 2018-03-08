package com.minelife.util.server;

import com.google.common.collect.Lists;
import com.minelife.Minelife;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.item.Item;
import net.minecraft.server.MinecraftServer;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class MLCommand extends CommandBase {

    private final ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);
    protected static List<Runnable> scheduledTasks = Lists.newArrayList();

    @Override
    public final synchronized void processCommand(ICommandSender sender, String[] args) {
        pool.submit(() -> {
            try {
                execute(sender, args);
            } catch (Exception e) {
                e.printStackTrace();
                Minelife.handle_exception(e, sender);
            }
        });
    }

    public synchronized void execute(ICommandSender sender, String[] args) throws Exception {
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args)
    {
        return isUsernameIndex(args, args.length) ? CommandBase.getListOfStringsMatchingLastWord(args, this.getPlayers()) : Lists.newArrayList();
    }

    protected String[] getPlayers()
    {
        return MinecraftServer.getServer().getAllUsernames();
    }

    public static class Ticker {
        @SubscribeEvent
        public void onServerTick(TickEvent.ServerTickEvent event) {
            if(!MLCommand.scheduledTasks.isEmpty()) {
                MLCommand.scheduledTasks.forEach(task -> task.run());
                MLCommand.scheduledTasks.clear();
            }
        }
    }
}
