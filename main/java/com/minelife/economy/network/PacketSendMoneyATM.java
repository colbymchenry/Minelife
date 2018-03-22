package com.minelife.economy.network;

import com.minelife.Minelife;
import com.minelife.economy.ModEconomy;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.UUID;

public class PacketSendMoneyATM implements IMessage {

    private UUID playerID;
    private int amount;

    public PacketSendMoneyATM() {
    }

    public PacketSendMoneyATM(UUID playerID, int amount) {
        this.playerID = playerID;
        this.amount = amount;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.playerID = UUID.fromString(ByteBufUtils.readUTF8String(buf));
        this.amount = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, this.playerID.toString());
        buf.writeInt(this.amount);
    }

    public static class Handler implements IMessageHandler<PacketSendMoneyATM, IMessage> {

        @SideOnly(Side.SERVER)
        public IMessage onMessage(PacketSendMoneyATM message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;

            if(ModEconomy.getBalanceATM(player.getUniqueID()) < message.amount) return null;

            ModEconomy.withdrawATM(player.getUniqueID(), message.amount);
            ModEconomy.depositATM(message.playerID, message.amount);

            // TODO: Send notification

            Minelife.getNetwork().sendTo(new PacketOpenATM(ModEconomy.getBalanceATM(player.getUniqueID())), player);
            return null;
        }
    }

}
