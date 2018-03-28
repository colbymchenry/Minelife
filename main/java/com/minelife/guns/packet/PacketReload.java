package com.minelife.guns.packet;

import com.minelife.guns.ModGuns;
import com.minelife.guns.item.EnumGun;
import com.minelife.guns.item.ItemAmmo;
import com.minelife.guns.item.ItemGun;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.server.FMLServerHandler;

public class PacketReload implements IMessage {

    private long timeStamp;

    public PacketReload() {
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        timeStamp = buf.readLong();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(System.currentTimeMillis());
    }

    public static class Handler implements IMessageHandler<PacketReload, IMessage> {

        @SideOnly(Side.SERVER)
        public IMessage onMessage(PacketReload message, MessageContext ctx) {
            FMLServerHandler.instance().getServer().addScheduledTask(() -> {
                EntityPlayerMP player = ctx.getServerHandler().player;

                if (player.getHeldItemMainhand().getItem() != ModGuns.itemGun) return;

                EnumGun gunType = EnumGun.values()[player.getHeldItemMainhand().getMetadata()];

                long pingDelay = System.currentTimeMillis() - message.timeStamp;

                pingDelay = pingDelay > 200 ? 60 : pingDelay;

                if(ItemGun.getClipCount(player.getHeldItemMainhand()) == gunType.clipSize) return;

                if(ItemGun.isReloading(player.getHeldItemMainhand())) return;

                if(ItemAmmo.getAmmoCount(player, player.getHeldItemMainhand()) <= 0) return;

                ItemStack gunStack = player.getHeldItemMainhand();
                ItemGun.setReloadTime(gunStack, System.currentTimeMillis() + gunType.reloadTime - pingDelay);
                player.setHeldItem(EnumHand.MAIN_HAND, gunStack);
            });
            return null;
        }

    }

}
