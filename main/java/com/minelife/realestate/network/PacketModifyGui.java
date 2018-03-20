package com.minelife.realestate.network;

import com.google.common.collect.Sets;
import com.minelife.realestate.Estate;
import com.minelife.realestate.EstateProperty;
import com.minelife.realestate.PlayerPermission;
import com.minelife.realestate.client.gui.GuiModifyEstate;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Set;
import java.util.UUID;

public class PacketModifyGui implements IMessage {

    private Estate estate;
    private Set<PlayerPermission> allowedPermissions;
    private Set<EstateProperty> allowedProperties;

    public PacketModifyGui() {
    }

    public PacketModifyGui(Estate estate, Set<PlayerPermission> allowedPermissions, Set<EstateProperty> allowedProperties) {
        this.estate = estate;
        this.allowedPermissions = allowedPermissions;
        this.allowedProperties = allowedProperties;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        UUID id = UUID.fromString(ByteBufUtils.readUTF8String(buf));
        NBTTagCompound tagCompound = ByteBufUtils.readTag(buf);
        this.estate = new Estate(id, tagCompound);

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
        ByteBufUtils.writeUTF8String(buf, estate.getUniqueID().toString());
        ByteBufUtils.writeTag(buf, estate.getTagCompound());
        buf.writeInt(allowedPermissions.size());
        allowedPermissions.forEach(playerPermission -> ByteBufUtils.writeUTF8String(buf, playerPermission.name()));
        buf.writeInt(allowedProperties.size());
        allowedProperties.forEach(estateProperty -> ByteBufUtils.writeUTF8String(buf, estateProperty.name()));
    }

    public static class Handler implements IMessageHandler<PacketModifyGui, IMessage> {

        @SideOnly(Side.CLIENT)
        public IMessage onMessage(PacketModifyGui message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> Minecraft.getMinecraft().displayGuiScreen(
                    new GuiModifyEstate(message.estate, message.allowedPermissions, message.allowedProperties)));
            return null;
        }
    }

}
