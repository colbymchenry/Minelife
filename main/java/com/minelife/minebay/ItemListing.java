package com.minelife.minebay;

import com.minelife.Minelife;
import com.minelife.util.ItemUtil;
import com.minelife.util.NumberConversions;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;


public class ItemListing extends Listing {

    protected ItemStack item_stack;

    public ItemListing(UUID uuid, UUID seller, long price, ItemStack item_stack)
    {
        super(uuid, seller, price, "", "");
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

    @SideOnly(Side.SERVER)
    public void write_to_db() {
        try {
            Minelife.SQLITE.query("INSERT INTO item_listings (uuid, seller, price, title, description, item_stack, item_display_name, item_unlocalized_name) VALUES (" +
                    "'" + uuid().toString() + "'," +
                    "'" + seller().toString() + "'," +
                    "'" + price() + "'," +
                    "'" + title() + "'," +
                    "'" + description() + "'," +
                    "'" + ItemUtil.itemToString(item_stack()) + "'," +
                    "'" + item_stack().getDisplayName() + "'," +
                    "'" + item_stack().getUnlocalizedName() + "')");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @SideOnly(Side.SERVER)
    public static ItemListing from_db(ResultSet result) {
        try {
            if(result.next()) {
                ItemListing listing = new ItemListing();
                listing.uuid = UUID.fromString(result.getString("uuid"));
                listing.seller = UUID.fromString(result.getString("seller"));
                listing.price = result.getLong("price");
                listing.title = result.getString("title");
                listing.description = result.getString("description");
                listing.item_stack = ItemUtil.itemFromString(result.getString("item_stack"));
                return listing;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
