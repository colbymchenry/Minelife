package com.minelife.resourcefulness.forest;

import com.minelife.util.irds.IRDSObject;
import com.minelife.util.irds.IRDSTable;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public class ForestTable extends IRDSTable {

    private Forest forest;

    public ForestTable(Forest forest) {
        this.forest = forest;
    }

    @Override
    public int getCount() {
        int i = 0;
        for (int x = forest.getMin().getX(); x < forest.getMax().getX(); x++) {
            for (int y = forest.getMin().getY(); y < forest.getMin().getY() + 1; y++) {
                for (int z = forest.getMin().getZ(); z < forest.getMax().getZ(); z++) {
                    i++;
                }
            }
        }
        return i;
    }

    @Override
    public List<IRDSObject> getContents() {
        return ((List<IRDSObject>) (List<?>) forest.getTrees());
    }

    @Override
    public List<IRDSObject> getResult() {
        return this.getResultDefault();
    }

    @Override
    public double getProbability() {
        return 0;
    }

    @Override
    public boolean isUnique() {
        return false;
    }

    @Override
    public boolean dropsAlways() {
        return false;
    }

    @Override
    public boolean canDrop() {
        return false;
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
}