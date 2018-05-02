package com.minelife.util.irds;

import com.minelife.util.irds.IRDSValue;

public class RDSNullValue implements IRDSValue {

    private double probability;

    public RDSNullValue(double probability) {
        this.probability = probability;
    }

    @Override
    public Object getValue() {
        return null;
    }

    @Override
    public double getProbability() {
        return probability;
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
        return true;
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
