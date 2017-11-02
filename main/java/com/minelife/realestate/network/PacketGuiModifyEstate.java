package com.minelife.realestate.network;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.minelife.realestate.Estate;
import com.minelife.realestate.Permission;
import com.minelife.realestate.EstateData;
import com.minelife.realestate.client.gui.GuiModifyEstate;
import com.minelife.util.configuration.InvalidConfigurationException;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;

import java.io.IOException;
import java.util.List;
import java.util.Set;

public class PacketGuiModifyEstate implements IMessage {

    private EstateData estateData;
    private Set<Permission> playerPermissions;

    public PacketGuiModifyEstate(Estate estate, Set<Permission> playerPermissions) {
        this.playerPermissions = playerPermissions;
        estateData = new EstateData(estate);
    }

    public PacketGuiModifyEstate() {
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        try {
            playerPermissions = Sets.newTreeSet();
            estateData = EstateData.fromBytes(buf);
            int permsSize = buf.readInt();
            for (int i = 0; i < permsSize; i++) playerPermissions.add(Permission.valueOf(ByteBufUtils.readUTF8String(buf)));
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        estateData.toBytes(buf);
        buf.writeInt(playerPermissions.size());
        playerPermissions.forEach(p -> ByteBufUtils.writeUTF8String(buf, p.name()));
    }

    public static class Handler implements IMessageHandler<PacketGuiModifyEstate, IMessage> {

        @SideOnly(Side.CLIENT)
        public IMessage onMessage(PacketGuiModifyEstate message, MessageContext ctx) {
            Minecraft.getMinecraft().displayGuiScreen(new GuiModifyEstate(message.estateData, message.playerPermissions));
            return null;
        }
    }

}
