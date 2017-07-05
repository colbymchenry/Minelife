package com.minelife.util;

import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;

public class NBTUtil {

    public static NBTTagCompound fromString(String str) {
        try {
            return (NBTTagCompound) JsonToNBT.func_150315_a(str);
        } catch (NBTException e) {
            e.printStackTrace();
        }

        return null;
    }

}
