package com.minelife.casino.slotmachine;

import com.minelife.casino.ModCasino;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

import java.util.EnumMap;
import java.util.Random;

public class TileEntitySlotMachine extends TileEntity {

    private long startSpin;

    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);
    }

    @Override
    public void writeToNBT(NBTTagCompound tagCompound) {
        super.writeToNBT(tagCompound);
    }

    @Override
    public Packet getDescriptionPacket() {
        return super.getDescriptionPacket();
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
        super.onDataPacket(net, pkt);
    }

    public enum Symbol {

        IRON(Items.iron_ingot), APPLE(Items.apple), DIAMOND(Items.diamond),
        GOLDEN_APPLE(Items.golden_apple), MELON(Items.melon), COOKIE(Items.cookie);

        Item item;
        int minChance, maxChance;

        Symbol(Item item) {
            this.item = item;
            this.minChance = ModCasino.config.getInt("SlotMachine." + item.getItemStackDisplayName(new ItemStack(item)) + ".minChance", 0);
            this.maxChance = ModCasino.config.getInt("SlotMachine." + item.getItemStackDisplayName(new ItemStack(item)) + ".maxChance", 0);
        }
    }

    private Random random = new Random();

    public Symbol[] spin() {
        startSpin = System.currentTimeMillis();
        Symbol[] symbols = new Symbol[3];

        for(int i = 0; i < symbols.length; i++) {
            int chosenNumber = random.nextInt(128);
            for (Symbol symbol : Symbol.values()) {
                if(chosenNumber > symbol.minChance && chosenNumber < symbol.maxChance) {
                    symbols[i] = symbol;
                    break;
                }
            }
        }

        return symbols;
    }

    private int payout(Symbol symbols[]) {
        for (String s : ModCasino.config.getStringList("SlotMachine.Combos")) {

        }
        return 0;
//        return total;
    }
}
