package com.minelife.gun.client;

import com.minelife.gun.item.guns.ItemGun;
import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.ReflectionHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSound;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.audio.SoundManager;
import net.minecraft.util.ResourceLocation;

import java.util.Iterator;
import java.util.Map;

public class TickHandler {

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event)
    {
        if (Minecraft.getMinecraft().thePlayer != null) {
            SoundManager mng = ReflectionHelper.getPrivateValue(SoundHandler.class,
                    Minecraft.getMinecraft().getSoundHandler(), "field_147694_f", "sndManager");
            Map playingSounds = ReflectionHelper.getPrivateValue(SoundManager.class,
                    mng, "field_148629_h", "playingSounds");
            Iterator it = playingSounds.keySet().iterator();
            while (it.hasNext()) {
                PositionedSound ps = (PositionedSound) playingSounds.get(it.next());
                ResourceLocation reloc = ReflectionHelper.getPrivateValue(PositionedSound.class,
                        ps, "field_147664_a");

                if (reloc.getResourcePath().contains("guns.") && reloc.getResourcePath().contains(".reload")) {
                    if (Minecraft.getMinecraft().thePlayer.getHeldItem() == null || !(Minecraft.getMinecraft().thePlayer.getHeldItem().getItem() instanceof ItemGun))
                        Minecraft.getMinecraft().getSoundHandler().stopSound(ps);
                }
            }
        }
    }

}
