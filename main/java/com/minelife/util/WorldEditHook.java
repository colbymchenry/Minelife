package com.minelife.util;

import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.forge.ForgeWorld;
import com.sk89q.worldedit.forge.ForgeWorldEdit;
import net.minecraft.entity.player.EntityPlayerMP;

public class WorldEditHook {

    // TODO: Get world edit to work so can uncomment code
    public static com.sk89q.worldedit.regions.Region getSelection(EntityPlayerMP player) {
        try {
            ForgeWorld forgeWorld = ForgeWorldEdit.inst.getWorld(player.getEntityWorld());
            LocalSession session = ForgeWorldEdit.inst.getSession(player);

            if (session != null) {
                com.sk89q.worldedit.regions.Region selection = session.getSelection(forgeWorld);
                if (selection != null) {
                    return selection;
                }
            }

        } catch (Exception ignored) {
        }

        return null;
    }

}
