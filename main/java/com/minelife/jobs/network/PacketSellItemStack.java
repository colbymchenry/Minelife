package com.minelife.jobs.network;

import com.minelife.economy.ModEconomy;
import com.minelife.jobs.EnumJob;
import com.minelife.jobs.job.SellingOption;
import com.minelife.util.ItemHelper;
import com.minelife.util.NumberConversions;
import com.minelife.util.client.PacketPopup;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.server.FMLServerHandler;

public class PacketSellItemStack implements IMessage {

    private EnumJob job;
    private ItemStack stack;
    private boolean sellAll;

    public PacketSellItemStack() {
    }

    public PacketSellItemStack(EnumJob job, ItemStack stack, boolean sellAll) {
        this.job = job;
        this.stack = stack;
        this.sellAll = sellAll;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        job = EnumJob.valueOf(ByteBufUtils.readUTF8String(buf));
        stack = ByteBufUtils.readItemStack(buf);
        sellAll = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, job.name());
        ByteBufUtils.writeItemStack(buf, stack);
        buf.writeBoolean(sellAll);
    }

    public static class Handler implements IMessageHandler<PacketSellItemStack, IMessage> {

        @SideOnly(Side.SERVER)
        public IMessage onMessage(PacketSellItemStack message, MessageContext ctx) {
            FMLServerHandler.instance().getServer().addScheduledTask(() -> {
                EntityPlayerMP player = ctx.getServerHandler().player;

                for (SellingOption sellingOption : message.job.getHandler().getSellingOptions()) {
                    if (ItemHelper.areStacksIdentical(sellingOption.getStack(), message.stack)) {
                        if(ItemHelper.amountInInventory(player, message.stack) < sellingOption.getStack().getCount()) {
                            PacketPopup.sendPopup("Insufficient items.", player);
                            return;
                        }

                        if(!message.sellAll) {
                            ItemHelper.removeFromPlayerInventory(player, sellingOption.getStack(), sellingOption.getStack().getCount());
                            int didNotFit = ModEconomy.depositInventory(player, sellingOption.getPrice());
                            if (didNotFit > 0) {
                                PacketPopup.sendPopup(TextFormatting.RED + "$" + NumberConversions.format(didNotFit) + TextFormatting.DARK_GRAY + " did not fit in your inventory and was deposited into your ATM.", player);
                            }
                        } else {
                            int didNotFit = 0;
                            while(ItemHelper.amountInInventory(player, message.stack) >= sellingOption.getStack().getCount()) {
                                ItemHelper.removeFromPlayerInventory(player, sellingOption.getStack(), sellingOption.getStack().getCount());
                                 didNotFit += ModEconomy.depositInventory(player, sellingOption.getPrice());
                            }

                            if (didNotFit > 0) {
                                PacketPopup.sendPopup(TextFormatting.RED + "$" + NumberConversions.format(didNotFit) + TextFormatting.DARK_GRAY + " did not fit in your inventory and was deposited into your ATM.", player);
                            }
                        }

                    }
                }
            });
            return null;
        }
    }
}
