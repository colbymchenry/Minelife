package com.minelife.minebay.client;

import com.minelife.notification.AbstractGuiNotification;
import com.minelife.notification.AbstractNotification;
import com.minelife.util.ItemHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ItemSoldNotification extends AbstractNotification {

    public int price;
    public ItemStack item_sold;

    public ItemSoldNotification(int price, ItemStack item_sold) {
        this.price = price;
        this.item_sold = item_sold;
    }

    @Override
    public void writeToNBT(NBTTagCompound tagCompound)
    {
        tagCompound.setInteger("price", price);
        tagCompound.setString("item_sold", ItemHelper.itemToString(item_sold));
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompound)
    {
        price = tagCompound.getInteger("price");
        item_sold = ItemHelper.itemFromString(tagCompound.getString("price"));
    }

    @Override
    public Class<? extends AbstractGuiNotification> getGuiClass()
    {
        return GuiItemSoldNotification.class;
    }

    class GuiItemSoldNotification extends AbstractGuiNotification {

        public GuiItemSoldNotification(AbstractNotification notification)
        {
            super(notification);
        }

        @Override
        protected void drawForeground()
        {

        }

        @Override
        protected void onClick(int mouseX, int mouseY)
        {

        }

        @Override
        protected int getHeight()
        {
            return 0;
        }
    }
}
