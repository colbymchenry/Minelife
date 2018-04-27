package com.minelife.guns.packet;

import com.minelife.Minelife;
import com.minelife.guns.ModGuns;
import com.minelife.guns.item.EnumGun;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketRequestModifyGUI implements IMessage {

    private int gunSlot;

    public PacketRequestModifyGUI(int gunSlot) {
        this.gunSlot = gunSlot;
    }

    public PacketRequestModifyGUI() {
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        gunSlot = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(gunSlot);
    }

    public static class Handler implements IMessageHandler<PacketRequestModifyGUI, IMessage> {

        @Override
        public IMessage onMessage(PacketRequestModifyGUI message, MessageContext ctx) {
            ItemStack gunStack = ctx.getServerHandler().player.getHeldItemMainhand();
            if(gunStack.getItem() != ModGuns.itemGun) return null;
            Minelife.getNetwork().sendTo(new PacketOpenModifyGUI(message.gunSlot, EnumGun.getGunSkins(ctx.getServerHandler().player, gunStack)), ctx.getServerHandler().player);
            return null;
        }
    }

}
