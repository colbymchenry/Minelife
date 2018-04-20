package com.minelife.gangs.network;

import com.minelife.gangs.Gang;
import com.minelife.notifications.Notification;
import com.minelife.notifications.NotificationType;
import com.minelife.util.PlayerHelper;
import com.minelife.util.client.PacketPopup;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.sql.SQLException;
import java.util.Set;

public class PacketRemoveAlliance implements IMessage {

    private String gang;

    public PacketRemoveAlliance() {
    }

    public PacketRemoveAlliance(String gang) {
        this.gang = gang;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        gang = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, gang);
    }

    // TODO: Will need to remove gangs once they are disbanded from all gangs with alliances with them.
    public static class Handler implements IMessageHandler<PacketRemoveAlliance, IMessage> {

        @SideOnly(Side.SERVER)
        public IMessage onMessage(PacketRemoveAlliance message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            Gang playerGang = Gang.getGangForPlayer(player.getUniqueID());

            if(playerGang == null) {
                PacketPopup.sendPopup("You do not belong to a gang.", player);
                return null;
            }

            Gang toRemove = Gang.getGang(message.gang);
            if(toRemove == null) {
                PacketPopup.sendPopup("Gang not found.", player);
                return null;
            }

            if(!playerGang.getAlliances().contains(toRemove)) {
                PacketPopup.sendPopup("You are not allies with that gang.", player);
                return null;
            }

            Set<Gang> allies = playerGang.getAlliances();
            allies.remove(toRemove);
            playerGang.setAlliances(allies);
            allies = toRemove.getAlliances();
            allies.remove(playerGang);
            toRemove.setAlliances(allies);
            playerGang.writeToDatabase();
            toRemove.writeToDatabase();

            Notification notification = new Notification(toRemove.getOwner(), TextFormatting.RED + playerGang.getName() + TextFormatting.DARK_GRAY + " has ended their alliance with you.", NotificationType.EDGED, 10, 0xFFFFFF);
            if(PlayerHelper.getPlayer(toRemove.getOwner()) != null) {
                notification.sendTo(PlayerHelper.getPlayer(toRemove.getOwner()), true, true, false);
            } else {
                try {
                    notification.save();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            playerGang.getMembers().forEach((playerID, rank) -> {
                Notification n = new Notification(playerID, TextFormatting.DARK_GRAY + "Your gang has ended an alliance with " + TextFormatting.RED +  toRemove.getName(), NotificationType.EDGED, 10, 0xFFFFFF);
                if(PlayerHelper.getPlayer(playerID) != null) n.sendTo(PlayerHelper.getPlayer(playerID), true, true, false);
                else {
                    try {
                        n.save();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            });
            return null;
        }
    }

}
