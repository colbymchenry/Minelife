package com.minelife.realestate.server;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.minelife.MLProxy;
import com.minelife.Minelife;
import com.minelife.economy.Bill;
import com.minelife.economy.BillEvent;
import com.minelife.economy.ModEconomy;
import com.minelife.notifications.Notification;
import com.minelife.notifications.NotificationType;
import com.minelife.realestate.Estate;
import com.minelife.realestate.ModRealEstate;
import com.minelife.util.*;
import com.minelife.util.server.UUIDFetcher;
import lib.PatPeter.SQLibrary.Database;
import lib.PatPeter.SQLibrary.SQLite;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Logger;

public class ServerProxy extends MLProxy {

    private static Map<EntityPlayerMP, Long> billsDue = Maps.newHashMap();

    public static MLConfig CONFIG;
    public static Set<Estate> ESTATES = Sets.newTreeSet();
    public static Database DB;

    @Override
    public void preInit(FMLPreInitializationEvent event) throws Exception {
        DB = new SQLite(Logger.getLogger("Minecraft"), "[RealEstate]", Minelife.getDirectory().getAbsolutePath(), "realestate");
        DB.open();
        DB.query("CREATE TABLE IF NOT EXISTS estates (uuid VARCHAR(36), tagCompound TEXT)");
        loadEstates();

        CONFIG = new MLConfig("realestate");

        Blocks.IRON_BLOCK.setResistance(55);
        Blocks.DIAMOND_BLOCK.setResistance(100);
        Blocks.OBSIDIAN.setResistance(80);
        Blocks.STONEBRICK.setResistance(40);

        MinecraftForge.EVENT_BUS.register(new SelectionListener());
        MinecraftForge.EVENT_BUS.register(new EstateListener());
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void loadEstates() throws SQLException {
        ESTATES.clear();
        ResultSet result = DB.query("SELECT * FROM estates");
        while (result.next())
            ESTATES.add(new Estate(UUID.fromString(result.getString("uuid")), NBTHelper.fromString(result.getString("tagCompound"))));
    }

    @SubscribeEvent
    public void onBillPay(BillEvent.PayEvent event) {
        if (!event.getBill().getTagCompound().hasKey("EstateID")) return;

        Estate estate = ModRealEstate.getEstate(UUID.fromString(event.getBill().getTagCompound().getString("EstateID")));

        ModEconomy.withdrawATM(event.getPlayer().getUniqueID(), event.getAmount());
        int didNotFit = ModEconomy.depositCashPiles(estate.getOwnerID(), event.getAmount());
        ModEconomy.depositATM(estate.getOwnerID(), didNotFit, true);

        if (event.getBill().getDueDate().before(Calendar.getInstance().getTime()) && event.getAmount() >= event.getBill().getAmountDue()) {
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MINUTE, estate.getRentPeriod() * 20);
            event.getBill().setDueDate(cal.getTime());
        }
        event.getBill().setAmountDue(event.getBill().getAmountDue() - event.getAmount());
        try {
            event.getBill().save();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Notification billPayNotification = new Notification(estate.getOwnerID(), TextFormatting.DARK_GRAY + "Rent income: " + TextFormatting.DARK_GREEN + "$" + NumberConversions.format(event.getAmount()), new ResourceLocation(Minelife.MOD_ID, "textures/gui/notification/house-icon.png"), NotificationType.EDGED, 5, 0xFFFFFF);
        if (PlayerHelper.getPlayer(estate.getOwnerID()) != null) {
            billPayNotification.sendTo(PlayerHelper.getPlayer(estate.getOwnerID()), true, true, false);
        } else {
            try {
                billPayNotification.save();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // TODO: Add achievenements for money amount
    @SubscribeEvent
    public void onBillLate(BillEvent.LateEvent event) {
        if (!event.getBill().getTagCompound().hasKey("EstateID")) return;

        try {
            Estate estate = ModRealEstate.getEstate(UUID.fromString(event.getBill().getTagCompound().getString("EstateID")));

            if(estate == null) {
                event.getBill().delete();
                return;
            }

            if (event.getBill().getAmountDue() + estate.getRentPrice() <= 0) {
                event.getBill().setAmountDue(event.getBill().getAmountDue() + estate.getRentPrice());
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.MINUTE, estate.getRentPeriod() * 20);
                event.getBill().setDueDate(cal.getTime());
                event.getBill().save();
                return;
            }

            EntityPlayerMP renter = estate.getRenterID() == null ? null : PlayerHelper.getPlayer(estate.getRenterID());

            if (renter != null) {
                if (!billsDue.containsKey(renter)) {
                    billsDue.put(renter, System.currentTimeMillis() + ((60 * 10) * 1000L));
                }

                long minutes = DateHelper.getDiffMinutes(Calendar.getInstance().getTime(), new Date(billsDue.get(renter)));

                if (minutes < 1) {
                    Notification evictionNotification = new Notification(estate.getRenterID(), TextFormatting.DARK_RED + "You have been evicted!", new ResourceLocation(Minelife.MOD_ID, "textures/gui/notification/house-icon.png"), NotificationType.EDGED, 5, 0xFFFFFF);
                    evictionNotification.save();
                    estate.setRenterID(null);
                    estate.save();
                    event.getBill().delete();
                    return;
                }

                Notification lateNotification = new Notification(estate.getRenterID(), TextFormatting.DARK_RED + "You are late on payment! You have " + minutes + " minutes left to grab your things!", new ResourceLocation(Minelife.MOD_ID, "textures/gui/notification/house-icon.png"), NotificationType.EDGED, 5, 0xFFFFFF);
                lateNotification.sendTo(renter, true, true, false);
            } else {
                if (estate.getRenterID() != null) {
                    Notification evictionNotification = new Notification(estate.getRenterID(), TextFormatting.DARK_RED + "You have been evicted!", new ResourceLocation(Minelife.MOD_ID, "textures/gui/notification/house-icon.png"), NotificationType.EDGED, 5, 0xFFFFFF);
                    evictionNotification.save();
                    event.getBill().delete();
                }
                estate.setRenterID(null);
                estate.save();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
