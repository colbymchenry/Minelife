package com.minelife.realestate.network;

import com.google.common.collect.Lists;
import com.minelife.Minelife;
import com.minelife.realestate.*;
import com.minelife.realestate.server.SelectionHandler;
import com.minelife.util.PlayerHelper;
import com.minelife.util.client.PacketPopupMessage;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.Vec3;

import java.util.List;

public class PacketCreateEstate implements IMessage {

    private List<Permission> globalPermissions, ownerPermissions, renterPermissions, estatePermissions, globalAllowedToChangePerms;
    private double purchasePrice, rentPrice;
    private int rentPeriod;
    private String intro, outro;

    public PacketCreateEstate(List<Permission> globalPermissions, List<Permission> ownerPermissions, List<Permission> renterPermissions, List<Permission> estatePermissions, List<Permission> globalAllowedToChangePerms, double purchasePrice, double rentPrice, int rentPeriod, String intro, String outro) {
        this.globalPermissions = globalPermissions;
        this.ownerPermissions = ownerPermissions;
        this.renterPermissions = renterPermissions;
        this.estatePermissions = estatePermissions;
        this.globalAllowedToChangePerms = globalAllowedToChangePerms;
        this.purchasePrice = purchasePrice;
        this.rentPrice = rentPrice;
        this.rentPeriod = rentPeriod;
        this.intro = intro;
        this.outro = outro;
    }

    public PacketCreateEstate() {
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        globalPermissions = Lists.newArrayList();
        ownerPermissions = Lists.newArrayList();
        renterPermissions = Lists.newArrayList();
        estatePermissions = Lists.newArrayList();
        globalAllowedToChangePerms = Lists.newArrayList();
        purchasePrice = buf.readDouble();
        rentPrice = buf.readDouble();
        rentPeriod = buf.readInt();
        intro = ByteBufUtils.readUTF8String(buf);
        outro = ByteBufUtils.readUTF8String(buf);
        int globalPermsSize = buf.readInt();
        int ownerPermsSize = buf.readInt();
        int renterPermsSize = buf.readInt();
        int estatePermsSize = buf.readInt();
        int allowedToChangePermsSize = buf.readInt();
        for (int i = 0; i < globalPermsSize; i++)
            globalPermissions.add(Permission.valueOf(ByteBufUtils.readUTF8String(buf)));
        for (int i = 0; i < ownerPermsSize; i++)
            ownerPermissions.add(Permission.valueOf(ByteBufUtils.readUTF8String(buf)));
        for (int i = 0; i < renterPermsSize; i++)
            renterPermissions.add(Permission.valueOf(ByteBufUtils.readUTF8String(buf)));
        for (int i = 0; i < estatePermsSize; i++)
            estatePermissions.add(Permission.valueOf(ByteBufUtils.readUTF8String(buf)));
        for (int i = 0; i < allowedToChangePermsSize; i++)
            globalAllowedToChangePerms.add(Permission.valueOf(ByteBufUtils.readUTF8String(buf)));
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeDouble(purchasePrice);
        buf.writeDouble(rentPrice);
        buf.writeInt(rentPeriod);
        ByteBufUtils.writeUTF8String(buf, intro);
        ByteBufUtils.writeUTF8String(buf, outro);
        buf.writeInt(globalPermissions.size());
        buf.writeInt(ownerPermissions.size());
        buf.writeInt(renterPermissions.size());
        buf.writeInt(estatePermissions.size());
        buf.writeInt(globalAllowedToChangePerms.size());
        globalPermissions.forEach(p -> ByteBufUtils.writeUTF8String(buf, p.name()));
        ownerPermissions.forEach(p -> ByteBufUtils.writeUTF8String(buf, p.name()));
        renterPermissions.forEach(p -> ByteBufUtils.writeUTF8String(buf, p.name()));
        estatePermissions.forEach(p -> ByteBufUtils.writeUTF8String(buf, p.name()));
        globalAllowedToChangePerms.forEach(p -> ByteBufUtils.writeUTF8String(buf, p.name()));
    }

    public static class Handler implements IMessageHandler<PacketCreateEstate, IMessage> {

        @SideOnly(Side.SERVER)
        public IMessage onMessage(PacketCreateEstate message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().playerEntity;
            Estate estate = null;

            if(message.rentPrice == 0) {
                Minelife.NETWORK.sendTo(new PacketPopupMessage("Rent price cannot be 0.", 0xC6C6C6), player);
                return null;
            }

            if(message.purchasePrice == 0) {
                Minelife.NETWORK.sendTo(new PacketPopupMessage("Purchase price cannot be 0.", 0xC6C6C6), player);
                return null;
            }

            if(message.rentPrice > 0 && message.rentPeriod < 1) {
                Minelife.NETWORK.sendTo(new PacketPopupMessage("Rent period must be greater than 0.", 0xC6C6C6), player);
                return null;
            }

            try {
                Selection selection = SelectionHandler.getSelection(player);

                if (!selection.isComplete()) {
                    player.addChatComponentMessage(new ChatComponentText("Please make a full selection."));
                    return null;
                }

                Vec3 min = selection.getMin();
                min.zCoord -= 1;
                min.xCoord -= 1;
                min.yCoord -= 1;
                Vec3 max = selection.getMax();
                max.xCoord += 1;
                max.zCoord += 1;
                max.yCoord += 1;
                selection.setPos1((int) min.xCoord, (int) min.yCoord, (int) min.zCoord);
                selection.setPos2((int) max.xCoord, (int) max.yCoord, (int) max.zCoord);
                estate = EstateHandler.createEstate(player, selection);
                player.closeScreen();
            } catch (Exception e) {
                e.printStackTrace();
                player.addChatComponentMessage(new ChatComponentText(e.getMessage().length() > 100 ? e.getMessage().substring(100) : e.getMessage()));
            }

            if (estate != null) {
                estate.setPurchasePrice(message.purchasePrice);
                estate.setRentPrice(message.rentPrice);
                estate.setRentPeriod(message.rentPeriod);
                estate.setIntro(message.intro);
                estate.setOutro(message.outro);
                estate.setGlobalPermissions(message.globalPermissions);
                estate.setOwnerPermissions(message.ownerPermissions);
                estate.setRenterPermissions(message.renterPermissions);
                estate.setPermissionsAllowedToChange(message.globalAllowedToChangePerms);
                if (PlayerHelper.isOp(player))
                    estate.setEstatePermissions(message.estatePermissions);
                else if (estate.getParentEstate() == null)
                    estate.setEstatePermissions(Permission.getEstatePermissions());
            }

            player.addChatComponentMessage(new ChatComponentText(ModRealEstate.getServerProxy().config.getString("messages.estate_create", "Please set estate creation message in the config.")));
            Minelife.NETWORK.sendTo(new PacketSendSelection(null), player);
            return null;
        }
    }

}
