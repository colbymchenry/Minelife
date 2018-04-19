package com.minelife.gangs;

import com.minelife.MLMod;
import com.minelife.gangs.server.ServerProxy;
import lib.PatPeter.SQLibrary.Database;

public class ModGangs extends MLMod {

// TODO: Add rep, 5 ranks, pvp battles for betting, etc.

    public static Database getDatabase() {
        return ServerProxy.DB;
    }

}
