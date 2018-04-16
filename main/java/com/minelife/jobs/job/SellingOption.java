package com.minelife.jobs.job;

import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;

public class SellingOption {

    private ItemStack stack;
    private int price;

    public SellingOption(ItemStack stack, int price) {
        this.stack = stack;
        this.price = price;
    }

    public ItemStack getStack() {
        return stack;
    }

    public int getPrice() {
        return price;
    }

    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeItemStack(buf, stack);
        buf.writeInt(price);
    }

    public static SellingOption fromBytes(ByteBuf buf) {
        ItemStack stack = ByteBufUtils.readItemStack(buf);
        int price = buf.readInt();
        return new SellingOption(stack, price);
    }
}
