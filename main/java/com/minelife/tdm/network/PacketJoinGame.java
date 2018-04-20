package com.minelife.tdm.network;

import com.minelife.essentials.Location;
import com.minelife.essentials.TeleportHandler;
import com.minelife.tdm.Arena;
import com.minelife.tdm.Match;
import com.minelife.util.client.PacketPopup;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.server.FMLServerHandler;

import java.util.UUID;

public class PacketJoinGame implements IMessage {

    private String arena;

    public PacketJoinGame() {
    }

    public PacketJoinGame(String arena) {
        this.arena = arena;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        arena = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, arena);
    }

    public static class Handler implements IMessageHandler<PacketJoinGame, IMessage> {

        @Override
        public IMessage onMessage(PacketJoinGame message, MessageContext ctx) {
            FMLServerHandler.instance().getServer().addScheduledTask(() -> {
                EntityPlayerMP player = ctx.getServerHandler().player;
                Arena arena = Arena.getArena(message.arena);
                Match match = arena.getCurrentMatch();

                if (match == null) {
                    match = Match.builder().setArena(arena).setTeam1(player.getUniqueID()).setTeam2(new UUID[]{})
                            .setCountdownBetweenRounds(5).setStartCountdown(120).setRounds(5).setTeam1MaxSize(12).setTeam2MaxSize(12);
                }


                if (!match.getTeam1().contains(player.getUniqueID()) && !match.getTeam2().contains(player.getUniqueID())) {
                    if (match.getTeam1().size() >= match.getTeam2().size() && match.getTeam2().size() + 1 < match.getTeam2MaxSize()) {
                        match.addTeam2(player.getUniqueID());
                    } else if (match.getTeam1().size() < match.getTeam2().size() && match.getTeam1().size() + 1 < match.getTeam1MaxSize()) {
                        match.addTeam1(player.getUniqueID());
                    } else {
                        PacketPopup.sendPopup("Match full.", player);
                        return;
                    }
                }

                Location lobbySpawn = new Location(match.getArena().getEstate().getWorld().provider.getDimension(), match.getArena().getLobbySpawn().getX(), match.getArena().getLobbySpawn().getY(), match.getArena().getLobbySpawn().getZ());
                match.setPreviousInventory(player);
                TeleportHandler.teleport(player, lobbySpawn, 0);
            });
            return null;
        }

    }


}
