package com.minelife.chestshop.network;

import com.minelife.chestshop.TileEntityChestShop;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.server.FMLServerHandler;

public class PacketSaveShop implements IMessage {

    private BlockPos pos;
    private int dimension;
    private ItemStack stack;
    private int price;

    public PacketSaveShop() {
    }

    public PacketSaveShop(BlockPos pos, int dimension, ItemStack stack, int price) {
        this.pos = pos;
        this.dimension = dimension;
        this.stack = stack;
        this.price = price;
    }

    public PacketSaveShop(TileEntityChestShop tile, ItemStack stack, int price) {
        this(tile.getPos(), tile.getWorld().provider.getDimension(), stack, price);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.pos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
        this.dimension = buf.readInt();
        this.price = buf.readInt();
        if (buf.readBoolean()) this.stack = ByteBufUtils.readItemStack(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.pos.getX());
        buf.writeInt(this.pos.getY());
        buf.writeInt(this.pos.getZ());
        buf.writeInt(this.dimension);
        buf.writeInt(this.price);
        buf.writeBoolean(this.stack != null);
        if (this.stack != null) ByteBufUtils.writeItemStack(buf, this.stack);
    }

    public static class Handler implements IMessageHandler<PacketSaveShop, IMessage> {

        @SideOnly(Side.SERVER)
        public IMessage onMessage(PacketSaveShop message, MessageContext ctx) {
            FMLServerHandler.instance().getServer().addScheduledTask(() -> {
                EntityPlayerMP player = ctx.getServerHandler().player;
                World world = FMLServerHandler.instance().getServer().getWorld(message.dimension);
                TileEntityChestShop tile = world.getTileEntity(message.pos) != null &&
                        world.getTileEntity(message.pos) instanceof TileEntityChestShop ?
                        (TileEntityChestShop) world.getTileEntity(message.pos) : null;

                if(tile == null) return;

                if(!tile.getOwner().equals(player.getUniqueID())) return;

                tile.setPrice(message.price < 0 || message.stack == null ? 0 : message.price);
                tile.setItem(message.stack);
                tile.sendUpdates();
                player.closeScreen();
                player.sendMessage(new TextComponentString(TextFormatting.GOLD.toString() + TextFormatting.BOLD + "Shop updated!"));
            });
            return null;
        }
    }
}
