package com.minelife.resourcefulness.forest;

import com.minelife.util.NumberConversions;
import com.minelife.util.irds.IRDSObject;
import com.minelife.util.irds.IRDSValue;
import com.sk89q.worldedit.util.TreeGenerator;

public class ForestTree implements IRDSObject, IRDSValue<TreeGenerator.TreeType> {

    private TreeGenerator.TreeType treeType;
    private double probability;
    private boolean unique, always, enabled;

    public ForestTree(TreeGenerator.TreeType treeType, double probability, boolean unique, boolean always, boolean enabled) {
        this.treeType = treeType;
        this.probability = probability;
        this.unique = unique;
        this.always = always;
        this.enabled = enabled;
    }

    @Override
    public double getProbability() {
        return probability;
    }

    @Override
    public boolean isUnique() {
        return unique;
    }

    @Override
    public boolean dropsAlways() {
        return always;
    }

    @Override
    public boolean canDrop() {
        return enabled;
    }

    @Override
    public void preResultEvaluation() {

    }

    @Override
    public void onHit() {

    }

    @Override
    public void postResultEvaluation() {

    }

    @Override
    public TreeGenerator.TreeType getValue() {
        return treeType;
    }

    @Override
    public String toString() {
        return probability + ";" + unique + ";" + always + ";" + enabled + ";" + treeType.name();
    }

    public static ForestTree fromString(String str) {
        if(!str.contains(";")) return null;
        String[] data = str.split(";");
        if(data.length != 5) return null;
        double probability = NumberConversions.toDouble(data[0]);
        boolean unique = Boolean.valueOf(data[1]);
        boolean always = Boolean.valueOf(data[2]);
        boolean enabled = Boolean.valueOf(data[3]);
        TreeGenerator.TreeType treeType = TreeGenerator.TreeType.valueOf(data[4]);
        return new ForestTree(treeType, probability, unique, always, enabled);
    }
}