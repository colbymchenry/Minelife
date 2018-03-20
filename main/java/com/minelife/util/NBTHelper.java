package com.minelife.util;

import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;

public class NBTHelper {

    public static NBTTagCompound fromString(String str) {
        try {
            return JsonToNBT.getTagFromJson(str);
        } catch (NBTException e) {
            e.printStackTrace();
        }

        return null;
    }

}
