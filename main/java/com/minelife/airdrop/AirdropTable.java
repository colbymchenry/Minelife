package com.minelife.airdrop;

import com.minelife.util.irds.IRDSObject;
import com.minelife.util.irds.IRDSTable;

import java.util.List;

public class AirdropTable extends IRDSTable {

    @Override
    public int getCount() {
        return 14;
    }

    @Override
    public List<IRDSObject> getContents() {
        return ((List<IRDSObject>) (List<?>) ModAirdrop.getLootList());
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
