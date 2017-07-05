package com.minelife.realestate.client.packet;

import com.minelife.CustomMessageException;
import com.minelife.Minelife;
import com.minelife.economy.ModEconomy;
import com.minelife.realestate.Estate;
import com.minelife.realestate.ModRealEstate;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import java.util.logging.Level;

public class PacketBuyChunk implements IMessage {

    public PacketBuyChunk()
    {
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
    }

    @Override
    public void toBytes(ByteBuf buf)
    {

    }

    public static class Handler implements IMessageHandler<PacketBuyChunk, IMessage> {

        @SideOnly(Side.SERVER)
        public IMessage onMessage(PacketBuyChunk message, MessageContext ctx)
        {
            EntityPlayerMP player = ctx.getServerHandler().playerEntity;
            try
            {
                long playerBalance = ModEconomy.getBalance(player.getUniqueID(), false);

                if(playerBalance < ModRealEstate.getPricePerChunk()) throw new CustomMessageException(ModRealEstate.getMessage("BuyChunkMessage_Funds"));

                Estate.createEstate(player.getEntityWorld(), player.getEntityWorld().getChunkFromBlockCoords((int) player.posX, (int) player.posY), player.getUniqueID());

                ModEconomy.withdraw(player.getUniqueID(), ModRealEstate.getPricePerChunk(), false);

                player.addChatComponentMessage(new ChatComponentText(ModRealEstate.getMessage("BuyChunkMessage_Deduction")));
                player.addChatComponentMessage(new ChatComponentText(ModRealEstate.getMessage("BuyChunkMessage_Success")));
            } catch (Exception e)
            {
                if(e instanceof CustomMessageException)
                {
                    player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.RED + e.getMessage()));
                } else
                {
                    e.printStackTrace();
                    Minelife.getLogger().log(Level.SEVERE, "", e);
                    player.addChatComponentMessage(new ChatComponentText(Minelife.default_error_message));
                }
            }

            return null;
        }

    }

}
