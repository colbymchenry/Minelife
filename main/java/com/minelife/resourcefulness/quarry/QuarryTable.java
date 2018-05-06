package com.minelife.resourcefulness.quarry;

import com.minelife.util.irds.IRDSObject;
import com.minelife.util.irds.IRDSTable;

import java.util.List;

public class QuarryTable extends IRDSTable {

    private Quarry quarry;

    public QuarryTable(Quarry quarry) {
        this.quarry = quarry;
    }

    @Override
    public int getCount() {
        int i = 0;
        for (int x = quarry.getMin().getX(); x < quarry.getMax().getX(); x++) {
            for (int y = quarry.getMin().getY(); y < quarry.getMax().getY(); y++) {
                for (int z = quarry.getMin().getZ(); z < quarry.getMax().getZ(); z++) {
                    i++;
                }
            }
        }
        return i;
    }

    @Override
    public List<IRDSObject> getContents() {
        return ((List<IRDSObject>) (List<?>) quarry.getOres());
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