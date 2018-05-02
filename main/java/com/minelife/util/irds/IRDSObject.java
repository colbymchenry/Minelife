package com.minelife.util.irds;

public interface IRDSObject {

     public double getProbability();
     public boolean isUnique();
     public boolean dropsAlways();
     public boolean canDrop();

     public void preResultEvaluation();
     public void onHit();
     public void postResultEvaluation();

}
