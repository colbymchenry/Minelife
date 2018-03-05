package com.minelife.minebay;

import com.google.common.base.Objects;
import com.minelife.MLMod;
import com.minelife.MLProxy;
import com.minelife.minebay.packet.*;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.item.ItemStack;

import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

public class ModMinebay extends MLMod {

    @Override
    public void preInit(FMLPreInitializationEvent event)
    {
        registerPacket(PacketListings.Handler.class, PacketListings.class, Side.SERVER);
        registerPacket(PacketResponseListings.Handler.class, PacketResponseListings.class, Side.CLIENT);
        registerPacket(PacketSellItem.Handler.class, PacketSellItem.class, Side.SERVER);
        registerPacket(PacketPopupMsg.Handler.class, PacketPopupMsg.class, Side.CLIENT);
        registerPacket(PacketBuyItem.Handler.class, PacketBuyItem.class, Side.SERVER);
    }

    @Override
    public Class<? extends MLProxy> getClientProxyClass()
    {
        return com.minelife.minebay.client.ClientProxy.class;
    }

    @Override
    public Class<? extends MLProxy> getServerProxyClass()
    {
        return com.minelife.minebay.server.ServerProxy.class;
    }

    public static boolean areStacksIdentical(ItemStack stack1, ItemStack stack2) {
        if (stack1 != null && stack2 != null) {
            return stack1.getItem() == stack2.getItem() && stack1.getItemDamage() == stack2.getItemDamage() && Objects.equal(stack1.getTagCompound(), stack2.getTagCompound());
        } else {
            return stack1 == stack2;
        }
    }

    private static final NavigableMap<Long, String> suffixes = new TreeMap<>();
    static {
        suffixes.put(1_000L, "k");
        suffixes.put(1_000_000L, "M");
        suffixes.put(1_000_000_000L, "G");
        suffixes.put(1_000_000_000_000L, "T");
        suffixes.put(1_000_000_000_000_000L, "P");
        suffixes.put(1_000_000_000_000_000_000L, "E");
    }

    public static String format(long value) {
        //Long.MIN_VALUE == -Long.MIN_VALUE so we need an adjustment here
        if (value == Long.MIN_VALUE) return format(Long.MIN_VALUE + 1);
        if (value < 0) return "-" + format(-value);
        if (value < 1000) return Long.toString(value); //deal with easy case

        Map.Entry<Long, String> e = suffixes.floorEntry(value);
        Long divideBy = e.getKey();
        String suffix = e.getValue();

        long truncated = value / (divideBy / 10); //the number part of the output times 10
        boolean hasDecimal = truncated < 100 && (truncated / 10d) != (truncated / 10);
        return hasDecimal ? (truncated / 10d) + suffix : (truncated / 10) + suffix;
    }

}
