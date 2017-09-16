package com.minelife.police.server;

import com.google.common.collect.Lists;
import com.minelife.CommonProxy;
import com.minelife.Minelife;
import com.minelife.core.event.EntityDismountEvent;
import com.minelife.police.Charge;
import com.minelife.police.ItemTicket;
import com.minelife.police.arresting.ArrestingHandler;
import com.minelife.region.server.Region;
import com.minelife.util.MLConfig;
import com.minelife.util.NBTUtil;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.MinecraftForge;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class ServerProxy extends CommonProxy {

    public MLConfig config;

    @Override
    public void preInit(FMLPreInitializationEvent event) throws Exception {
        Minelife.SQLITE.query("CREATE TABLE IF NOT EXISTS policeofficers (playerUUID TEXT, xp INT)");
        Minelife.SQLITE.query("CREATE TABLE IF NOT EXISTS policetickets (ticketID INT, playerUUID VARCHAR(36), officerUUID VARCHAR(36), ticketNBT TEXT, timeServed DOUBLE DEFAULT '0.0', amountPayed DOUBLE DEFAULT '0.0D')");
        MinecraftForge.EVENT_BUS.register(this);

        config = new MLConfig("police");
        config.addDefault("prison_yard.region_uuid", "");
        config.addDefault("prison_yard.enter_x", 0.0D);
        config.addDefault("prison_yard.enter_y", 0.0D);
        config.addDefault("prison_yard.enter_z", 0.0D);
        config.addDefault("prison_yard.exit_x", 0.0D);
        config.addDefault("prison_yard.exit_y", 0.0D);
        config.addDefault("prison_yard.exit_z", 0.0D);
        config.save();
    }

    /**
        Prevent arrested players from dismounting an officer
     */
    @SubscribeEvent
    public void onDismount(EntityDismountEvent event) {
        if(event.entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.entity;
            if(ArrestingHandler.isArrested(player)) event.setCanceled(true);
        }
    }

    public void setPrisonRegion(Region region) {
        config.set("prison_yard.region_uuid", region == null ? "" : region.getUniqueID().toString());
        config.save();
    }

    public void setPrisonEntrance(double x, double y, double z) {
        config.set("prison_yard.enter_x", x);
        config.set("prison_yard.enter_y", y);
        config.set("prison_yard.enter_z", z);
        config.save();
    }

    public void setPrisonExit(double x, double y, double z) {
        config.set("prison_yard.exit_x", x);
        config.set("prison_yard.exit_y", y);
        config.set("prison_yard.exit_z", z);
        config.save();
    }

    public Region getPrisonRegion() {
        if(config.getString("prison_yard.region_uuid").isEmpty()) return null;
        return Region.getRegionFromUUID(UUID.fromString(config.getString("prison_yard.region_uuid")));
    }

    public Vec3 getPrisonEntrance() {
        double x = config.getDouble("prison_yard.enter_x");
        double y = config.getDouble("prison_yard.enter_y");
        double z = config.getDouble("prison_yard.enter_z");
        return x == 0.0D && y == 0.0D && z == 0.0D ? null : Vec3.createVectorHelper(x, y, z);
    }

    public Vec3 getPrisonExit() {
        double x = config.getDouble("prison_yard.exit_x");
        double y = config.getDouble("prison_yard.exit_y");
        double z = config.getDouble("prison_yard.exit_z");
        return x == 0.0D && y == 0.0D && z == 0.0D ? null : Vec3.createVectorHelper(x, y, z);
    }

    public List<TicketInfo> getTickets(EntityPlayer player) {
        List<TicketInfo> ticketList = Lists.newArrayList();
        try {
            ResultSet result = Minelife.SQLITE.query("SELECT * FROM policetickets WHERE playerUUID='" + player.getUniqueID().toString() + "'");
            while(result.next()) {
                int ticketID = result.getInt("ticketID");
                UUID officer = UUID.fromString(result.getString("officer"));
                ItemStack ticketStack = ItemStack.loadItemStackFromNBT(NBTUtil.fromString(result.getString("ticketNBT")));
                double timeServed = result.getDouble("timeServed");
                double amountPayed = result.getDouble("amountPayed");
                ticketList.add(new TicketInfo(ticketID, player.getUniqueID(), officer, ticketStack, timeServed, amountPayed));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return ticketList;
    }



    public class TicketInfo {
        public int ticketID;
        public UUID player, officer;
        public ItemStack itemstack;
        public double timeServed;
        public double amountPayed;

        public TicketInfo(int ticketID, UUID player, UUID officer, ItemStack itemstack, double timeServed, double amountPayed) {
            this.ticketID = ticketID;
            this.player = player;
            this.officer = officer;
            this.itemstack = itemstack;
            this.timeServed = timeServed;
            this.amountPayed = amountPayed;
        }

        public void setTimeServed(double timeServed) {
            this.timeServed = timeServed;
            try {
                Minelife.SQLITE.query("UPDATE policetickets SET timeServed='" + timeServed + "' WHERE ticketID='" + ticketID + "'");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        public void setAmountPayed(double amountPayed) {
            this.amountPayed = amountPayed;
            try {
                Minelife.SQLITE.query("UPDATE policetickets SET amountPayed='" + amountPayed + "' WHERE ticketID='" + ticketID + "'");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        public void delete() {
            try {
                Minelife.SQLITE.query("DELETE FROM policetickets WHERE ticketID='" + ticketID + "'");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        public List<Charge> getCharges() {
            return ItemTicket.getChargesForTicket(itemstack);
        }

        public double getTimeToServe() {
            double totalJailTime = 0.0D;
            for (Charge charge : getCharges()) totalJailTime += charge.jailTime;
            return totalJailTime;
        }

        public double getAmountToPay() {
            double totalAmountPayed = 0.0D;
            for (Charge charge : getCharges()) totalAmountPayed += charge.bail;
            return totalAmountPayed;
        }
    }

}
