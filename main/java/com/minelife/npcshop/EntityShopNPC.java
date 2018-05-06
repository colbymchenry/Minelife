package com.minelife.npcshop;

import com.google.common.collect.Maps;
import com.minelife.realestate.EntityReceptionist;
import com.minelife.util.ItemHelper;
import com.minelife.util.MLInventory;
import net.minecraft.entity.EntityCreature;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.world.World;

import java.util.Map;

public class EntityShopNPC extends EntityCreature {

    private static final DataParameter<Integer> DATA_SKIN = EntityDataManager.createKey(EntityReceptionist.class, DataSerializers.VARINT);
    private static final DataParameter<NBTTagCompound> DATA_TRADES = EntityDataManager.createKey(EntityReceptionist.class, DataSerializers.COMPOUND_TAG);
    private static final DataParameter<NBTTagCompound> DATA_INVENTORY = EntityDataManager.createKey(EntityReceptionist.class, DataSerializers.COMPOUND_TAG);
    private MLInventory inventory = new MLInventory(54, null, 64);

    public EntityShopNPC(World worldIn) {
        super(worldIn);
    }

    public void setSkin(int skin) {
        this.getDataManager().set(DATA_SKIN, skin);
    }

    public int getSkin() {
        return this.getDataManager().get(DATA_SKIN);
    }

    public void setInventory(Map<Integer, ItemStack> itemStacks) {
        inventory.clear();
        itemStacks.forEach((slot, item) -> inventory.setInventorySlotContents(slot, item));
        NBTTagCompound tag = new NBTTagCompound();
        inventory.writeToNBT(tag);
        this.getDataManager().set(DATA_INVENTORY, tag);
    }

    public MLInventory getInventory() {
        NBTTagCompound tagCompound = this.getDataManager().get(DATA_INVENTORY);
        inventory.clear();
        inventory.readFromNBT(tagCompound);
        return inventory;
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tagCompound) {
        super.readEntityFromNBT(tagCompound);
        this.setSkin(tagCompound.getInteger("Skin"));
        if(tagCompound.hasKey("Inventory")) {
            Map<Integer, ItemStack> itemStacks = Maps.newHashMap();
            for (String s : tagCompound.getString("Inventory").split(";")) {
                if(s.contains(",")) {
                    int slot = Integer.parseInt(s.split(",")[0]);
                    ItemStack stack = ItemHelper.itemFromString(s);
                    itemStacks.put(slot, stack);
                }
            }
            this.setInventory(itemStacks);
        }
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tagCompound) {
        super.writeEntityToNBT(tagCompound);
        tagCompound.setInteger("Skin", this.getSkin());
    }

}
