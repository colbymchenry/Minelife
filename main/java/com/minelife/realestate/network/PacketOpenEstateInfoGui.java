package com.minelife.realestate.network;

import com.minelife.realestate.EnumPermission;
import com.minelife.realestate.Estate;
import com.minelife.realestate.client.gui.GuiEstateInfo;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;

import java.util.Set;
import java.util.TreeSet;

public class PacketOpenEstateInfoGui implements IMessage {

    private Estate estate;
    private Set<EnumPermission> permsAllowedToChange;
    private Set<EnumPermission> permsAllowedToChangeEnabled;
    private boolean isInsideTheirEstate;

    public PacketOpenEstateInfoGui(Estate estate, Set<EnumPermission> permsAllowedToChange, Set<EnumPermission> permsAllowedToChangeEnabled, boolean isInsideTheirEstate)
    {
        this.estate = estate;
        this.permsAllowedToChange = permsAllowedToChange;
        this.permsAllowedToChangeEnabled = permsAllowedToChangeEnabled;
        this.isInsideTheirEstate = isInsideTheirEstate;
    }

    public PacketOpenEstateInfoGui()
    {
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        estate = Estate.fromBytes(buf);
        permsAllowedToChange = new TreeSet<>();
        permsAllowedToChangeEnabled = new TreeSet<>();
        int permsSize = buf.readInt();
        for (int i = 0; i < permsSize; i++) permsAllowedToChange.add(EnumPermission.values()[buf.readInt()]);
        isInsideTheirEstate = buf.readBoolean();
        permsSize = buf.readInt();
        for (int i = 0; i < permsSize; i++) permsAllowedToChangeEnabled.add(EnumPermission.values()[buf.readInt()]);

    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        estate.toBytes(buf);
        buf.writeInt(permsAllowedToChange.size());
        for (EnumPermission permission : permsAllowedToChange) buf.writeInt(permission.ordinal());
        buf.writeBoolean(isInsideTheirEstate);
        buf.writeInt(permsAllowedToChangeEnabled.size());
        for (EnumPermission permission : permsAllowedToChangeEnabled) buf.writeInt(permission.ordinal());
    }

    public static class Handler implements IMessageHandler<PacketOpenEstateInfoGui, IMessage> {

        @SideOnly(Side.CLIENT)
        public IMessage onMessage(PacketOpenEstateInfoGui message, MessageContext ctx)
        {
            Minecraft.getMinecraft().displayGuiScreen(new GuiEstateInfo(message.estate, message.permsAllowedToChange, message.permsAllowedToChangeEnabled, message.isInsideTheirEstate));
            return null;
        }
    }
}
