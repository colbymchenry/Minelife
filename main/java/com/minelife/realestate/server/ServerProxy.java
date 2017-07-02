package com.minelife.realestate.server;

import com.minelife.CommonProxy;
import com.minelife.Minelife;
import com.minelife.realestate.Estate;
import com.minelife.realestate.ModRealEstate;
import com.minelife.util.SimpleConfig;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraft.util.EnumChatFormatting;

import java.io.File;
import java.sql.SQLException;
import java.util.Map;
import java.util.logging.Level;

public class ServerProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event)
    {
        try
        {
            ModRealEstate.config = new SimpleConfig(new File(Minelife.getDirectory(), "realestate_config.txt"));
            ModRealEstate.getPricePerChunk();

            Map<String, Object> options = ModRealEstate.config.getOptions();
            options.put("BuyChunkMessage_Deduction", EnumChatFormatting.RED + "$" + ModRealEstate.getPricePerChunk() + EnumChatFormatting.GOLD + " has been deducted from your account.");
            options.put("BuyChunkMessage_Success", EnumChatFormatting.GOLD + "Chunk of land purchased!");
            options.put("BuyChunkMessage_Funds", EnumChatFormatting.RED + "You do not have enough funds.");
            options.put("Message_Intersects", EnumChatFormatting.RED + "This chunk intersects with a region or estate.");
            ModRealEstate.config.setOptions(options);

            Minelife.SQLITE.query("CREATE TABLE IF NOT EXISTS RealEstate_Estates (uuid VARCHAR(36) NOT NULL, region VARCHAR(36) NOT NULL);");
            Estate.initEstates();
        } catch (Exception e)
        {
            e.printStackTrace();
            Minelife.getLogger().log(Level.SEVERE, "", e);
        }
    }
}
