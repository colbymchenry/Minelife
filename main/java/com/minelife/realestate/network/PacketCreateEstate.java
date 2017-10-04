package com.minelife.realestate.network;

import com.google.common.collect.Lists;
import com.minelife.realestate.EstateHandler;
import com.minelife.realestate.Permission;
import com.minelife.realestate.server.SelectionHandler;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ChatComponentText;

import java.util.List;

public class PacketCreateEstate implements IMessage {

    private List<Permission> globalPermissions, ownerPermissions, renterPermissions;
    private double purchasePrice, rentPrice;
    private int rentPeriod;
    private boolean forRent;
    private String intro, outro;

    public PacketCreateEstate(List<Permission> globalPermissions, List<Permission> ownerPermissions, List<Permission> renterPermissions, double purchasePrice, double rentPrice, int rentPeriod, boolean forRent, String intro, String outro) {
        this.globalPermissions = globalPermissions;
        this.ownerPermissions = ownerPermissions;
        this.renterPermissions = renterPermissions;
        this.purchasePrice = purchasePrice;
        this.rentPrice = rentPrice;
        this.rentPeriod = rentPeriod;
        this.forRent = forRent;
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
        double purchasePrice = buf.readDouble();
        double rentPrice = buf.readDouble();
        int rentPeriod = buf.readInt();
        boolean forRent = buf.readBoolean();
        String intro = ByteBufUtils.readUTF8String(buf);
        String outro = ByteBufUtils.readUTF8String(buf);
        int globalPermsSize = buf.readInt();
        int ownerPermsSize = buf.readInt();
        int renterPermsSize = buf.readInt();
        for (int i = 0; i < globalPermsSize; i++)
            globalPermissions.add(Permission.valueOf(ByteBufUtils.readUTF8String(buf)));
        for (int i = 0; i < ownerPermsSize; i++)
            ownerPermissions.add(Permission.valueOf(ByteBufUtils.readUTF8String(buf)));
        for (int i = 0; i < renterPermsSize; i++)
            renterPermissions.add(Permission.valueOf(ByteBufUtils.readUTF8String(buf)));
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeDouble(purchasePrice);
        buf.writeDouble(rentPrice);
        buf.writeInt(rentPeriod);
        buf.writeBoolean(forRent);
        ByteBufUtils.writeUTF8String(buf, intro);
        ByteBufUtils.writeUTF8String(buf, outro);
        buf.writeInt(globalPermissions.size());
        buf.writeInt(ownerPermissions.size());
        buf.writeInt(renterPermissions.size());
        globalPermissions.forEach(p -> ByteBufUtils.writeUTF8String(buf, p.name()));
        ownerPermissions.forEach(p -> ByteBufUtils.writeUTF8String(buf, p.name()));
        renterPermissions.forEach(p -> ByteBufUtils.writeUTF8String(buf, p.name()));
    }

    public static class Handler implements IMessageHandler<PacketCreateEstate, IMessage> {

        @SideOnly(Side.SERVER)
        public IMessage onMessage(PacketCreateEstate message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().playerEntity;
            try {
                EstateHandler.createEstate(player, SelectionHandler.getSelection(player));
                player.closeScreen();
            } catch (Exception e) {
                e.printStackTrace();
                player.addChatComponentMessage(new ChatComponentText(e.getMessage()));
            }
            return null;
        }
    }

}
