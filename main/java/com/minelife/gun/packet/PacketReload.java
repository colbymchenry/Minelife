package com.minelife.gun.packet;

import com.minelife.Minelife;
import com.minelife.gun.item.ammos.ItemAmmo;
import com.minelife.gun.item.guns.ItemGun;
import com.minelife.util.PacketPlaySound;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

public class PacketReload implements IMessage {

    public PacketReload() {
    }

    @Override
    public void fromBytes(ByteBuf buf) {
    }

    @Override
    public void toBytes(ByteBuf buf) {
    }

    public static class Handler implements IMessageHandler<PacketReload, IMessage> {

        @Override
        public IMessage onMessage(PacketReload message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().playerEntity;

            ItemStack heldItem = player.getHeldItem();
            if (heldItem != null && heldItem.getItem() instanceof ItemGun) {

                ItemAmmo.AmmoType ammoType = heldItem.stackTagCompound != null && heldItem.stackTagCompound.hasKey("ammoType") ?
                        ItemAmmo.AmmoType.valueOf(heldItem.stackTagCompound.getString("ammoType")) : ItemAmmo.AmmoType.NORMAL;

                // prevent reloading if have no ammo
                if (ItemGun.getAmmoFromInventory(player, heldItem, ammoType).length == 0) {
                    player.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.RED + "Could not find any " + ammoType.name().toLowerCase() + " rounds."));
                    return null;
                }
                // prevent reloading if max ammo is already met
                if (ItemGun.getCurrentClipHoldings(heldItem) == ((ItemGun) heldItem.getItem()).getClipSize())
                    return null;

                Minelife.NETWORK.sendTo(new PacketPlaySound("guns." + ((ItemGun) heldItem.getItem()).getName() + ".reload", 5F, 1.0F), player);


                player.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 30 * 20, 1));

                NBTTagCompound tagCompound = heldItem.hasTagCompound() ? heldItem.stackTagCompound : new NBTTagCompound();

                tagCompound.setLong("reloadTime", System.currentTimeMillis() + ((ItemGun) heldItem.getItem()).getReloadTime());

                heldItem.stackTagCompound = tagCompound;

            }
            return null;
        }

    }

}
