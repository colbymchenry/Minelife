package com.minelife.minebay;

import com.minelife.util.DateHelper;
import com.minelife.util.NBTHelper;
import com.minelife.util.NumberConversions;
import com.minelife.util.client.GuiFakeInventory;
import com.minelife.util.client.GuiHelper;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.ByteBufUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

public class ItemListing extends Listing {

    private Date datePublished;
    private ItemStack stack;
    private int storage;

    public ItemListing(UUID uuid, UUID seller, int price, String title, String description, Date datePublished, ItemStack stack, int storage) {
        super(uuid, seller, price, title, description);
        this.datePublished = datePublished;
        this.stack = stack;
        this.storage = storage;
    }

    public ItemListing(ResultSet result) throws SQLException {
        super(UUID.fromString(result.getString("uuid")), UUID.fromString(result.getString("seller")), result.getInt("price"), result.getString("title"), result.getString("description"));
        this.datePublished = DateHelper.stringToDate(result.getString("datePublished"));
        this.stack = new ItemStack(Objects.requireNonNull(NBTHelper.fromString(result.getString("item"))));
        this.storage = result.getInt("storage");
    }

    @Override
    public int getHeight() {
        return 20;
    }

    @Override
    public void draw(int mouse_x, int mouse_y) {
        GlStateManager.color(1, 1, 1, 1);
        GuiHelper.renderItem(stack, ItemCameraTransforms.TransformType.GUI);
        getFontRenderer().drawStringWithShadow("$" + NumberConversions.format(this.getPrice()), 20, 10, 0xFFFFFF);
    }

    @Override
    public void mouseClicked(int mouse_x, int mouse_y, boolean double_click) {

    }

    @Override
    public void finalize(EntityPlayer player, Object... objects) {

    }

    public ItemStack getItemStack() {
        return stack;
    }

    public int getAmountStored() {
        return storage;
    }

    public void setAmountStored(int amount) {
        this.storage = amount;
    }

    public void toBytes(ByteBuf buf) {
        buf.writeInt(getPrice());
        ByteBufUtils.writeUTF8String(buf,getUniqueID().toString());
        ByteBufUtils.writeUTF8String(buf,getSellerID().toString());
        ByteBufUtils.writeUTF8String(buf, getTitle() == null ? " " : getTitle());
        ByteBufUtils.writeUTF8String(buf, getDescription() == null ? " " : getDescription());
        ByteBufUtils.writeItemStack(buf, getItemStack());
        ByteBufUtils.writeUTF8String(buf, DateHelper.dateToString(datePublished));
        buf.writeInt(this.getAmountStored());
    }

    public static ItemListing fromBytes(ByteBuf buf) {
        int price = buf.readInt();
        UUID uniqueID = UUID.fromString(ByteBufUtils.readUTF8String(buf));
        UUID sellerID = UUID.fromString(ByteBufUtils.readUTF8String(buf));
        String title = ByteBufUtils.readUTF8String(buf);
        String description = ByteBufUtils.readUTF8String(buf);
        ItemStack itemStack = ByteBufUtils.readItemStack(buf);
        Date datePublished = DateHelper.stringToDate(ByteBufUtils.readUTF8String(buf));
        int storage = buf.readInt();
        return new ItemListing(uniqueID, sellerID, price, title, description, datePublished, itemStack, storage);
    }

}
