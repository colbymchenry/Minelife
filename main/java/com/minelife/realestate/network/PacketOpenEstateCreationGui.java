package com.minelife.realestate.network;

import com.minelife.realestate.EnumPermission;
import com.minelife.realestate.client.gui.GuiEstateCreationForm;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;

import java.util.Set;
import java.util.TreeSet;

public class PacketOpenEstateCreationGui implements IMessage {

    private Set<EnumPermission> permissionsAllowedToEdit;

    public PacketOpenEstateCreationGui(Set<EnumPermission> permissionsAllowedToEdit)
    {
        this.permissionsAllowedToEdit = permissionsAllowedToEdit;
    }

    public PacketOpenEstateCreationGui()
    {
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        permissionsAllowedToEdit = new TreeSet<>();
        int permSize = buf.readInt();
        for (int i = 0; i < permSize; i++) permissionsAllowedToEdit.add(EnumPermission.values()[buf.readInt()]);

    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(permissionsAllowedToEdit.size());
        permissionsAllowedToEdit.forEach(p -> buf.writeInt(p.ordinal()));
    }

    public static class Handler implements IMessageHandler<PacketOpenEstateCreationGui, IMessage> {

        @SideOnly(Side.CLIENT)
        public IMessage onMessage(PacketOpenEstateCreationGui message, MessageContext ctx)
        {
            Minecraft.getMinecraft().displayGuiScreen(new GuiEstateCreationForm(message.permissionsAllowedToEdit));
            return null;
        }
    }
}
