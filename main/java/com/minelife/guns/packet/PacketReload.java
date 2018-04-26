package com.minelife.guns.packet;

import com.minelife.guns.item.ItemGun;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.server.FMLServerHandler;

public class PacketReload implements IMessage {

    public PacketReload() {
    }

    @Override
    public void fromBytes(ByteBuf buf) {

    }

    @Override
    public void toBytes(ByteBuf buf) {
    }

    public static class Handler implements IMessageHandler<PacketReload, IMessage> {

        @SideOnly(Side.SERVER)
        public IMessage onMessage(PacketReload message, MessageContext ctx) {
            FMLServerHandler.instance().getServer().addScheduledTask(() -> ItemGun.reload(ctx.getServerHandler().player, ctx.getServerHandler().player.ping));
            return null;
        }

    }

}
