package com.minelife.realestate.util;

import net.minecraft.util.AxisAlignedBB;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Util {

    public static AxisAlignedBB boundingBoxDeserialize(String serialized) {
        List<Double> vals = Arrays.asList(serialized.replace("box[", "").replaceAll("]", "").split(",")).stream().map(val -> Double.parseDouble(val.trim())).collect(Collectors.toList());
        if (vals.size() == 6) return AxisAlignedBB.getBoundingBox(vals.get(0), vals.get(1), vals.get(2), vals.get(3), vals.get(4), vals.get(5));
        else return null;
    }

}
