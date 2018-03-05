package com.minelife.minebay;

import com.minelife.Minelife;
import com.minelife.economy.ModEconomy;
import com.minelife.economy.MoneyHandler;
import com.minelife.minebay.client.gui.MasterGui;
import com.minelife.minebay.packet.PacketPopupMsg;
import com.minelife.util.ItemHelper;
import com.minelife.util.NumberConversions;
import com.minelife.util.PlayerHelper;
import com.minelife.util.client.render.MLItemRenderer;
import com.minelife.util.server.NameFetcher;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class ItemListing extends Listing {

    public static final DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    protected ItemStack item_stack;
    public int stack_size;
    protected Date date_published;

    @SideOnly(Side.CLIENT)
    private MLItemRenderer item_renderer;

    @SideOnly(Side.SERVER)
    public ItemListing(UUID uuid, UUID seller, int price, int stack_size, ItemStack item_stack)
    {
        super(uuid, seller, price, item_stack.getDisplayName(), "");
        this.item_stack = item_stack;
        this.item_stack.stackSize = 1;
        this.stack_size = stack_size;
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 7);
        this.date_published = calendar.getTime();
    }

    @SideOnly(Side.SERVER)
    public ItemListing(UUID uuid) throws SQLException, ParseException
    {
        this.uuid = uuid;
        ResultSet result = Minelife.SQLITE.query("SELECT * FROM item_listings WHERE uuid='" + uuid.toString() + "'");
        if (result.next()) {
            seller = UUID.fromString(result.getString("seller"));
            price = result.getInt("price");
            title = result.getString("title");
            description = NameFetcher.get(seller);
            stack_size = result.getInt("stack_size");
            item_stack = ItemHelper.itemFromString(result.getString("item_stack"));
            item_stack.stackSize = 1;
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
        MLItemRenderer.attempt_gl_reset();
        int xOffset = 5;
        item_renderer.drawItemStack(item_stack, xOffset + 2, (height() - 16) / 2);

        int sectionWidth = (MasterGui.bg_width - (xOffset + 2 + 16)) / 5;
        mc.fontRenderer.drawString(title(),
                xOffset + 24, (height() - mc.fontRenderer.FONT_HEIGHT) / 2, 0xFFFFFF);
        mc.fontRenderer.drawString(description(),
                xOffset + 34 + (sectionWidth), (height() - mc.fontRenderer.FONT_HEIGHT) / 2, 0xFFFFFF);
        mc.fontRenderer.drawString("$" + ModMinebay.format(price()),
                xOffset + 24 + (sectionWidth * 3), (height() - mc.fontRenderer.FONT_HEIGHT) / 2, 0xFFFFFF);
        mc.fontRenderer.drawString("x" + ModMinebay.format(stack_size),
                xOffset + 24 + (sectionWidth * 4) - 10, (height() - mc.fontRenderer.FONT_HEIGHT) / 2, 0xFFFFFF);

        if(mouse_x >= xOffset + 2 && mouse_x <= xOffset + 18 && mouse_y >= (height() - 16) / 2 &&
                mouse_y <= ((height() - 16) / 2) + 16) {
            item_renderer.renderToolTip(item_stack, mouse_x, mouse_y);
            GL11.glDisable(GL11.GL_LIGHTING);
        }
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
            int price = price() * amount;

            if (MoneyHandler.getBalanceVault(player.getUniqueID()) < price) {
                PacketPopupMsg.send("Insufficient Funds. You need to create more cash piles within your estates.", (EntityPlayerMP) player);
                return;
            }

            ItemStack to_give = item_stack().copy();

            for (int i = 0; i < amount / to_give.getMaxStackSize(); i++) {
                ItemStack stack = to_give.copy();
                stack.stackSize = stack.getMaxStackSize();
                EntityItem entity_item = player.dropPlayerItemWithRandomChoice(stack, false);
                entity_item.delayBeforeCanPickup = 0;
            }

            int leftOver = amount % to_give.getMaxStackSize();
            if(leftOver > 0) {
                ItemStack stack = to_give.copy();
                stack.stackSize = leftOver;
                EntityItem entity_item = player.dropPlayerItemWithRandomChoice(stack, false);
                entity_item.delayBeforeCanPickup = 0;
            }

            int leftOverTake = MoneyHandler.takeMoneyVault(player.getUniqueID(), price);
            int leftOverAdd = MoneyHandler.addMoneyVault(seller(), price);

            MoneyHandler.depositATM(player.getUniqueID(), leftOverTake);
            MoneyHandler.depositATM(seller(), leftOverAdd);

            if(leftOverTake > 0)
                player.addChatMessage(new ChatComponentText(EnumChatFormatting.RED + "$" + NumberConversions.formatter.format(leftOverTake) + " was deposited to your checking account because there wasn't enough inventory space."));


            stack_size -= amount;

            if(stack_size > 0) {
                write_to_db();
            } else {
                delete();
            }

            SoldNotification notification = new SoldNotification(seller(), to_give, price);
            if(PlayerHelper.getPlayer(seller()) != null)
                notification.sendTo(PlayerHelper.getPlayer(seller()));
            else
                notification.writeToDB();

            player.closeScreen();
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
        buf.writeInt(price());
        ByteBufUtils.writeUTF8String(buf, uuid().toString());
        ByteBufUtils.writeUTF8String(buf, title() == null ? " " : title());
        ByteBufUtils.writeUTF8String(buf, description() == null ? " " : description());
        ByteBufUtils.writeItemStack(buf, item_stack());
        buf.writeInt(stack_size);
        ByteBufUtils.writeUTF8String(buf, df.format(date_published));
    }

    public static ItemListing from_bytes(ByteBuf buf)
    {
        ItemListing listing = new ItemListing();
        listing.price = buf.readInt();
        listing.uuid = UUID.fromString(ByteBufUtils.readUTF8String(buf));
        listing.title = ByteBufUtils.readUTF8String(buf);
        listing.description = ByteBufUtils.readUTF8String(buf);
        listing.item_stack = ByteBufUtils.readItemStack(buf);
        listing.stack_size = buf.readInt();
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
                        "item_stack='" + ItemHelper.itemToString(item_stack()) + "'," +
                        "damage='" + item_stack().getItemDamage() + "'," +
                        "stack_size='" + stack_size + "'," +
                        "date_published='" + df.format(date_published) + "'" +
                        "WHERE uuid='" + uuid().toString() + "'");
            } else {
                Minelife.SQLITE.query("INSERT INTO item_listings (uuid, seller, price, title, description, item_stack, damage, stack_size, date_published) VALUES (" +
                        "'" + uuid().toString() + "'," +
                        "'" + seller().toString() + "'," +
                        "'" + price() + "'," +
                        "'" + title() + "'," +
                        "'" + description() + "'," +
                        "'" + ItemHelper.itemToString(item_stack()) + "'," +
                        "'" + item_stack().getItemDamage() + "'," +
                        "'" + stack_size + "'," +
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
