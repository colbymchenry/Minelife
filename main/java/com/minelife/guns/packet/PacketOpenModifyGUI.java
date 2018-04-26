package com.minelife.guns.packet;

import com.google.common.collect.Lists;
import com.minelife.guns.client.GuiModifyGun;
import com.minelife.guns.item.EnumGun;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class PacketOpenModifyGUI implements IMessage {

    private List<EnumGun> availableSkins;
    private int gunSlot;

    public PacketOpenModifyGUI() {
    }

    public PacketOpenModifyGUI(int gunSlot, List<EnumGun> availableSkins) {
        this.gunSlot = gunSlot;
        this.availableSkins = availableSkins;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        gunSlot = buf.readInt();
        availableSkins = Lists.newArrayList();
        int size = buf.readInt();
        for (int i = 0; i < size; i++) availableSkins.add(EnumGun.values()[buf.readInt()]);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(gunSlot);
        buf.writeInt(availableSkins.size());
        availableSkins.forEach(skin -> buf.writeInt(skin.ordinal()));
    }

    public static class Handler implements IMessageHandler<PacketOpenModifyGUI, IMessage> {

        @SideOnly(Side.CLIENT)
        public IMessage onMessage(PacketOpenModifyGUI message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> Minecraft.getMinecraft().displayGuiScreen(new GuiModifyGun(message.gunSlot, message.availableSkins)));
            return null;
        }
    }


}
