package com.minelife.chestshop.network;

import com.minelife.chestshop.TileEntityChestShop;
import com.minelife.economy.ModEconomy;
import com.minelife.util.NumberConversions;
import com.minelife.util.client.PacketPopup;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.server.FMLServerHandler;

public class PacketBuyFromShop implements IMessage {

    private BlockPos pos;
    private int amount;

    public PacketBuyFromShop() {
    }

    public PacketBuyFromShop(BlockPos pos, int amount) {
        this.pos = pos;
        this.amount = amount;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.pos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
        this.amount = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.pos.getX());
        buf.writeInt(this.pos.getY());
        buf.writeInt(this.pos.getZ());
        buf.writeInt(this.amount);
    }


    public static class Handler implements IMessageHandler<PacketBuyFromShop, IMessage> {

        @SideOnly(Side.SERVER)
        public IMessage onMessage(PacketBuyFromShop message, MessageContext ctx) {
            FMLServerHandler.instance().getServer().addScheduledTask(() -> {
                EntityPlayerMP player = ctx.getServerHandler().player;

                if(!(player.world.getTileEntity(message.pos) instanceof TileEntityChestShop)) {
                    player.closeScreen();
                    player.sendMessage(new TextComponentString(TextFormatting.LIGHT_PURPLE + "[Shop] " + TextFormatting.GOLD + "Shop not found."));
                    return;
                }

                TileEntityChestShop tile = (TileEntityChestShop) player.world.getTileEntity(message.pos);

                int balance = ModEconomy.getBalanceInventory(player);

                if (tile.getItem() == null) {
                    player.closeScreen();
                    player.sendMessage(new TextComponentString(TextFormatting.LIGHT_PURPLE + "[Shop] " + TextFormatting.GOLD + "Item not set."));
                    return;
                }

                if (balance < tile.getPrice() * message.amount) {
                    PacketPopup.sendPopup("Insufficient funds in inventory.", player);
                    return;
                }

                if (tile.getStockCount() < tile.getItem().getCount() * message.amount) {
                    PacketPopup.sendPopup("Out of stock.", player);
                    return;
                }

                tile.doPurchase(player, message.amount);
                int didNotFitCash = ModEconomy.depositCashPiles(tile.getOwner(), tile.getPrice() * message.amount);
                int didNotFitInv = ModEconomy.withdrawInventory(player, tile.getPrice() * message.amount);

                if(didNotFitCash > 0) ModEconomy.depositATM(tile.getOwner(), didNotFitCash, true);
                if(didNotFitInv > 0){
                    ModEconomy.depositATM(player.getUniqueID(), didNotFitInv, true);
                    player.sendMessage(new TextComponentString(TextFormatting.LIGHT_PURPLE + "[Shop] " + TextFormatting.GOLD + "$" + NumberConversions.format(didNotFitInv) + " did not fit in your inventory and was deposited into your ATM."));
                }
            });
            return null;
        }

    }
}
