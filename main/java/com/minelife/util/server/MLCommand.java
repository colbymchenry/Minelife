package com.minelife.util.server;

import com.minelife.Minelife;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;

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
    public int compareTo(Object o) {
        return 0;
    }
}
