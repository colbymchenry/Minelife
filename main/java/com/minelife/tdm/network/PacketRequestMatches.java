package com.minelife.tdm.network;

import com.google.common.collect.Sets;
import com.minelife.tdm.Arena;
import com.minelife.tdm.Match;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.Set;

public class PacketRequestMatches implements IMessage {

    public PacketRequestMatches() {
    }

    @Override
    public void fromBytes(ByteBuf buf) {

    }

    @Override
    public void toBytes(ByteBuf buf) {

    }

    public static class Handler implements IMessageHandler<PacketRequestMatches, IMessage> {

        @Override
        public IMessage onMessage(PacketRequestMatches message, MessageContext ctx) {
            Set<String> arenaNames = Sets.newTreeSet();
            Arena.ARENAS.forEach(arena -> arenaNames.add(arena.getName()));

            return null;
        }
    }

}
