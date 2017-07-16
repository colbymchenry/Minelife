package com.minelife.realestate.sign;

import com.minelife.realestate.Zone;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;

public class ListenerForSaleSign {

    @SubscribeEvent
    public void onBlockPlace(BlockEvent.PlaceEvent event) {
        World world = event.world;
        int x = event.x;
        int y = event.y;
        int z = event.z;
        EntityPlayer player = event.player;

        Zone zone = Zone.getZone(world, Vec3.createVectorHelper(x, y, z));
        if(zone == null) {
            player.addChatComponentMessage(new ChatComponentText("There is no zone there."));
            event.setCanceled(true);
            return;
        }

        if(zone.getOwner() == null || !zone.getOwner().equals(player.getUniqueID())) {
            player.addChatComponentMessage(new ChatComponentText("You are not the owner of this zone."));
            event.setCanceled(true);
            return;
        }

        if(zone.hasForSaleSign(Side.SERVER)) {
            player.addChatComponentMessage(new ChatComponentText("This zone already has a for sale sign."));
            event.setCanceled(true);
            return;
        }

        // TODO: Open for GuiZoneSell
    }

    @SubscribeEvent
    public void onBlockBreak(BlockEvent.BreakEvent event) {
        // TODO: Nothing
    }

}
