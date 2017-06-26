package com.minelife.gun.packet;

import com.minelife.gun.item.ammos.ItemAmmo;
import com.minelife.gun.item.guns.ItemGun;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

public class PacketSetAmmoType implements IMessage {

    private ItemAmmo.AmmoType ammoType;

    public PacketSetAmmoType(){}

    public PacketSetAmmoType(ItemAmmo.AmmoType ammoType) {
        this.ammoType = ammoType;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        ammoType = ItemAmmo.AmmoType.valueOf(ByteBufUtils.readUTF8String(buf));
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, ammoType.name());
    }

    public static class Handler implements IMessageHandler<PacketSetAmmoType, IMessage> {

        @Override
        public IMessage onMessage(PacketSetAmmoType message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().playerEntity;

            ItemStack stack = player.getHeldItem();

            if(stack == null) {
                player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.RED + "You are not holding a gun."));
                return null;
            }

            if(!(stack.getItem() instanceof ItemGun)) {
                player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.RED + "You are not holding a gun."));
                return null;
            }

            NBTTagCompound tagCompound = stack.hasTagCompound() ? stack.stackTagCompound : new NBTTagCompound();
            tagCompound.setString("ammoType", message.ammoType.name());
            stack.stackTagCompound = tagCompound;

            player.addChatComponentMessage(new ChatComponentText("Ammo type changed to " +
                    EnumChatFormatting.UNDERLINE + message.ammoType.name().toLowerCase() + EnumChatFormatting.RESET + " for this gun."));
            return null;
        }
    }
}
