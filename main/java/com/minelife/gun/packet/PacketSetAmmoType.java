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

public class PacketSetAmmoType implements IMessage {

    // TODO: Implement client side
    private int slot;
    private ItemAmmo.AmmoType ammoType;

    public PacketSetAmmoType(){}

    public PacketSetAmmoType(int slot, ItemAmmo.AmmoType ammoType) {
        this.slot = slot;
        this.ammoType = ammoType;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        slot = buf.readInt();
        ammoType = ItemAmmo.AmmoType.valueOf(ByteBufUtils.readUTF8String(buf));
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(slot);
        ByteBufUtils.writeUTF8String(buf, ammoType.name());
    }

    public static class Handler implements IMessageHandler<PacketSetAmmoType, IMessage> {

        @Override
        public IMessage onMessage(PacketSetAmmoType message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().playerEntity;

            ItemStack stack = player.inventory.mainInventory[message.slot];

            if(stack == null) return null;

            if(!(stack.getItem() instanceof ItemAmmo)) return null;

            NBTTagCompound tagCompound = stack.hasTagCompound() ? stack.stackTagCompound : new NBTTagCompound();

            tagCompound.setString("ammoType", message.ammoType.name());

            stack.stackTagCompound = tagCompound;
            return null;
        }
    }
}
