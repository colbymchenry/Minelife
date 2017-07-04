package com.minelife.realestate;

import com.minelife.CustomMessageException;
import com.minelife.Minelife;
import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class EstateSellingForm {

    private static final DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

    private UUID uuid;
    private Estate estate;
    private Date datePublished;
    private long price;
    private boolean renting;
    private String title;

    private EstateSellingForm()
    {
    }

    public EstateSellingForm(UUID uuid) throws Exception
    {
        this.uuid = uuid;

        ResultSet result = Minelife.SQLITE.query("SELECT * FROM RealEstate_Listings WHERE uuid='" + uuid.toString() + "'");

        if (!result.next()) throw new SQLException("Listing not found.");

        this.estate = Estate.getEstate(UUID.fromString(result.getString("estate")));
        this.price = result.getLong("price");
        this.renting = result.getBoolean("renting");
        this.datePublished = dateFormat.parse(result.getString("datePublished"));
        this.title = result.getString("title");
    }

    public static EstateSellingForm createForm(Estate estate, long price, boolean renting, String title) throws Exception
    {
        UUID formID = UUID.randomUUID();

        ResultSet result = Minelife.SQLITE.query("SELECT * FROM RealEstate_Listings WHERE estate='" + estate.getUUID().toString() + "'");
        if (result.next()) throw new CustomMessageException("There is already a listing for this estate.");

        Date datePublished = Calendar.getInstance().getTime();

        Minelife.SQLITE.query("INSERT INTO RealEstate_Listings (uuid, estate, price, renting, datePublished, title) VALUES (" +
                "'" + formID.toString() + "'," +
                "'" + estate.getUUID().toString() + "'," +
                "'" + price + "'," +
                "'" + (!renting ? 0 : 1) + "'," +
                "'" + dateFormat.format(datePublished) + "'," +
                "'" + title + "')");

        return new EstateSellingForm(formID);
    }

    public void toBytes(ByteBuf buf)
    {
        ByteBufUtils.writeUTF8String(buf, uuid.toString());
        ByteBufUtils.writeUTF8String(buf, dateFormat.format(datePublished));
        buf.writeLong(price);
        buf.writeBoolean(renting);
        ByteBufUtils.writeUTF8String(buf, title);
        estate.toBytes(buf);
    }

    public static EstateSellingForm fromBytes(ByteBuf buf) throws ParseException
    {
        EstateSellingForm sellingForm = new EstateSellingForm();
        sellingForm.uuid = UUID.fromString(ByteBufUtils.readUTF8String(buf));
        sellingForm.datePublished = dateFormat.parse(ByteBufUtils.readUTF8String(buf));
        sellingForm.price = buf.readLong();
        sellingForm.renting = buf.readBoolean();
        sellingForm.title = ByteBufUtils.readUTF8String(buf);
        sellingForm.estate = Estate.fromBytes(buf);
        return sellingForm;
    }


}
