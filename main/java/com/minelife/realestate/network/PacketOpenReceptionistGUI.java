package com.minelife.realestate.network;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.minelife.realestate.Estate;
import com.minelife.realestate.PlayerPermission;
import com.minelife.realestate.client.gui.GuiReceptionist;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class PacketOpenReceptionistGUI implements IMessage {

    private Map<Estate, Set<PlayerPermission>> estates;

    public PacketOpenReceptionistGUI() {
    }

    public PacketOpenReceptionistGUI(Map<Estate, Set<PlayerPermission>> estates) {
        this.estates = estates;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        estates = Maps.newHashMap();
        int estatesSize = buf.readInt();
        for (int i = 0; i < estatesSize; i++) {
            Estate e = new Estate(UUID.fromString(ByteBufUtils.readUTF8String(buf)), ByteBufUtils.readTag(buf));
            Set<PlayerPermission> renterPerms = Sets.newTreeSet();
            int permsSize = buf.readInt();
            for (int i1 = 0; i1 < permsSize; i1++) renterPerms.add(PlayerPermission.values()[buf.readInt()]);
            estates.put(e, renterPerms);
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(estates.size());
        estates.forEach((estate, renterPerms) -> {
            ByteBufUtils.writeUTF8String(buf, estate.getUniqueID().toString());
            ByteBufUtils.writeTag(buf, estate.getTagCompound());
            buf.writeInt(renterPerms.size());
            renterPerms.forEach(perm -> buf.writeInt(perm.ordinal()));
        });
    }

    public static class Handler implements IMessageHandler<PacketOpenReceptionistGUI, IMessage> {

        @SideOnly(Side.CLIENT)
        public IMessage onMessage(PacketOpenReceptionistGUI message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> Minecraft.getMinecraft().displayGuiScreen(new GuiReceptionist(message.estates)));
            return null;
        }
    }

}
