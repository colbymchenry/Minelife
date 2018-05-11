package com.minelife.tutorial;

import com.google.common.collect.Sets;
import com.minelife.Minelife;
import com.minelife.util.NumberConversions;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Set;

public class CommandHelp extends CommandBase {

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/help";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        Set<Section> sectionSet = Sets.newTreeSet();
        for (File sectionFile : ModTutorial.getSections(new File(Minelife.getDirectory(), "tutorials"))) {
            Section section = new Section(sectionFile.getName().split("\\.")[0]);
            for (File pageFile : ModTutorial.getPages(sectionFile)) {
                Page page = new Page(NumberConversions.toInt(pageFile.getName().split("\\.")[0]));
                try {
                    Scanner scanner = new Scanner(pageFile);
                    while(scanner.hasNextLine()) page.lines.add(scanner.nextLine());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                section.pages.add(page);
            }

            sectionSet.add(section);
        }

        Minelife.getNetwork().sendTo(new PacketSendTutorials(sectionSet), (EntityPlayerMP) sender);
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return true;
    }
}
