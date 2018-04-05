package com.minelife.economy.network;

import com.minelife.economy.ModEconomy;
import com.minelife.economy.server.CommandEconomy;
import com.minelife.util.NumberConversions;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketDepositATM implements IMessage {

    private int amount;

    public PacketDepositATM() {
    }

    public PacketDepositATM(int amount) {
        this.amount = amount;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        amount = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(amount);
    }

    public static class Handler implements IMessageHandler<PacketDepositATM, IMessage> {

        @SideOnly(Side.SERVER)
        public IMessage onMessage(PacketDepositATM message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            if(ModEconomy.getBalanceInventory(player) < message.amount) {
                return null;
            }

            ModEconomy.depositATM(player.getUniqueID(), message.amount);
            int didNotFit = ModEconomy.withdrawInventory(player, message.amount);

            if(didNotFit > 0) {
                ModEconomy.depositATM(player.getUniqueID(), didNotFit);
                CommandEconomy.sendMessage(player, TextFormatting.RED + "$" + NumberConversions.format(message.amount) + TextFormatting.GOLD + " did not fit in your inventory.");
            }

            player.closeScreen();
            return null;
        }
    }

}
