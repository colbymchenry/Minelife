package com.minelife.realestate.network;

import com.google.common.collect.Lists;
import com.minelife.realestate.Estate;
import com.minelife.realestate.Permission;
import com.minelife.realestate.client.gui.GuiPurchaseEstate;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;

import java.util.List;

public class PacketGuiPurchaseEstate implements IMessage {

    public PacketGuiPurchaseEstate() {}

    private double purchasePrice, rentPrice;
    private int rentPeriod, estateID;
    private List<Permission> globalPerms, renterPerms, ownerPerms, allowedToChangePerms, estatePerms;

    public PacketGuiPurchaseEstate(Estate estate) {
        this.purchasePrice = estate.getPurchasePrice();
        this.rentPrice = estate.getRentPrice();
        this.rentPeriod = estate.getRentPeriod();
        this.globalPerms = estate.getGlobalPermissions();
        this.renterPerms = estate.getRenterPermissions();
        this.ownerPerms = estate.getOwnerPermissions();
        this.allowedToChangePerms = estate.getGlobalPermissionsAllowedToChange();
        this.estatePerms = estate.getEstatePermissions();
        this.estateID = estate.getID();
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        globalPerms = Lists.newArrayList();
        renterPerms = Lists.newArrayList();
        ownerPerms = Lists.newArrayList();
        allowedToChangePerms = Lists.newArrayList();
        estatePerms = Lists.newArrayList();

        estateID = buf.readInt();
        purchasePrice = buf.readDouble();
        rentPrice = buf.readDouble();
        rentPeriod = buf.readInt();
        int globalPermsSize = buf.readInt();
        int renterPermsSize = buf.readInt();
        int ownerPermsSize = buf.readInt();
        int allowedToChangePermsSize = buf.readInt();
        int estatePermsSize = buf.readInt();
        for (int i = 0; i < globalPermsSize; i++) globalPerms.add(Permission.valueOf(ByteBufUtils.readUTF8String(buf)));
        for (int i = 0; i < renterPermsSize; i++) renterPerms.add(Permission.valueOf(ByteBufUtils.readUTF8String(buf)));
        for (int i = 0; i < ownerPermsSize; i++) ownerPerms.add(Permission.valueOf(ByteBufUtils.readUTF8String(buf)));
        for (int i = 0; i < allowedToChangePermsSize; i++) allowedToChangePerms.add(Permission.valueOf(ByteBufUtils.readUTF8String(buf)));
        for (int i = 0; i < estatePermsSize; i++) estatePerms.add(Permission.valueOf(ByteBufUtils.readUTF8String(buf)));

    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(estateID);
        buf.writeDouble(purchasePrice);
        buf.writeDouble(rentPrice);
        buf.writeInt(rentPeriod);
        buf.writeInt(globalPerms.size());
        buf.writeInt(renterPerms.size());
        buf.writeInt(ownerPerms.size());
        buf.writeInt(allowedToChangePerms.size());
        buf.writeInt(estatePerms.size());
        globalPerms.forEach(p -> ByteBufUtils.writeUTF8String(buf, p.name()));
        renterPerms.forEach(p -> ByteBufUtils.writeUTF8String(buf, p.name()));
        ownerPerms.forEach(p -> ByteBufUtils.writeUTF8String(buf, p.name()));
        allowedToChangePerms.forEach(p -> ByteBufUtils.writeUTF8String(buf, p.name()));
        estatePerms.forEach(p -> ByteBufUtils.writeUTF8String(buf, p.name()));
    }

    public static class Handler implements IMessageHandler<PacketGuiPurchaseEstate, IMessage> {

        @SideOnly(Side.CLIENT)
        public IMessage onMessage(PacketGuiPurchaseEstate message, MessageContext ctx) {
            Minecraft.getMinecraft().displayGuiScreen(new GuiPurchaseEstate(message.estateID, message.purchasePrice, message.rentPrice, message.rentPeriod,
                    message.globalPerms, message.renterPerms, message.ownerPerms, message.allowedToChangePerms, message.estatePerms));
            return null;
        }
    }
}
