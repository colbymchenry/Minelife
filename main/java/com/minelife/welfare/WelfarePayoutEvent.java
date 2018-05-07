package com.minelife.welfare;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.Event;

public class WelfarePayoutEvent extends Event {

    private EntityPlayer player;
    private int amount;

    public WelfarePayoutEvent(EntityPlayer player, int amount) {
        this.player = player;
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }

    public EntityPlayer getPlayer() {
        return player;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    @Override
    public boolean isCancelable() {
        return true;
    }
}
