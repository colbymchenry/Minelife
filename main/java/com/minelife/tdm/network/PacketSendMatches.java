package com.minelife.tdm.network;

import com.minelife.tdm.Match;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import java.util.Set;

public class PacketSendMatches implements IMessage {

    private Set<String> arenaNames;
    private Set<Match> activeMatches;

    public PacketSendMatches() {
    }

    public PacketSendMatches(Set<String> arenaNames, Set<Match> activeMatches) {
        this.arenaNames = arenaNames;
        this.activeMatches = activeMatches;
    }

    @Override
    public void fromBytes(ByteBuf buf) {

    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(arenaNames.size());

    }
}
