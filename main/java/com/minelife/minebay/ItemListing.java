package com.minelife.minebay;

import com.minelife.util.ItemUtil;
import com.minelife.util.NumberConversions;
import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

import java.util.UUID;


public class ItemListing extends Listing {

    private ItemStack item_stack;

    public ItemListing(UUID uuid, UUID seller, long price, String title, String description, ItemStack item_stack)
    {
        super(uuid, seller, price, title, description);
        this.item_stack = item_stack;
    }

    private ItemListing()
    {
    }

    @Override
    public int height()
    {
        return 30;
    }

    @Override
    public void draw(int mouse_x, int mouse_y)
    {

    }

    public void draw(Minecraft mc, int mouse_x, int mouse_y, int width, int height)
    {
        int xOffset = 5;
        ItemUtil.Client.renderItem(item_stack, xOffset + 2, (height() - 16) / 2, width, height, mouse_x, mouse_y);
        mc.fontRenderer.setUnicodeFlag(true);
        mc.fontRenderer.drawString(title(), xOffset + 24, 2, 0xFFFFFF);
        mc.fontRenderer.drawString(description(), xOffset + 24, mc.fontRenderer.FONT_HEIGHT + 1, 0xFFFFFF);
        mc.fontRenderer.drawString("$" + NumberConversions.formatter.format(price()), xOffset + 24, mc.fontRenderer.FONT_HEIGHT * 2 + 1, 0xFFFFFF);
        mc.fontRenderer.setUnicodeFlag(false);
    }

    @Override
    public void mouse_clicked(int mouse_x, int mouse_y, boolean double_click)
    {

    }

    public ItemStack item_stack()
    {
        return item_stack;
    }

    public void to_bytes(ByteBuf buf)
    {
        buf.writeLong(price());
        ByteBufUtils.writeUTF8String(buf, uuid().toString());
        ByteBufUtils.writeUTF8String(buf, title());
        ByteBufUtils.writeUTF8String(buf, description());
        ByteBufUtils.writeItemStack(buf, item_stack());
    }

    public static ItemListing from_bytes(ByteBuf buf)
    {
        ItemListing listing = new ItemListing();
        listing.price = buf.readLong();
        listing.uuid = UUID.fromString(ByteBufUtils.readUTF8String(buf));
        listing.title = ByteBufUtils.readUTF8String(buf);
        listing.description = ByteBufUtils.readUTF8String(buf);
        listing.item_stack = ItemUtil.itemFromString(ByteBufUtils.readUTF8String(buf));
        return listing;
    }

}
