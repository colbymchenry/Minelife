package com.minelife.drug;

import com.minelife.Minelife;
import com.minelife.drug.ModDrugs;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.potion.Potion;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.living.LivingEvent;

public class CocaineEffect extends Potion {

    public static final ResourceLocation icon = new ResourceLocation(Minelife.MOD_ID, "textures/gui/cocaine_effect.png");

    public CocaineEffect(int id, boolean is_harmful, int amplifier)
    {
        super(id, is_harmful, amplifier);
        setIconIndex(0, 0).setPotionName("potion.cocaine_effect");
    }

    public int getStatusIconIndex()
    {
        Minecraft.getMinecraft().renderEngine.bindTexture(icon);
        return super.getStatusIconIndex();
    }

    @SubscribeEvent
    public void onEntityUpdate(LivingEvent.LivingUpdateEvent e)
    {
        if (e.entityLiving.isPotionActive(ModDrugs.cocaine_potion)) {
            if (e.entityLiving.getActivePotionEffect(ModDrugs.cocaine_potion).getDuration() == 0) {
                e.entityLiving.removePotionEffect(ModDrugs.cocaine_potion.id);
            }
        }
    }
}