package com.minelife.tdm.network;

import blusunrize.immersiveengineering.common.IEContent;
import com.minelife.Minelife;
import com.minelife.guns.ModGuns;
import com.minelife.guns.item.EnumAttachment;
import com.minelife.guns.item.EnumGun;
import com.minelife.guns.item.ItemGun;
import com.minelife.tdm.Match;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketSetLoudout implements IMessage {

    private EnumGun primary, secondary;
    private EnumAttachment primarySite, secondarySite;

    public PacketSetLoudout() {
    }

    public PacketSetLoudout(EnumGun primary, EnumGun secondary, EnumAttachment primarySite, EnumAttachment secondarySite) {
        this.primary = primary;
        this.secondary = secondary;
        this.primarySite = primarySite;
        this.secondarySite = secondarySite;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        primary = EnumGun.values()[buf.readInt()];
        secondary = EnumGun.values()[buf.readInt()];
        if(buf.readBoolean()) primarySite = EnumAttachment.values()[buf.readInt()];
        if(buf.readBoolean()) secondarySite = EnumAttachment.values()[buf.readInt()];
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(primary.ordinal());
        buf.writeInt(secondary.ordinal());
        buf.writeBoolean(primarySite != null);
        if(primarySite != null) buf.writeInt(primarySite.ordinal());
        buf.writeBoolean(secondarySite != null);
        if(secondarySite != null) buf.writeInt(secondarySite.ordinal());
    }

    public static class Handler implements IMessageHandler<PacketSetLoudout, IMessage> {
        @SideOnly(Side.SERVER)
        public IMessage onMessage(PacketSetLoudout message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            Match match = Match.getMatch(player);

            if(match == null) {
                player.closeScreen();
                player.sendMessage(new TextComponentString(TextFormatting.RED + "You are not in a match."));
                return null;
            }

            ItemStack primary = new ItemStack(ModGuns.itemGun, 1, message.primary.ordinal());
            ItemStack secondary = new ItemStack(ModGuns.itemGun, 1, message.secondary.ordinal());
            ItemGun.setAttachment(primary, message.primarySite);
            ItemGun.setAttachment(secondary, message.secondarySite);

            ItemStack potionStack = new ItemStack(Items.POTIONITEM, 1);
            NBTTagCompound tagCompound =  new NBTTagCompound();
            tagCompound.setString("Potion", "minecraft:regeneration");
            potionStack.setTagCompound(tagCompound);

            ItemStack ammoStack = new ItemStack(IEContent.itemBullet, 64, 2);
            tagCompound = new NBTTagCompound();
            tagCompound.setString("bullet", "casull");
            ammoStack.setTagCompound(tagCompound);

            match.setLoadout(player.getUniqueID(), primary, secondary, ammoStack.copy(), ammoStack.copy(), ammoStack.copy(),
                    ammoStack.copy(), ammoStack.copy(), potionStack);

            Minelife.getNetwork().sendTo(new PacketOpenLobbyPlayers(), player);
            return null;
        }
    }

}
