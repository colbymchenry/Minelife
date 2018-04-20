package com.minelife.tdm;

import com.google.common.collect.Maps;
import com.minelife.Minelife;
import com.minelife.util.ItemHelper;
import com.minelife.util.MLConfig;
import com.minelife.util.configuration.InvalidConfigurationException;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

public class SavedInventory {

    private UUID uniqueID;
    private Map<Integer, ItemStack> items;
    private MLConfig config;
    private File file;

    public SavedInventory(UUID uniqueID) throws IOException, InvalidConfigurationException {
        file = new File(Minelife.getDirectory(), "saved_inventories/" + uniqueID.toString() + ".yml");
        config = new MLConfig(new File(Minelife.getDirectory(), "saved_inventories"), uniqueID.toString());
        items = Maps.newHashMap();
        this.uniqueID = uniqueID;
        if (file.exists()) {
            for(int i = 0; i < 200; i++) {
                if(config.contains("" + i)) items.put(i, ItemHelper.itemFromString(config.getString("" + i)));
            }
        }
    }

    public static boolean hasSavedInventory(UUID uniqueID) {
        return new File(Minelife.getDirectory(), "saved_inventories/" + uniqueID.toString() + ".yml").exists();
    }

    public Map<Integer, ItemStack> getItems() {
        return items;
    }

    public void setItems(Map<Integer, ItemStack> items) throws IOException, InvalidConfigurationException {
        this.items = items;
        this.file.delete();
        config = new MLConfig(new File(Minelife.getDirectory(), "saved_inventories"), uniqueID.toString());
        items.forEach((slot, stack) -> {
            if(stack != null && stack != ItemStack.EMPTY) {
                config.set("" + slot, ItemHelper.itemToString(stack));
            }
        });
        config.save();
    }

    public void setItems(IInventory inventory) throws IOException, InvalidConfigurationException {
        this.items.clear();
        this.file.delete();
        config = new MLConfig(new File(Minelife.getDirectory(), "saved_inventories"), uniqueID.toString());
        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            if(inventory.getStackInSlot(i) != null && inventory.getStackInSlot(i) != ItemStack.EMPTY) {
                this.items.put(i, inventory.getStackInSlot(i));
                config.set("" + i, ItemHelper.itemToString(inventory.getStackInSlot(i)));
            }
        }
        config.save();
    }

    public void delete() {
        file.delete();
    }

}
