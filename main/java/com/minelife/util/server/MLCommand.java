package com.minelife.util.server;

import com.google.common.collect.Lists;
import com.minelife.Minelife;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.item.Item;
import net.minecraft.server.MinecraftServer;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class MLCommand implements ICommand {

    private final ExecutorService pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);

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

    @Override
    public int compareTo(Object o) {
        return 0;
    }
}
