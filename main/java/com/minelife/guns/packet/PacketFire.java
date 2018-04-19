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

public class PacketFire implements IMessage {

    private long timeStamp;
    private Vec3d lookVector;

    public PacketFire() {
    }
// TODO: Take out timestamp from this and reload packet, not needed if the player.ping works
    public PacketFire(Vec3d lookVector, long timeStamp) {
        this.lookVector = lookVector;
        this.timeStamp = timeStamp;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        timeStamp = buf.readLong();
        lookVector = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(timeStamp);
        buf.writeDouble(lookVector.x);
        buf.writeDouble(lookVector.y);
        buf.writeDouble(lookVector.z);
    }

    public static class Handler implements IMessageHandler<PacketFire, IMessage> {

        @Override
        public IMessage onMessage(PacketFire message, MessageContext ctx) {
            FMLServerHandler.instance().getServer().addScheduledTask(() -> ItemGun.fire(ctx.getServerHandler().player, message.lookVector, ctx.getServerHandler().player.ping));
            return null;
        }

    }

}
