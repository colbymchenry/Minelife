package com.minelife.realestate.network;

import com.google.common.collect.Sets;
import com.minelife.realestate.EstateProperty;
import com.minelife.realestate.PlayerPermission;
import com.minelife.realestate.client.gui.GuiCreateEstate;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Set;

public class PacketCreateGui implements IMessage {

    private Set<PlayerPermission> allowedPermissions;
    private Set<EstateProperty> allowedProperties;

    public PacketCreateGui() {
    }

    public PacketCreateGui(Set<PlayerPermission> allowedPermissions, Set<EstateProperty> allowedProperties) {
        this.allowedPermissions = allowedPermissions;
        this.allowedProperties = allowedProperties;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.allowedPermissions = Sets.newTreeSet();
        this.allowedProperties = Sets.newTreeSet();
        int size = buf.readInt();
        for (int i = 0; i < size; i++)
            allowedPermissions.add(PlayerPermission.valueOf(ByteBufUtils.readUTF8String(buf)));
        size = buf.readInt();
        for (int i = 0; i < size; i++)
            allowedProperties.add(EstateProperty.valueOf(ByteBufUtils.readUTF8String(buf)));
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(allowedPermissions.size());
        allowedPermissions.forEach(playerPermission -> ByteBufUtils.writeUTF8String(buf, playerPermission.name()));
        buf.writeInt(allowedProperties.size());
        allowedProperties.forEach(estateProperty -> ByteBufUtils.writeUTF8String(buf, estateProperty.name()));
    }

    public static class Handler implements IMessageHandler<PacketCreateGui, IMessage> {

        @SideOnly(Side.CLIENT)
        public IMessage onMessage(PacketCreateGui message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> {
                Minecraft.getMinecraft().displayGuiScreen(new GuiCreateEstate(message.allowedPermissions, message.allowedProperties));
            });
            return null;
        }
    }

}
