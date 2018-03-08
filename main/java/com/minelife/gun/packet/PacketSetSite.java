package com.minelife.gun.packet;

import com.minelife.MLItems;
import com.minelife.gun.item.attachments.ItemSight;
import com.minelife.gun.item.guns.ItemGun;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

public class PacketSetSite implements IMessage {

    private int slot;

    public PacketSetSite() {
    }

    public PacketSetSite(int slot) {
        this.slot = slot;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        slot = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(slot);
    }

    public static class Handler implements IMessageHandler<PacketSetSite, IMessage> {

        @SideOnly(Side.SERVER)
        public IMessage onMessage(PacketSetSite message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().playerEntity;
            ItemStack heldItem = player.getHeldItem();

            if (heldItem == null) {
                player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.RED + "Gun not found."));
                player.closeScreen();
                return null;
            }

            if (!(heldItem.getItem() instanceof ItemGun)) {
                player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.RED + "Gun not found."));
                player.closeScreen();
                return null;
            }

            ItemGun gun = (ItemGun) heldItem.getItem();
            if (gun == MLItems.magnum) {
                player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.RED + "You cannot modify the sites on this gun."));
                player.closeScreen();
                return null;
            }

            // Removing the sight
            if (message.slot == -2) {
                if(ItemGun.getSight(heldItem) == null) return null;
                ItemStack to_give = ItemGun.getSight(heldItem).copy();
                EntityItem entity_item = player.dropPlayerItemWithRandomChoice(to_give, false);
                entity_item.delayBeforeCanPickup = 0;
                ItemGun.setSight(heldItem, null);
                return null;
            }

            ItemStack stackInSlot = player.inventory.getStackInSlot(message.slot);
            if (stackInSlot != null && stackInSlot.getItem() instanceof ItemSight) {
                if (ItemGun.getSight(heldItem) != null) {
                    ItemStack to_give = ItemGun.getSight(heldItem).copy();
                    EntityItem entity_item = player.dropPlayerItemWithRandomChoice(to_give, false);
                    entity_item.delayBeforeCanPickup = 0;
                }


                ItemGun.setSight(heldItem, stackInSlot);
                player.inventory.setInventorySlotContents(message.slot, null);
                player.inventory.setInventorySlotContents(player.inventory.currentItem, heldItem);
            } else {
                player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.RED + "Holographic Site not found."));
            }
            return null;
        }
    }


}
