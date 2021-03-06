package com.minelife.drugs;

import com.minelife.Minelife;
import net.minecraft.client.Minecraft;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class DetectableHempEffect extends Potion {

    public static Potion INSTANCE = new DetectableHempEffect(false, 0);

    private static final ResourceLocation icon = new ResourceLocation(Minelife.MOD_ID, "textures/gui/potion_detectable_hemp.png");

    private DetectableHempEffect(boolean is_harmful, int amplifier) {
        super(is_harmful, amplifier);
        setIconIndex(0, 0).setPotionName("potion.detectable_hemp");
        setRegistryName(Minelife.MOD_ID, "potion.detectable_hemp");
    }

    @Override
    public int getStatusIconIndex() {
        Minecraft.getMinecraft().renderEngine.bindTexture(icon);
        return super.getStatusIconIndex();
    }

    @SubscribeEvent
    public void onEntityUpdate(LivingEvent.LivingUpdateEvent e) {
        if (e.getEntityLiving().isPotionActive(this)) {
            if (e.getEntityLiving().getActivePotionEffect(this).getDuration() <= 1) {
                e.getEntityLiving().removePotionEffect(this);
            }
        }
    }
}