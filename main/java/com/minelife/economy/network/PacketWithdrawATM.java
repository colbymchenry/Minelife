package com.minelife.economy.network;

import com.minelife.economy.ModEconomy;
import com.minelife.economy.item.ItemCash;
import com.minelife.economy.server.CommandEconomy;
import com.minelife.util.NumberConversions;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketWithdrawATM implements IMessage {

    private int amount;

    public PacketWithdrawATM() {
    }

    public PacketWithdrawATM(int amount) {
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

    public static class Handler implements IMessageHandler<PacketWithdrawATM, IMessage> {

        @SideOnly(Side.SERVER)
        public IMessage onMessage(PacketWithdrawATM message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            if(ModEconomy.getBalanceATM(player.getUniqueID()) < message.amount) {
                return null;
            }

            int firstAmount = message.amount;

            message.amount = ModEconomy.depositInventory(player, message.amount);

            ModEconomy.withdrawATM(player.getUniqueID(), firstAmount - message.amount);

            if(message.amount > 0) {
                CommandEconomy.sendMessage(player, TextFormatting.RED + "$" + NumberConversions.format(message.amount) + TextFormatting.GOLD + " did not fit in your inventory.");
            }

            player.closeScreen();
            return null;
        }
    }

}
