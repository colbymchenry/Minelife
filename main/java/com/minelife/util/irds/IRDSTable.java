package com.minelife.util.irds;

import com.google.common.collect.Lists;
import net.minecraft.util.math.MathHelper;

import java.util.List;
import java.util.Random;

public abstract class IRDSTable implements IRDSObject {

    protected static Random random = new Random();

    protected List<IRDSObject> uniquedrops = Lists.newArrayList();

    public abstract int getCount();

    public abstract List<IRDSObject> getContents();

    public abstract List<IRDSObject> getResult();

    public void addToResult(List<IRDSObject> resultList, IRDSObject irdsObject) {
        if(!irdsObject.isUnique() || !uniquedrops.contains(irdsObject)) {
            if(irdsObject.isUnique()) uniquedrops.add(irdsObject);
            if(!(irdsObject instanceof RDSNullValue)) {
                if(irdsObject instanceof IRDSTable) {
                    resultList.addAll(((IRDSTable) irdsObject).getResult());
                } else {
                    IRDSObject adder = irdsObject;
                    if(irdsObject instanceof IRDSObjectCreator)
                        adder = ((IRDSObjectCreator) irdsObject).createInstance();
                    resultList.add(adder);
                    irdsObject.onHit();
                }
            } else {
                irdsObject.onHit();
            }
        }
    }

    protected List<IRDSObject> getResultDefault() {
        List<IRDSObject> resultList = Lists.newArrayList();
        uniquedrops.clear();

        getContents().forEach(irdsObject -> irdsObject.preResultEvaluation());

        getContents().forEach(irdsObject -> {
            if(irdsObject.dropsAlways() && irdsObject.canDrop())
                addToResult(resultList, irdsObject);
        });


        int alwaysCount = getContents().stream().mapToInt(irdsObject -> (irdsObject.dropsAlways() && irdsObject.canDrop()) ? 1 : 0).sum();
        int realDropCount = getCount() - alwaysCount;

        if(realDropCount < 0) return resultList;

        for(int dropcount = 0; dropcount < realDropCount; dropcount++) {
            List<IRDSObject> dropables = Lists.newArrayList();

            getContents().forEach(irdsObject -> {
                if (irdsObject.canDrop() && !irdsObject.dropsAlways()) dropables.add(irdsObject);
            });

            double hitValue = MathHelper.nextDouble(random, 0.0, dropables.stream().mapToDouble(dropable -> dropable.getProbability()).sum());
            double runningValue = 0;

            for (IRDSObject dropable : dropables) {
                runningValue += dropable.getProbability();

                if(hitValue < runningValue) {
                    addToResult(resultList, dropable);
                    break;
                }
            }
        }

        resultList.forEach(drop -> drop.postResultEvaluation());
        return resultList;
    }

}
