package com.minelife.guns.packet;

import com.google.common.collect.Lists;
import com.minelife.Minelife;
import com.minelife.guns.ModGuns;
import com.minelife.guns.item.EnumGun;
import com.minelife.permission.ModPermission;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.List;

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

            List<EnumGun> availableSkins = Lists.newArrayList();
            EnumGun gunType = EnumGun.values()[gunStack.getMetadata()];

            for (EnumGun gun : EnumGun.values()) {
                boolean sameGunType = gunType.name().contains(gun.name().contains("_") ? gun.name().split("_")[0] : gun.name());
                if(sameGunType && ModPermission.hasPermission(ctx.getServerHandler().player.getUniqueID(), "gun.skin." + gun.name().toLowerCase()))
                    availableSkins.add(gun);
            }

            Minelife.getNetwork().sendTo(new PacketOpenModifyGUI(message.gunSlot, availableSkins), ctx.getServerHandler().player);
            return null;
        }
    }

}
