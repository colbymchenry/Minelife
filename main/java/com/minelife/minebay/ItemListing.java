package com.minelife.minebay;

import com.minelife.Minelife;
import com.minelife.economy.ModEconomy;
import com.minelife.minebay.packet.PacketPopupMsg;
import com.minelife.util.ItemUtil;
import com.minelife.util.NumberConversions;
import com.minelife.util.client.render.MLItemRenderer;
import com.minelife.util.server.NameFetcher;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;


public class ItemListing extends Listing {

    protected ItemStack item_stack;

    @SideOnly(Side.CLIENT)
    private MLItemRenderer item_renderer;

    @SideOnly(Side.SERVER)
    public ItemListing(UUID uuid, UUID seller, long price, ItemStack item_stack)
    {
        super(uuid, seller, price, item_stack.getDisplayName(), "");
        this.item_stack = item_stack;
    }

    @SideOnly(Side.SERVER)
    public ItemListing(UUID uuid) throws SQLException
    {
        this.uuid = uuid;
        ResultSet result = Minelife.SQLITE.query("SELECT * FROM item_listings WHERE uuid='" + uuid.toString() + "'");
        if (result.next()) {
            seller = UUID.fromString(result.getString("seller"));
            price = result.getLong("price");
            title = result.getString("title");
            description = NameFetcher.get(seller);
            item_stack = ItemUtil.itemFromString(result.getString("item_stack"));
        } else {
            throw new SQLException("ItemListing not found by that uuid.");
        }
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

    @SideOnly(Side.CLIENT)
    public void draw(Minecraft mc, int mouse_x, int mouse_y, int width, int height)
    {
        if (item_renderer == null) item_renderer = new MLItemRenderer(mc);
        item_renderer.attempt_gl_reset();
        int xOffset = 5;
        item_renderer.drawItemStack(item_stack, xOffset + 2, (height() - 16) / 2, null);
//        item_renderer.renderToolTip(item_stack, mouse_x, mouse_y, width, height);
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

    @Override
    public void finalize(EntityPlayer player)
    {
        try {
            if (ModEconomy.getBalance(player.getUniqueID(), false) < price())
                PacketPopupMsg.send("Insufficient Funds in Wallet", (EntityPlayerMP) player);


        } catch (Exception e) {
            e.printStackTrace();
            Minelife.handle_exception(e, player);
        }
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
        listing.item_stack = ByteBufUtils.readItemStack(buf);
        return listing;
    }

    @SideOnly(Side.SERVER)
    public void write_to_db()
    {
        try {
            Minelife.SQLITE.query("INSERT INTO item_listings (uuid, seller, price, title, description, item_stack) VALUES (" +
                    "'" + uuid().toString() + "'," +
                    "'" + seller().toString() + "'," +
                    "'" + price() + "'," +
                    "'" + title() + "'," +
                    "'" + description() + "'," +
                    "'" + ItemUtil.itemToString(item_stack()) + "')");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
