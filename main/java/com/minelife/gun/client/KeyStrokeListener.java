package com.minelife.gun.client;

import com.minelife.Minelife;
import com.minelife.gun.BaseGun;
import com.minelife.gun.BaseGunClient;
import com.minelife.gun.packet.PacketMouseClick;
import com.minelife.gun.packet.PacketReload;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.living.LivingEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class KeyStrokeListener {

    private KeyBinding keyReload = new KeyBinding("key.tut_inventory.desc", Keyboard.KEY_R, "key." + Minelife.MOD_ID + ".category");

    public KeyStrokeListener() {
        ClientRegistry.registerKeyBinding(keyReload);
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if(keyReload.isPressed()) {
            Minelife.NETWORK.sendToServer(new PacketReload());
        }
    }

}
