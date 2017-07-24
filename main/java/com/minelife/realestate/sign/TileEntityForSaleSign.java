package com.minelife.realestate.sign;

import com.google.common.collect.Lists;
import com.minelife.economy.Billing;
import com.minelife.realestate.Member;
import com.minelife.realestate.Zone;
import com.minelife.util.ListToString;
import com.minelife.util.StringToList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Vec3;

import java.util.*;

public class TileEntityForSaleSign extends TileEntity {

    private Set<Member> members = new TreeSet<>();
    private UUID renter;
    private boolean rentable;
    private long price;
    private int billingPeriod;
    private boolean allowBreaking, allowPlacing, allowInteracting;
    private long timeSync = 0L;

    // TODO: what to do if the bill is not paid (suspend all privileges)

    @Override
    public void writeToNBT(NBTTagCompound tagCompound)
    {
        super.writeToNBT(tagCompound);
        tagCompound.setString("renter", renter != null ? renter.toString() : "");
        tagCompound.setBoolean("rentable", rentable);
        tagCompound.setLong("price", price);
        tagCompound.setInteger("billingPeriod", billingPeriod);
        tagCompound.setBoolean("allowBreaking", allowBreaking);
        tagCompound.setBoolean("allowPlacing", allowPlacing);
        tagCompound.setBoolean("allowInteracting", allowInteracting);

        // write members
        List<Member> memberList = Lists.newArrayList();
        memberList.addAll(members);
        ListToString<Member> listToString = new ListToString<Member>(memberList) {
            @Override
            public String toString(Member o)
            {
                return o.toString();
            }
        };
        tagCompound.setString("members", listToString.getListAsString());
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompound)
    {
        super.readFromNBT(tagCompound);
        renter = !tagCompound.getString("renter").isEmpty() ? UUID.fromString(tagCompound.getString("renter")) : null;
        rentable = tagCompound.getBoolean("rentable");
        price = tagCompound.getLong("price");
        billingPeriod = tagCompound.getInteger("billingPeriod");
        allowBreaking = tagCompound.getBoolean("allowBreaking");
        allowPlacing = tagCompound.getBoolean("allowPlacing");
        allowInteracting = tagCompound.getBoolean("allowInteracting");

        // read members
        members.addAll(new StringToList<Member>(tagCompound.getString("members")) {
            @Override
            public Member parse(String s)
            {
                return Member.fromString(s);
            }
        }.getList());
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt)
    {
        super.onDataPacket(net, pkt);
        readFromNBT(pkt.func_148857_g());
    }

    @Override
    public Packet getDescriptionPacket()
    {
        NBTTagCompound tagCompound = new NBTTagCompound();
        writeToNBT(tagCompound);
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, tagCompound);
    }

    public void setRentable(boolean rentable)
    {
        this.rentable = rentable;
        sync();
    }

    public void setBillingPeriod(int billingPeriod)
    {
        this.billingPeriod = billingPeriod;
        sync();
    }

    public boolean isAllowBreaking()
    {
        return allowBreaking;
    }

    public void setAllowBreaking(boolean allowBreaking)
    {
        this.allowBreaking = allowBreaking;
        sync();
    }

    public boolean isAllowPlacing()
    {
        return allowPlacing;
    }

    public void setAllowPlacing(boolean allowPlacing)
    {
        this.allowPlacing = allowPlacing;
        sync();
    }

    public boolean isAllowInteracting()
    {
        return allowInteracting;
    }

    public void setAllowInteracting(boolean allowInteracting)
    {
        this.allowInteracting = allowInteracting;
        sync();
    }

    public void setPrice(long price)
    {
        this.price = price;
        sync();
    }

    public boolean isOccupied()
    {
        return renter != null;
    }

    public UUID getRenter()
    {
        return renter;
    }

    public void setRenter(UUID renter)
    {
        this.renter = renter;
        sync();
    }

    public boolean isRentable()
    {
        return rentable;
    }

    public long getPrice()
    {
        return price;
    }

    public int getBillingPeriod()
    {
        return billingPeriod;
    }

    public Set<Member> getMembers()
    {
        return members;
    }

    public Member getMember(EntityPlayer player)
    {
        return members.stream().filter(member -> member.getUniqueID().equals(player.getUniqueID())).findFirst().orElse(null);
    }

    public void sync()
    {
        this.markDirty();
        this.worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    @Override
    public void updateEntity()
    {
        if (worldObj.isRemote) return;

        if (timeSync == 0L) timeSync = System.currentTimeMillis() + 10000L;

        // we check every 10 seconds to see if the sign is still in a zone
        if (System.currentTimeMillis() > timeSync) {
            timeSync = System.currentTimeMillis() + 10000L;
            if (Zone.getZone(worldObj, Vec3.createVectorHelper(xCoord, yCoord, zCoord)) == null) {
                BlockForSaleSign.getBlock(true).dropBlockAsItem(worldObj, xCoord, yCoord, zCoord, worldObj.getBlockMetadata(xCoord, yCoord, zCoord), 0);
                worldObj.setBlockToAir(xCoord, yCoord, zCoord);
            }
        }
    }
}
