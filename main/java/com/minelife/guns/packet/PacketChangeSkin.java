package com.minelife.guns.packet;

import com.minelife.Minelife;
import com.minelife.guns.ModGuns;
import com.minelife.guns.item.EnumGun;
import com.minelife.permission.ModPermission;
import com.minelife.util.client.PacketPopup;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.server.FMLServerHandler;

public class PacketChangeSkin implements IMessage {

    private EnumGun gunSkin;

    public PacketChangeSkin() {
    }

    public PacketChangeSkin(EnumGun gunSkin) {
        this.gunSkin = gunSkin;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        gunSkin = EnumGun.values()[buf.readInt()];
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(gunSkin.ordinal());
    }

    public static class Handler implements IMessageHandler<PacketChangeSkin, IMessage> {

        @SideOnly(Side.SERVER)
        public IMessage onMessage(PacketChangeSkin message, MessageContext ctx) {
            FMLServerHandler.instance().getServer().addScheduledTask(() -> {
                EntityPlayerMP player = ctx.getServerHandler().player;
                ItemStack heldItem = player.getHeldItemMainhand();

                if(message.gunSkin.defaultSkin != null && !ModPermission.hasPermission(player.getUniqueID(), "gun.skin." + message.gunSkin.name().toLowerCase())) {
                    PacketPopup.sendPopup("You do not have permission to use that skin.", player);
                    return;
                }

                if(heldItem.getItem() != ModGuns.itemGun)  {
                    PacketPopup.sendPopup("Could not find gun.", player);
                    return;
                }

                EnumGun currentGunType = EnumGun.values()[heldItem.getMetadata()];
                boolean sameGunType = currentGunType.name().contains(message.gunSkin.name().contains("_") ? message.gunSkin.name().split("_")[0] : message.gunSkin.name());
                if(!sameGunType) {
                    PacketPopup.sendPopup("That is not the same gun type.", player);
                    return;
                }

                heldItem.setItemDamage(message.gunSkin.ordinal());
                player.inventoryContainer.detectAndSendChanges();

                Minelife.getNetwork().sendTo(new PacketChangeSkinResponse(message.gunSkin), player);
            });
            return null;
        }
    }

}
