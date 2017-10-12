package com.minelife.realestate;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.minelife.Minelife;
import com.minelife.economy.Billing;
import com.minelife.util.MLConfig;
import com.minelife.util.client.INameReceiver;
import com.minelife.util.configuration.InvalidConfigurationException;
import com.minelife.util.server.Callback;
import com.minelife.util.server.NameFetcher;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class EstateData extends Estate implements INameReceiver, Callback {

    private UUID owner, renter;
    private int id, rentPeriod;
    private double purchasePrice, rentPrice;
    private String ownerName, renterName, intro, outro;
    private List<Permission> globalPermissionList, ownerPermissionList, renterPermissionList,
            allowedToChangePermissionList, estatePermissionList;
    private AxisAlignedBB bounds;
    private Map<UUID, List<Permission>> members;

    public EstateData(int id, UUID owner, UUID renter, int rentPeriod, double purchasePrice, double rentPrice, String intro, String outro, List<Permission> globalPermissionList, List<Permission> ownerPermissionList, List<Permission> renterPermissionList, List<Permission> allowedToChangePermissionList, List<Permission> estatePermissionList, AxisAlignedBB bounds, Map<UUID, List<Permission>> members) throws IOException, InvalidConfigurationException {
        this.id = id;
        this.owner = owner;
        this.renter = renter;
        this.rentPeriod = rentPeriod;
        this.purchasePrice = purchasePrice;
        this.rentPrice = rentPrice;
        if(Minelife.getSide() == Side.CLIENT) {
            this.ownerName = owner == null ? "NULL" : NameFetcher.asyncFetchClient(owner, this);
            this.renterName = renter == null ? "NULL" : NameFetcher.asyncFetchClient(renter, this);
        } else {
            this.ownerName = owner == null ? "NULL" : NameFetcher.asyncFetchServer(owner, this);
            this.renterName = renter == null ? "NULL" : NameFetcher.asyncFetchServer(renter, this);
        }
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

    @SideOnly(Side.SERVER)
    public EstateData(Estate estate) {
        this.id = estate.getID();
        this.owner = estate.getOwner();
        this.renter = estate.getRenter();
        this.rentPeriod = estate.getRentPeriod();
        this.purchasePrice = estate.getPurchasePrice();
        this.rentPrice = estate.getRentPrice();
        this.ownerName = owner == null ? "NULL" : NameFetcher.asyncFetchServer(owner, this);
        this.renterName = renter == null ? "NULL" : NameFetcher.asyncFetchServer(renter, this);
        this.intro = estate.getIntro();
        this.outro = estate.getOutro();
        this.globalPermissionList = estate.getGlobalPermissions();
        this.ownerPermissionList = estate.getOwnerPermissions();
        this.renterPermissionList = estate.getRenterPermissions();
        this.allowedToChangePermissionList = estate.getGlobalPermissionsAllowedToChange();
        this.estatePermissionList = estate.getEstatePermissions();
        this.bounds = estate.getBounds();
        this.members = estate.getMembers();
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
        if (getRenter() != null) return false;
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
        if (uuid.equals(owner)) ownerName = name;
        else if (uuid.equals(renter)) renterName = name;
    }

    public void toBytes(ByteBuf buf) {
        buf.writeInt(id);
        ByteBufUtils.writeUTF8String(buf, getOwner() != null ? getOwner().toString() : " ");
        ByteBufUtils.writeUTF8String(buf, getRenter() != null ? getRenter().toString() : " ");
        buf.writeInt(getRentPeriod());
        buf.writeDouble(getPurchasePrice());
        buf.writeDouble(getRentPrice());
        ByteBufUtils.writeUTF8String(buf, getIntro() != null && !getIntro().trim().isEmpty() ? getIntro() : " ");
        ByteBufUtils.writeUTF8String(buf, getOutro() != null && !getOutro().trim().isEmpty() ? getOutro() : " ");
        buf.writeInt(getGlobalPermissions().size());
        buf.writeInt(getOwnerPermissions().size());
        buf.writeInt(getRenterPermissions().size());
        buf.writeInt(getGlobalPermissionsAllowedToChange().size());
        buf.writeInt(getEstatePermissions().size());
        getGlobalPermissions().forEach(p -> ByteBufUtils.writeUTF8String(buf, p.name()));
        getOwnerPermissions().forEach(p -> ByteBufUtils.writeUTF8String(buf, p.name()));
        getRenterPermissions().forEach(p -> ByteBufUtils.writeUTF8String(buf, p.name()));
        getGlobalPermissionsAllowedToChange().forEach(p -> ByteBufUtils.writeUTF8String(buf, p.name()));
        getEstatePermissions().forEach(p -> ByteBufUtils.writeUTF8String(buf, p.name()));
        buf.writeDouble(getBounds().minX);
        buf.writeDouble(getBounds().minY);
        buf.writeDouble(getBounds().minZ);
        buf.writeDouble(getBounds().maxX);
        buf.writeDouble(getBounds().maxY);
        buf.writeDouble(getBounds().maxZ);
        buf.writeInt(getMembers().size());
        getMembers().forEach((uuid, permissions) -> {
            ByteBufUtils.writeUTF8String(buf, uuid.toString());
            buf.writeInt(permissions.size());
            permissions.forEach(p -> ByteBufUtils.writeUTF8String(buf, p.name()));
        });
    }

    public static EstateData fromBytes(ByteBuf buf) throws IOException, InvalidConfigurationException {
        int id = buf.readInt();
        String ownerStr = ByteBufUtils.readUTF8String(buf);
        String renterStr = ByteBufUtils.readUTF8String(buf);
        UUID owner = ownerStr.trim().isEmpty() ? null : UUID.fromString(ownerStr);
        UUID renter = renterStr.trim().isEmpty() ? null : UUID.fromString(renterStr);
        int rentPeriod = buf.readInt();
        double purchasePrice = buf.readDouble();
        double rentPrice = buf.readDouble();
        String intro = ByteBufUtils.readUTF8String(buf);
        String outro = ByteBufUtils.readUTF8String(buf);
        int globalPermsSize = buf.readInt();
        int ownerPermsSize = buf.readInt();
        int renterPermsSize = buf.readInt();
        int permsAllowedToChangeSize = buf.readInt();
        int estatePermsSize = buf.readInt();
        List<Permission> globalPerms = Lists.newArrayList();
        List<Permission> ownerPerms = Lists.newArrayList();
        List<Permission> renterPerms = Lists.newArrayList();
        List<Permission> allowedToChangePerms = Lists.newArrayList();
        List<Permission> estatePerms = Lists.newArrayList();
        for (int i = 0; i < globalPermsSize; i++) globalPerms.add(Permission.valueOf(ByteBufUtils.readUTF8String(buf)));
        for (int i = 0; i < ownerPermsSize; i++) ownerPerms.add(Permission.valueOf(ByteBufUtils.readUTF8String(buf)));
        for (int i = 0; i < renterPermsSize; i++) renterPerms.add(Permission.valueOf(ByteBufUtils.readUTF8String(buf)));
        for (int i = 0; i < permsAllowedToChangeSize; i++)
            allowedToChangePerms.add(Permission.valueOf(ByteBufUtils.readUTF8String(buf)));
        for (int i = 0; i < estatePermsSize; i++) estatePerms.add(Permission.valueOf(ByteBufUtils.readUTF8String(buf)));
        AxisAlignedBB bounds = AxisAlignedBB.getBoundingBox(buf.readDouble(), buf.readDouble(), buf.readDouble(), buf.readDouble(), buf.readDouble(), buf.readDouble());
        Map<UUID, List<Permission>> members = Maps.newHashMap();
        int membersSize = buf.readInt();
        for (int i = 0; i < membersSize; i++) {
            UUID memberUUID = UUID.fromString(ByteBufUtils.readUTF8String(buf));
            List<Permission> permissions = Lists.newArrayList();
            int permsSize = buf.readInt();
            for (int i1 = 0; i1 < permsSize; i1++) {
                permissions.add(Permission.valueOf(ByteBufUtils.readUTF8String(buf)));
            }
            members.put(memberUUID, permissions);
        }
        return new EstateData(id, owner, renter, rentPeriod, purchasePrice, rentPrice, intro, outro, globalPerms, ownerPerms,
                renterPerms, allowedToChangePerms, estatePerms, bounds, members);
    }

    @Override
    public void callback(Object... objects) {

    }
}
