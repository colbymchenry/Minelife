package com.minelife.gangs.server;

import com.google.common.collect.Maps;
import com.minelife.gangs.server.commands.Create;
import com.minelife.gangs.server.commands.Home;
import com.minelife.gangs.server.commands.SetHome;
import com.minelife.gangs.server.commands.UnsetHome;
import com.minelife.util.server.MLCommand;
import net.minecraft.command.ICommandSender;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class CommandGang extends MLCommand {

    private static Map<String, ICommandHandler> commandMap = Maps.newHashMap();

    static {
        commandMap.put("create", new Create());
        commandMap.put("home", new Home());
        commandMap.put("sethome", new SetHome());
        commandMap.put("unsethome", new UnsetHome());
    }

    @Override
    public String getCommandName() {
        return "gang";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return null;
    }

    @Override
    public List getCommandAliases() {
        return Arrays.asList("gangs", "g");
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public List addTabCompletionOptions(ICommandSender p_71516_1_, String[] p_71516_2_) {
        return null;
    }

    @Override
    public boolean isUsernameIndex(String[] p_82358_1_, int p_82358_2_) {
        return false;
    }

    @Override
    public synchronized void execute(ICommandSender sender, String[] args) throws Exception {
        if(args.length == 0) {
            getCommandUsage(sender);
            return;
        }

        if(commandMap.containsKey(args[0].toLowerCase())) {
            commandMap.get(args[0].toLowerCase()).execute(sender, args);
        } else {
            getCommandUsage(sender);
        }
    }
}
