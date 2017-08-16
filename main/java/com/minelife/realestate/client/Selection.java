package com.minelife.realestate.client;

import com.minelife.Minelife;
import com.minelife.economy.ModEconomy;
import com.minelife.realestate.EstateRegion;
import com.minelife.realestate.client.packet.BlockPriceRequest;
import com.minelife.realestate.client.packet.SelectionAvailabilityRequest;
import com.minelife.realestate.client.packet.SelectionPurchaseRequest;
import com.minelife.realestate.util.GUIUtil;
import com.minelife.util.Vector;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.AxisAlignedBB;

import java.awt.*;
import java.sql.SQLException;
import java.util.UUID;

public class Selection {

    private static long pricePerBlock = 2;
    private static boolean isAvailable = true;

    public static void setPricePerBlock(long price) {
        pricePerBlock = price;
    }

    public static void setAvailable(boolean available) {
        isAvailable = available;
    }

    private AxisAlignedBB bounds;
    private String worldName;
    private UUID playerUniqueID;

    private Selection() {
    }

    @SideOnly(Side.CLIENT)
    public Selection(AxisAlignedBB bounds) {
        this.bounds = bounds;
        this.worldName = Minecraft.getMinecraft().theWorld.getWorldInfo().getWorldName();
        this.playerUniqueID = Minecraft.getMinecraft().thePlayer.getUniqueID();
    }

    @SideOnly(Side.CLIENT)
    public Selection(Vector start, Vector end) {
        Vector s = new Vector(Math.min(start.getBlockX(), end.getBlockX()), Math.min(start.getBlockY(), end.getBlockY()), Math.min(start.getBlockZ(), end.getBlockZ()));
        Vector e = new Vector(Math.max(start.getBlockX(), end.getBlockX()), Math.max(start.getBlockY(), end.getBlockY()), Math.max(start.getBlockZ(), end.getBlockZ()));
        this.bounds = AxisAlignedBB.getBoundingBox(s.getBlockX(), s.getBlockY(), s.getBlockZ(), e.getBlockX(), e.getBlockY(), e.getBlockZ());
        this.worldName = Minecraft.getMinecraft().theWorld.getWorldInfo().getWorldName();
        this.playerUniqueID = Minecraft.getMinecraft().thePlayer.getUniqueID();
    }

    public void setBounds(AxisAlignedBB bounds) {
        this.bounds = bounds;
    }

    public void setBounds(Vector start, Vector end) {
        Vector s = new Vector(Math.min(start.getBlockX(), end.getBlockX()), Math.min(start.getBlockY(), end.getBlockY()), Math.min(start.getBlockZ(), end.getBlockZ()));
        Vector e = new Vector(Math.max(start.getBlockX(), end.getBlockX()), Math.max(start.getBlockY(), end.getBlockY()), Math.max(start.getBlockZ(), end.getBlockZ()));
        this.setBounds(AxisAlignedBB.getBoundingBox(s.getBlockX(), s.getBlockY(), s.getBlockZ(), e.getBlockX(), e.getBlockY(), e.getBlockZ()));
    }

    public AxisAlignedBB getBounds() {
        return this.bounds;
    }

    public String getWorldName() {
        return this.worldName;
    }

    public UUID getPlayerUniqueID() {
        return this.playerUniqueID;
    }

    public long getPrice() {
        // Update Price Per Block From Server Stored Value
        if (FMLCommonHandler.instance().getSide() == Side.CLIENT) Minelife.NETWORK.sendToServer(new BlockPriceRequest());
        double dx = Math.abs(this.bounds.maxX - this.bounds.minX) + 1;
        double dy = Math.abs(this.bounds.maxY - this.bounds.minY) + 1;
        double dz = Math.abs(this.bounds.maxZ - this.bounds.minZ) + 1;
        return (long) (dx * dy * dz * pricePerBlock);
    }

    // TODO: Intersection with Region Check not working properly.
    public boolean isAvailable() {
        if (FMLCommonHandler.instance().getSide() == Side.SERVER) {
            EstateRegion region = null;
            try {
                region = EstateRegion.create(this.getWorldName(), this.getBounds());
                EstateRegion.delete(region.getUniqueID());
                return ModEconomy.getBalance(this.playerUniqueID, true) >= this.getPrice();
            } catch (Exception ignored) { }
            return false;
        } else {
            Minelife.NETWORK.sendToServer(new SelectionAvailabilityRequest(this));
        }
        return isAvailable;
    }

    public void purchase() {
        if (FMLCommonHandler.instance().getSide() == Side.SERVER) {
            if (this.isAvailable()) {
                EstateRegion region = null;
                try {
                    region = EstateRegion.create(this.getWorldName(), this.bounds);
                    // TODO: Create Estate

                    ModEconomy.withdraw(this.playerUniqueID, this.getPrice(), true);
                } catch (Exception e) {
                    if (region != null) try { EstateRegion.delete(region.getUniqueID()); }
                    catch (SQLException e1) { e1.printStackTrace(); }
                }
            }
        } else {
            Minelife.NETWORK.sendToServer(new SelectionPurchaseRequest(this));
        }
    }

    public void toBytes(ByteBuf buf) {
        buf.writeDouble(bounds.minX);
        buf.writeDouble(bounds.minY);
        buf.writeDouble(bounds.minZ);
        buf.writeDouble(bounds.maxX);
        buf.writeDouble(bounds.maxY);
        buf.writeDouble(bounds.maxZ);
        ByteBufUtils.writeUTF8String(buf, this.worldName);
        ByteBufUtils.writeUTF8String(buf, this.playerUniqueID.toString());
    }

    public static Selection fromBytes(ByteBuf buf) {
        Selection selection = new Selection();
        selection.bounds = AxisAlignedBB.getBoundingBox(buf.readDouble(), buf.readDouble(), buf.readDouble(), buf.readDouble(), buf.readDouble(), buf.readDouble());
        selection.worldName = ByteBufUtils.readUTF8String(buf);
        selection.playerUniqueID = UUID.fromString(ByteBufUtils.readUTF8String(buf));
        return selection;
    }

    @SideOnly(Side.CLIENT)
    public void highlight(float partialTickTime, Color color) {
        GUIUtil.drawCuboidAroundsBlocks(Minecraft.getMinecraft(), this.bounds, partialTickTime, color, true);
    }

    @Override
    public boolean equals(Object object) {
        if (object != null && object instanceof Selection) {
            Selection other = (Selection) object;
            return this.bounds.minX == other.bounds.minX &&
                    this.bounds.minY == other.bounds.minY &&
                    this.bounds.minZ == other.bounds.minZ &&
                    this.bounds.maxX == other.bounds.maxX &&
                    this.bounds.maxY == other.bounds.maxY &&
                    this.bounds.maxZ == other.bounds.maxZ &&
                    this.worldName.equals(other.worldName) && this.playerUniqueID.equals(other.playerUniqueID);
        }
        return false;
    }

    @SideOnly(Side.CLIENT)
    public Selection copy() {
        return new Selection(this.bounds);
    }

    @Override
    public String toString() {
        return "Selection { \n" +
                "bounds: " + this.bounds.toString() + "\n" +
                "worldName: " + this.worldName + "\n" +
                "playerUniqueID: " + this.playerUniqueID.toString() + "\n" +
                "}";
    }

}