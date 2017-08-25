package com.minelife.drug;

import com.minelife.Minelife;
import com.minelife.drug.ModDrugs;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.living.LivingEvent;

public class MarijuanaEffect extends Potion {

    public static final ResourceLocation icon = new ResourceLocation(Minelife.MOD_ID, "textures/gui/marijuana_effect.png");

    public MarijuanaEffect(int id, boolean is_harmful, int amplifier)
    {
        super(id, is_harmful, amplifier);
        setIconIndex(0, 0).setPotionName("potion.marijuana_effect");
    }

    public int getStatusIconIndex()
    {
        Minecraft.getMinecraft().renderEngine.bindTexture(icon);
        return super.getStatusIconIndex();
    }

    @SubscribeEvent
    public void onEntityUpdate(LivingEvent.LivingUpdateEvent e)
    {
        if (e.entityLiving.isPotionActive(ModDrugs.marijuana_potion)) {
            if (e.entityLiving.getActivePotionEffect(ModDrugs.marijuana_potion).getDuration() == 0) {
                e.entityLiving.removePotionEffect(ModDrugs.marijuana_potion.id);
            }
        }
    }
}