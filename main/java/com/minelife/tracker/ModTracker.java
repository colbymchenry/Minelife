package com.minelife.tracker;

import com.minelife.MLMod;
import com.minelife.MLProxy;
import com.minelife.Minelife;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class ModTracker extends MLMod {

    @Override
    public Class<? extends MLProxy> getServerProxyClass() {
        return ServerProxy.class;
    }

    public static int getMonthsPlayed(UUID playerUUID) {
        Calendar startCalendar = new GregorianCalendar();
        startCalendar.setTime(getDateJoined(playerUUID));
        Calendar endCalendar = new GregorianCalendar();
        endCalendar.setTime(Calendar.getInstance().getTime());

        int diffYear = endCalendar.get(Calendar.YEAR) - startCalendar.get(Calendar.YEAR);
        int diffMonth = diffYear * 12 + endCalendar.get(Calendar.MONTH) - startCalendar.get(Calendar.MONTH);
        return diffMonth;
    }

    public static int getDaysPlayed(UUID playerUUID) {
        Calendar startCalendar = new GregorianCalendar();
        startCalendar.setTime(getDateJoined(playerUUID));
        Calendar endCalendar = new GregorianCalendar();
        endCalendar.setTime(Calendar.getInstance().getTime());
        long diff = endCalendar.getTime().getTime() - startCalendar.getTime().getTime();
        return (int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
    }

    public static int getHoursPlayed(UUID playerUUID) {
        Calendar startCalendar = new GregorianCalendar();
        startCalendar.setTime(getDateJoined(playerUUID));
        Calendar endCalendar = new GregorianCalendar();
        endCalendar.setTime(Calendar.getInstance().getTime());
        long diff = endCalendar.getTime().getTime() - startCalendar.getTime().getTime();
        return (int) TimeUnit.HOURS.convert(diff, TimeUnit.MILLISECONDS);
    }

    public static int getMinutesPlayed(UUID playerUUID) {
        Calendar startCalendar = new GregorianCalendar();
        startCalendar.setTime(getDateJoined(playerUUID));
        Calendar endCalendar = new GregorianCalendar();
        endCalendar.setTime(Calendar.getInstance().getTime());
        long diff = endCalendar.getTime().getTime() - startCalendar.getTime().getTime();
        return (int) TimeUnit.MINUTES.convert(diff, TimeUnit.MILLISECONDS);
    }

    public static Date getDateJoined(UUID playerUUID) {
        try {
            ResultSet result = Minelife.SQLITE.query("SELECT * FROM tracker WHERE playerUUID='" +  playerUUID.toString() + "'");
            if(result.next()) return ServerProxy.df.parse(result.getString("dateJoined"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


}
