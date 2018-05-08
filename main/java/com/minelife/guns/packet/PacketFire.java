package com.minelife.guns.packet;

import com.minelife.guns.item.ItemGun;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.server.FMLServerHandler;

import java.util.Calendar;

public class PacketFire implements IMessage {

    private Vec3d lookVector;

    public PacketFire() {
    }

    public PacketFire(Vec3d lookVector) {
        this.lookVector = lookVector;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        lookVector = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeDouble(lookVector.x);
        buf.writeDouble(lookVector.y);
        buf.writeDouble(lookVector.z);
    }

    public static class Handler implements IMessageHandler<PacketFire, IMessage> {

        @Override
        public IMessage onMessage(PacketFire message, MessageContext ctx) {
            // TODO: Need to fix ping '0'
            FMLServerHandler.instance().getServer().addScheduledTask(() -> ItemGun.fire(ctx.getServerHandler().player, message.lookVector, 0));
            return null;
        }

    }

}
