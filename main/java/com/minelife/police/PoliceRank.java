package com.minelife.police;

import net.minecraft.entity.player.EntityPlayer;

public enum PoliceRank {

    CADET(1),
    OFFICER(1),
    MASTER_OFFICER(1),
    CORPORAL(1),
    SERGEANT(1),
    STAFF_SERGEANT(1),
    INSPECTOR(1),
    LIEUTENANT(1),
    CAPTAIN(1),
    MAJOR(2),
    COMMANDER(2),
    LIEUTENANT_COLONEL(2),
    COLONEL(2),
    DEPUTY(3),
    CHIEF(4);

    public int unlawful_arrests;

    PoliceRank(int unlawful_arrests) {
        this.unlawful_arrests = unlawful_arrests;
    }

    public static PoliceRank getRank(EntityPlayer player) {
        int level = XPHandler.getLevel(player.getUniqueID());
        for (PoliceRank rank : PoliceRank.values()) {
            if(rank.ordinal() == level) return rank;
        }

        return null;
    }

    public int getPayment() {
        return ordinal() * 1000;
    }

    public boolean canHireCadets() {
        return ordinal() >= CORPORAL.ordinal();
    }

}
