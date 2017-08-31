package com.minelife.police;

import com.minelife.Minelife;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class XPHandler {

    public static int getXP(UUID playerUUID) {
        try {
            ResultSet result = Minelife.SQLITE.query("SELECT * FROM policeofficers WHERE playerUUID='" + playerUUID.toString() + "'");
            if(result.next()) return result.getInt("xp");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public static int getLevel(int xp) {
        return (int) Math.floor(0.1D * Math.sqrt(xp));
    }

    public static int getLevel(UUID playerUUID) {
        return getLevel(getXP(playerUUID));
    }

}
