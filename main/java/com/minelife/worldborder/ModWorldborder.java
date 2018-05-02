package com.minelife.worldborder;

import com.google.common.collect.Maps;
import com.minelife.MLMod;
import com.minelife.essentials.TeleportHandler;
import com.minelife.essentials.server.commands.Spawn;
import com.minelife.util.MLConfig;
import com.minelife.util.NumberConversions;
import com.minelife.util.client.render.Vector;
import com.minelife.util.configuration.InvalidConfigurationException;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

public class ModWorldborder extends MLMod {

    private MLConfig config;

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        if (event.getSide() == Side.SERVER) {
            try {
                config = new MLConfig("worldborder");
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }
            MinecraftForge.EVENT_BUS.register(this);
        }
    }

    @Override
    public void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandWorldBorder());
    }

    static Map<UUID, Long> sentMessage = Maps.newHashMap();

    @SubscribeEvent
    public void playerTick(TickEvent.PlayerTickEvent event) {
        if (!config.contains("x") && !config.contains("y") && !config.contains("z") && !config.contains("radius"))
            return;

        if (event.player.posX < config.getInt("x") + config.getInt("radius") && event.player.posZ < config.getInt("z") + config.getInt("radius")
                && event.player.posX > config.getInt("x") - config.getInt("radius") && event.player.posZ > config.getInt("z") - config.getInt("radius"))
            return;

        Vector v = new Vector(event.player.posX, event.player.posY, event.player.posZ);
        Vector v1 = new Vector(config.getInt("x"), event.player.posY, config.getInt("z"));

        if (v.distanceSquared(v1) - (config.getInt("radius") * config.getInt("radius")) > 500) {
            if(!sentMessage.containsKey(event.player.getUniqueID()) || sentMessage.get(event.player.getUniqueID()) < System.currentTimeMillis()) {
                event.player.sendMessage(new TextComponentString(TextFormatting.DARK_RED + "You are out side of the server bounds!"));
                sentMessage.put(event.player.getUniqueID(), System.currentTimeMillis() + 5000L);
            }
            if (Spawn.GetSpawn() != null)
                TeleportHandler.teleport((EntityPlayerMP) event.player, Spawn.GetSpawn());
            return;
        }

        if(!sentMessage.containsKey(event.player.getUniqueID()) || sentMessage.get(event.player.getUniqueID()) < System.currentTimeMillis()) {
            event.player.sendMessage(new TextComponentString(TextFormatting.DARK_RED + "You are out side of the server bounds!"));
            sentMessage.put(event.player.getUniqueID(), System.currentTimeMillis() + 5000L);
        }

        Vector subtracted = v.subtract(v1).normalize();
        event.player.addVelocity(-subtracted.getX(), 0, -subtracted.getZ());
        event.player.velocityChanged = true;
    }

    class CommandWorldBorder extends CommandBase {

        @Override
        public String getName() {
            return "wb";
        }

        @Override
        public String getUsage(ICommandSender sender) {
            return "/wb <radius>";
        }

        @Override
        public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
            if (args.length != 1) {
                sender.sendMessage(new TextComponentString(getUsage(sender)));
                return;
            }

            EntityPlayer player = (EntityPlayer) sender;

            if (!NumberConversions.isInt(args[0])) {
                sender.sendMessage(new TextComponentString("Radius must be an integer."));
                return;
            }

            int radius = NumberConversions.toInt(args[0]);
            config.set("x", (int) player.posX);
            config.set("y", (int) player.posY);
            config.set("z", (int) player.posZ);
            config.set("radius", radius);
            config.save();
            player.sendMessage(new TextComponentString("World Border set!"));
        }
    }

}
