package com.minelife.police;

import com.minelife.CommonProxy;
import com.minelife.AbstractMod;
import com.minelife.police.client.ClientProxy;
import com.minelife.police.packet.PacketArrestPlayer;
import com.minelife.util.client.render.ModelBipedCustom;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;

// TODO: NOT EVEN CLOSE, WILL NEED TO BE REDONE
public class ModPolice extends AbstractMod {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        registerPacket(PacketArrestPlayer.Handler.class, PacketArrestPlayer.class, Side.CLIENT);

        GameRegistry.registerItem(ItemHandcuff.INSTANCE, ItemHandcuff.NAME);
    }

    @Override
    public Class<? extends CommonProxy> getServerProxy() {
        return com.minelife.police.server.ServerProxy.class;
    }

    @Override
    public Class<? extends CommonProxy> getClientProxy() {
        return ClientProxy.class;
    }

    public static boolean isPlayerArrested(EntityPlayer player) {
        if(!player.getEntityData().hasKey("arrested")) return false;

        return player.getEntityData().getBoolean("arrested");
    }

    @SideOnly(Side.CLIENT)
    public static void applyPlayerArmRotations(ModelBipedCustom model, float f1) {
        // TODO: Apply correct rotations
        model.bipedRightArm.rotateAngleZ += MathHelper.cos(f1 * 0.09F) * 0.05F + 0.05F;
        model.bipedLeftArm.rotateAngleZ -= MathHelper.cos(f1 * 0.09F) * 0.05F + 0.05F;
        model.bipedRightArm.rotateAngleX += MathHelper.sin(f1 * 0.067F) * 0.05F;
        model.bipedLeftArm.rotateAngleX -= MathHelper.sin(f1 * 0.067F) * 0.05F;
    }

    // TODO: Make jail regions, and jail spawns/cells

}
