package com.minelife.minebay;

import com.minelife.Minelife;
import com.minelife.economy.ModEconomy;
import com.minelife.minebay.packet.PacketPopupMsg;
import com.minelife.util.ItemUtil;
import com.minelife.util.NumberConversions;
import com.minelife.util.client.render.MLItemRenderer;
import com.minelife.util.server.Callback;
import com.minelife.util.server.NameFetcher;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;


public class ItemListing extends Listing {

    private static final DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    protected ItemStack item_stack;
    protected Date date_published;

    @SideOnly(Side.CLIENT)
    private MLItemRenderer item_renderer;

    @SideOnly(Side.SERVER)
    public ItemListing(UUID uuid, UUID seller, long price, ItemStack item_stack)
    {
        super(uuid, seller, price, item_stack.getDisplayName(), "");
        this.item_stack = item_stack;
        this.date_published = Calendar.getInstance().getTime();
    }

    @SideOnly(Side.SERVER)
    public ItemListing(UUID uuid) throws SQLException, ParseException
    {
        this.uuid = uuid;
        ResultSet result = Minelife.SQLITE.query("SELECT * FROM item_listings WHERE uuid='" + uuid.toString() + "'");
        if (result.next()) {
            seller = UUID.fromString(result.getString("seller"));
            price = result.getLong("price");
            title = result.getString("title");
            description = NameFetcher.get(seller);
            item_stack = ItemUtil.itemFromString(result.getString("item_stack"));
            date_published = df.parse(result.getString("date_published"));
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
        item_renderer.drawItemStack(item_stack, xOffset + 2, (height() - 16) / 2);
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
    public void finalize(EntityPlayer player, Object... objects)
    {
        try {
            int amount = (int) objects[0];
            long price = price() <= 0 ? 0 : amount <= 0 ? 0 : price() / amount;

            if (ModEconomy.getBalance(player.getUniqueID(), false) < price) {
                PacketPopupMsg.send("Insufficient Funds in Bank Account", (EntityPlayerMP) player);
                return;
            }

            ItemStack to_give = item_stack().copy();
            to_give.stackSize = amount;
            item_stack.stackSize -= amount;
            EntityItem entity_item = player.dropPlayerItemWithRandomChoice(to_give, false);
            entity_item.delayBeforeCanPickup = 0;

            ModEconomy.withdraw(player.getUniqueID(), price, false);
            ModEconomy.deposit(seller(), price, false);

            if(item_stack.stackSize > 0) {
                write_to_db();
            } else {
                delete();
            }
            // TODO: Notification for seller
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
        ByteBufUtils.writeUTF8String(buf, title() == null ? " " : title());
        ByteBufUtils.writeUTF8String(buf, description() == null ? " " : description());
        ByteBufUtils.writeItemStack(buf, item_stack());
        ByteBufUtils.writeUTF8String(buf, df.format(date_published));
    }

    public static ItemListing from_bytes(ByteBuf buf)
    {
        ItemListing listing = new ItemListing();
        listing.price = buf.readLong();
        listing.uuid = UUID.fromString(ByteBufUtils.readUTF8String(buf));
        listing.title = ByteBufUtils.readUTF8String(buf);
        listing.description = ByteBufUtils.readUTF8String(buf);
        listing.item_stack = ByteBufUtils.readItemStack(buf);
        try {
            listing.date_published = df.parse(ByteBufUtils.readUTF8String(buf));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return listing;
    }

    @SideOnly(Side.SERVER)
    public void write_to_db()
    {
        try {

            ResultSet result = Minelife.SQLITE.query("SELECT * FROM item_listings WHERE uuid='" + uuid().toString() + "'");

            if(result.next()) {
                Minelife.SQLITE.query("UPDATE item_listings SET " +
                        "seller='" + seller().toString() + "'," +
                        "price='" + price() + "'," +
                        "title='" + title() + "'," +
                        "description='" + description() + "'," +
                        "item_stack='" + ItemUtil.itemToString(item_stack()) + "'," +
                        "damage='" + item_stack().getItemDamage() + "'," +
                        "stack_size='" + item_stack().stackSize + "'," +
                        "date_published='" + df.format(date_published) + "'" +
                        "WHERE uuid='" + uuid().toString() + "'");
            } else {
                Minelife.SQLITE.query("INSERT INTO item_listings (uuid, seller, price, title, description, item_stack, damage, stack_size, date_published) VALUES (" +
                        "'" + uuid().toString() + "'," +
                        "'" + seller().toString() + "'," +
                        "'" + price() + "'," +
                        "'" + title() + "'," +
                        "'" + description() + "'," +
                        "'" + ItemUtil.itemToString(item_stack()) + "'," +
                        "'" + item_stack().getItemDamage() + "'," +
                        "'" + item_stack().stackSize + "'," +
                        "'" + df.format(date_published) + "')");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete() {
        try {
            Minelife.SQLITE.query("DELETE FROM item_listings WHERE uuid='" + uuid().toString() + "'");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
