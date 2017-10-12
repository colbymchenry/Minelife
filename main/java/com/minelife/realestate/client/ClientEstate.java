package com.minelife.realestate.client;

import com.google.common.collect.Lists;
import com.minelife.economy.Billing;
import com.minelife.realestate.Estate;
import com.minelife.realestate.Permission;
import com.minelife.realestate.RentBillHandler;
import com.minelife.realestate.Selection;
import com.minelife.util.MLConfig;
import com.minelife.util.client.INameReceiver;
import com.minelife.util.configuration.InvalidConfigurationException;
import com.minelife.util.server.NameFetcher;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ClientEstate extends Estate implements INameReceiver{

    private UUID owner, renter;
    private int id, rentPeriod;
    private double purchasePrice, rentPrice;
    private String ownerName, renterName, intro, outro;
    private List<Permission> globalPermissionList, ownerPermissionList, renterPermissionList,
            allowedToChangePermissionList, estatePermissionList;
    private AxisAlignedBB bounds;
    private Map<UUID, List<Permission>> members;

    public ClientEstate(int id, UUID owner, UUID renter, int rentPeriod, double purchasePrice, double rentPrice, String intro, String outro, List<Permission> globalPermissionList, List<Permission> ownerPermissionList, List<Permission> renterPermissionList, List<Permission> allowedToChangePermissionList, List<Permission> estatePermissionList, AxisAlignedBB bounds, Map<UUID, List<Permission>> members) throws IOException, InvalidConfigurationException {
        this.id = id;
        this.owner = owner;
        this.renter = renter;
        this.rentPeriod = rentPeriod;
        this.purchasePrice = purchasePrice;
        this.rentPrice = rentPrice;
        this.ownerName = NameFetcher.asyncFetchClient(owner, this);
        this.renterName = NameFetcher.asyncFetchClient(renter, this);;
        this.intro = intro;
        this.outro = outro;
        this.globalPermissionList = globalPermissionList;
        this.ownerPermissionList = ownerPermissionList;
        this.renterPermissionList = renterPermissionList;
        this.allowedToChangePermissionList = allowedToChangePermissionList;
        this.estatePermissionList = estatePermissionList;
        this.bounds = bounds;
        this.members = members;
    }

    @Override
    public int getID() {
        return id;
    }

    @Override
    public MLConfig getConfig() {
        return null;
    }

    @Override
    public double getPurchasePrice() {
        return purchasePrice;
    }

    @Override
    public boolean isPurchasable() {
        return purchasePrice != -1;
    }

    @Override
    public double getRentPrice() {
        return rentPrice;
    }

    @Override
    public boolean isForRent() {
        if(getRenter() != null) return false;
        return getRentPrice() != -1;
    }

    @Override
    public int getRentPeriod() {
        return rentPeriod;
    }

    @Override
    public String getOutro() {
        return outro;
    }

    @Override
    public String getIntro() {
        return intro;
    }

    @Override
    public List<Permission> getGlobalPermissions() {
        return globalPermissionList;
    }

    @Override
    public List<Permission> getOwnerPermissions() {
        return ownerPermissionList;
    }

    @Override
    public List<Permission> getRenterPermissions() {
        return renterPermissionList;
    }

    @Override
    public List<Permission> getGlobalPermissionsAllowedToChange() {
        return allowedToChangePermissionList;
    }

    @Override
    public List<Permission> getEstatePermissions() {
        return estatePermissionList;
    }

    @Override
    public UUID getOwner() {
        return owner;
    }

    public String getOwnerName() {
        return ownerName;
    }

    @Override
    public UUID getRenter() {
        return renter;
    }

    public String getRenterName() {
        return renterName;
    }

    @Override
    public Map<UUID, List<Permission>> getMembers() {
        return members;
    }

    @Override
    public AxisAlignedBB getBounds() {
        return bounds;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public World getWorld() {
        return Minecraft.getMinecraft().theWorld;
    }

    @Override
    public boolean contains(Estate estate) {
        System.out.println("NOT IMPLEMENTED");
        return false;
    }

    @Override
    public boolean contains(World world, double x, double y, double z) {
        return bounds.isVecInside(Vec3.createVectorHelper(x, y, z));
    }

    @Override
    public boolean contains(Selection selection) {
        return super.contains(selection);
    }

    @Override
    public boolean intersects(Selection selection) {
        return super.intersects(selection);
    }

    @Override
    public Estate getParentEstate() {
        System.out.println("NOT IMPLEMENTED");
        return null;
    }

    @Override
    public List<Estate> getContainingEstates() {
        System.out.println("NOT IMPLEMENTED");
        return null;
    }

    @Override
    public Estate getMasterEstate() {
        System.out.println("NOT IMPLEMENTED");
        return null;
    }

    @Override
    public void deleteEstate() {
        System.out.println("NOT IMPLEMENTED");
    }

    @Override
    public List<Permission> getPlayerPermissions(EntityPlayer player) {
        return Lists.newArrayList();
    }

    @Override
    public void setGlobalPermissions(List<Permission> permissions) {
        globalPermissionList = permissions;
    }

    @Override
    public void setOwnerPermissions(List<Permission> permissions) {
        ownerPermissionList = permissions;
    }

    @Override
    public void setRenterPermissions(List<Permission> permissions) {
        renterPermissionList = permissions;
    }

    @Override
    public void setPermissionsAllowedToChange(List<Permission> permissions) {
        allowedToChangePermissionList = permissions;
    }

    @Override
    public void setEstatePermissions(List<Permission> permissions) {
        estatePermissionList = permissions;
    }

    @Override
    public void setRentPeriod(int period) {
        rentPeriod = period;
    }

    @Override
    public void setOwner(UUID owner) {
        this.owner = owner;
    }

    @Override
    public void setRenter(UUID renter) {
        this.renter = renter;
    }

    @Override
    public void setMembers(Map<UUID, List<Permission>> members) {
        this.members = members;
    }

    @Override
    public void setPurchasePrice(double price) {
        purchasePrice = price;
    }

    @Override
    public void setRentPrice(double price) {
        rentPrice = price;
    }

    @Override
    public void setBounds(AxisAlignedBB bounds) {
        this.bounds = bounds;
    }

    @Override
    public void setWorld(World world) {
        System.out.println("NOT IMPLEMENTED");
    }

    @Override
    public void setIntro(String intro) {
        this.intro = intro;
    }

    @Override
    public void setOutro(String outro) {
        this.outro = outro;
    }

    @Override
    public void setBill(RentBillHandler bill) {
        System.out.println("NOT IMPLEMENTED");
    }

    @Override
    public Billing.Bill getBill() {
        return null;
    }

    @Override
    public void nameReceived(UUID uuid, String name) {
        if(uuid.equals(owner)) ownerName = name;
        else if(uuid.equals(renter)) renterName = name;
    }
}
