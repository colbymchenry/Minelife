package com.minelife.tdm.network;

import com.google.common.collect.Sets;
import com.minelife.tdm.client.gui.GuiLobbyPlayers;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Set;
import java.util.UUID;

public class PacketUpdateLobby implements IMessage {

    private Set<UUID> team1, team2;

    public PacketUpdateLobby() {
    }

    public PacketUpdateLobby(Set<UUID> team1, Set<UUID> team2) {
        this.team1 = team1;
        this.team2 = team2;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        team1 = Sets.newTreeSet();
        team2 = Sets.newTreeSet();
        int team1Size = buf.readInt();
        for (int i = 0; i < team1Size; i++) {
            team1.add(UUID.fromString(ByteBufUtils.readUTF8String(buf)));
        }
        int team2Size = buf.readInt();
        for (int i = 0; i < team2Size; i++) {
            team2.add(UUID.fromString(ByteBufUtils.readUTF8String(buf)));
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(team1.size());
        team1.forEach(id -> ByteBufUtils.writeUTF8String(buf, id.toString()));
        buf.writeInt(team2.size());
        team2.forEach(id -> ByteBufUtils.writeUTF8String(buf, id.toString()));
    }

    public static class Handler implements IMessageHandler<PacketUpdateLobby, IMessage> {

        @SideOnly(Side.CLIENT)
        public IMessage onMessage(PacketUpdateLobby message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                GuiLobbyPlayers.team1 = message.team1;
                GuiLobbyPlayers.team2 = message.team2;
            });
            return null;
        }
    }

}
