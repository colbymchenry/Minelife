package com.minelife.police;

import com.google.common.collect.Lists;
import com.minelife.Minelife;
import com.minelife.core.event.EntityDismountEvent;
import com.minelife.police.cop.EntityCop;
import com.minelife.util.PacketPlaySound;
import com.minelife.util.StringHelper;
import com.minelife.util.client.PacketDropEntity;
import com.minelife.util.client.PacketRidingEntity;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.List;
import java.util.UUID;

public class ItemHandcuff extends Item {

    //TODO: Disable dismount until core mod workds

    public ItemHandcuff() {
        setRegistryName(Minelife.MOD_ID, "handcuff");
        setUnlocalizedName(Minelife.MOD_ID + ":handcuff");
        setCreativeTab(CreativeTabs.MISC);
        setMaxStackSize(1);
    }

    public void registerModel() {
        ModelResourceLocation itemModelResourceLocation = new ModelResourceLocation(Minelife.MOD_ID + ":handcuff", "inventory");
        ModelLoader.setCustomModelResourceLocation(this, 0, itemModelResourceLocation);
    }

    public void registerRecipe() {
        ResourceLocation name = new ResourceLocation(Minelife.MOD_ID + ":handcuff");
        GameRegistry.addShapedRecipe(name, null, new ItemStack(this),
                "AA ",
                "AAA",
                " AA",
                'A', Ingredient.fromStacks(new ItemStack(Items.IRON_NUGGET)));
    }

    public static void setHandcuffed(EntityPlayer player, boolean value, boolean playSound) {
        List<String> handcuffed = ModPolice.getConfig().getStringList("Handcuffed") != null ? ModPolice.getConfig().getStringList("Handcuffed") : Lists.newArrayList();
        if (value) {
            if (!isHandcuffed(player)) {
                handcuffed.add(player.getUniqueID().toString() + "," + UUID.randomUUID().toString());
                player.addPotionEffect(new PotionEffect(ArrestedEffect.INSTANCE, Integer.MAX_VALUE, 0, false, false));

                if (playSound)
                    Minelife.getNetwork().sendToAllAround(new PacketPlaySound("minelife:handcuff", 1, 1), new NetworkRegistry.TargetPoint(player.dimension, player.posX, player.posY, player.posZ, 10));
            }
        } else {
            player.removePotionEffect(ArrestedEffect.INSTANCE);
            player.addPotionEffect(new PotionEffect(ArrestedEffect.INSTANCE, 20, 0, false, false));
        }

        ModPolice.getConfig().set("Handcuffed", handcuffed);
        ModPolice.getConfig().save();
    }

    public static UUID getKeyUUID(EntityPlayer player) {
        List<String> handcuffed = ModPolice.getConfig().getStringList("Handcuffed") != null ? ModPolice.getConfig().getStringList("Handcuffed") : Lists.newArrayList();
        for (String s : handcuffed) {
            if (s.contains(",")) {
                if (s.contains(player.getUniqueID().toString())) {
                    return UUID.fromString(s.split(",")[1]);
                }
            }
        }
        return null;
    }

    public static boolean isHandcuffed(EntityPlayer player) {
        return player.getActivePotionEffect(ArrestedEffect.INSTANCE) != null;
    }



}
