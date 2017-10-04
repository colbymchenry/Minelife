package com.minelife.realestate.network;

import com.google.common.collect.Lists;
import com.minelife.realestate.Permission;
import com.minelife.realestate.client.gui.GuiCreateEstate;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;

import java.util.List;

public class PacketGuiCreateEstate implements IMessage {

    private List<Permission> permissions;

    public PacketGuiCreateEstate(List<Permission> permissions) {
        this.permissions = permissions;
    }

    public PacketGuiCreateEstate() {
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        permissions = Lists.newArrayList();
        int permsSize = buf.readInt();
        for (int i = 0; i < permsSize; i++)
            permissions.add(Permission.valueOf(ByteBufUtils.readUTF8String(buf)));
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(permissions.size());
        permissions.forEach(p -> ByteBufUtils.writeUTF8String(buf, p.name()));
    }

    public static class Handler implements IMessageHandler<PacketGuiCreateEstate, IMessage> {

        @SideOnly(Side.CLIENT)
        public IMessage onMessage(PacketGuiCreateEstate message, MessageContext ctx) {
            Minecraft.getMinecraft().displayGuiScreen(new GuiCreateEstate(message.permissions));
            return null;
        }
    }

}
