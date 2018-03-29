package com.minelife.guns.packet;

import com.minelife.guns.ModGuns;
import com.minelife.guns.item.EnumAttachment;
import com.minelife.guns.item.ItemGun;
import com.minelife.util.client.PacketPopup;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.server.FMLServerHandler;

public class PacketAttachment implements IMessage {

    private int slotAttachment, slotGun;
    private String customName;

    public PacketAttachment() {
    }

    public PacketAttachment(int slotAttachment, int slotGun, String customName) {
        this.slotAttachment = slotAttachment;
        this.slotGun = slotGun;
        this.customName = customName;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        slotAttachment = buf.readInt();
        slotGun = buf.readInt();
        customName = ByteBufUtils.readUTF8String(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(slotAttachment);
        buf.writeInt(slotGun);
        ByteBufUtils.writeUTF8String(buf, customName == null || customName.trim().isEmpty() ? " " : customName);
    }

    public static class Handler implements IMessageHandler<PacketAttachment, IMessage> {

        @Override
        public IMessage onMessage(PacketAttachment message, MessageContext ctx) {
            FMLServerHandler.instance().getServer().addScheduledTask(() -> {
                EntityPlayerMP player = ctx.getServerHandler().player;

                ItemStack gunStack = player.inventory.getStackInSlot(message.slotGun);

                if (gunStack == null || gunStack.getItem() != ModGuns.itemGun) {
                    PacketPopup.sendPopup("Invalid gun.", player);
                    return;
                }

                if (message.slotAttachment == -2) {
                    System.out.println("CALLED");
                    ItemGun.setCustomName(gunStack, message.customName.trim().isEmpty() ? null : message.customName);
                    player.inventory.setInventorySlotContents(message.slotGun, gunStack);
                    return;
                }

                ItemStack currentAttachment = ItemGun.getAttachment(gunStack) != null ?
                        new ItemStack(ModGuns.itemAttachment, 1, ItemGun.getAttachment(gunStack).ordinal()) : null;

                if (message.slotAttachment == -1) {
                    ItemGun.setAttachment(gunStack, null);
                    player.inventory.setInventorySlotContents(message.slotGun, gunStack);
                    if (currentAttachment != null) {
                        EntityItem entity_item = player.dropItem(currentAttachment, false);
                        entity_item.setPickupDelay(0);
                    }
                    return;
                }

                ItemStack attachmentStack = player.inventory.getStackInSlot(message.slotAttachment);

                if (attachmentStack == null || attachmentStack.getItem() != ModGuns.itemAttachment) {
                    PacketPopup.sendPopup("Invalid attachment.", player);
                    return;
                }

                ItemGun.setAttachment(gunStack, EnumAttachment.values()[attachmentStack.getMetadata()]);


                if (currentAttachment != null) {
                    EntityItem entity_item = player.dropItem(currentAttachment, false);
                    entity_item.setPickupDelay(0);
                }

                ItemGun.setCustomName(gunStack, message.customName.trim().isEmpty() ? null : message.customName);
                player.inventory.setInventorySlotContents(message.slotGun, gunStack);
                player.inventory.setInventorySlotContents(message.slotAttachment, null);
            });
            return null;
        }

    }

}
