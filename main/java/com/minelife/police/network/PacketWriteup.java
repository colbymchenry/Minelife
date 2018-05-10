package com.minelife.police.network;

import com.google.common.collect.Lists;
import com.minelife.police.ChargeType;
import com.minelife.police.ModPolice;
import com.minelife.police.Prisoner;
import com.minelife.police.server.Prison;
import com.minelife.util.PlayerHelper;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class PacketWriteup implements IMessage {

    private UUID playerID;
    private List<ChargeType> charges;

    public PacketWriteup() {
    }

    public PacketWriteup(UUID playerID, List<ChargeType> charges) {
        this.playerID = playerID;
        this.charges = charges;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        playerID = UUID.fromString(ByteBufUtils.readUTF8String(buf));
        charges = Lists.newArrayList();
        int size = buf.readInt();
        for (int i = 0; i < size; i++) {
            charges.add(ChargeType.values()[buf.readInt()]);
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, playerID.toString());
        buf.writeInt(charges.size());
        if (!charges.isEmpty())
            charges.forEach(chargeType -> buf.writeInt(chargeType.ordinal()));
    }

    public static class Handler implements IMessageHandler<PacketWriteup, IMessage> {

        @SideOnly(Side.SERVER)
        public IMessage onMessage(PacketWriteup message, MessageContext ctx) {
            EntityPlayer player = ctx.getServerHandler().player;

            if (!ModPolice.isCop(player.getUniqueID()) && !PlayerHelper.isOp((EntityPlayerMP) player)) {
                player.sendMessage(new TextComponentString(TextFormatting.RED + "You are not a cop."));
                return null;
            }

            if (Prisoner.isPrisoner(message.playerID)) {
                player.sendMessage(new TextComponentString(TextFormatting.RED + "That player is already in prison."));
                return null;
            }

            Prison prison = Prison.getPrison(player.getPosition());

            if(prison == null) {
                player.sendMessage(new TextComponentString(TextFormatting.RED + "You are not in a prison."));
                return null;
            }

            Prisoner prisoner = new Prisoner(message.playerID, message.charges);

            if (PlayerHelper.getPlayer(message.playerID) != null) {
                prisoner.setSavedInventory(PlayerHelper.getPlayer(message.playerID).inventory);
                PlayerHelper.getPlayer(message.playerID).inventory.clear();
                PlayerHelper.getPlayer(message.playerID).inventoryContainer.detectAndSendChanges();
                PlayerHelper.getPlayer(message.playerID).setPositionAndUpdate(prison.getDropOffPos().getX() + 0.5, prison.getDropOffPos().getY() + 0.5, prison.getDropOffPos().getZ() + 0.5);
                ModPolice.setUnconscious(PlayerHelper.getPlayer(message.playerID), false, false);
            }

            try {
                prisoner.save();
                player.sendMessage(new TextComponentString(TextFormatting.BLUE + "Player has been jailed!"));
                player.closeScreen();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return null;
        }

    }
}
