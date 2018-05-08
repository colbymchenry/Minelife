package com.minelife.police;

import com.google.common.collect.Lists;
import com.minelife.Minelife;
import com.minelife.core.event.EntityDismountEvent;
import com.minelife.drugs.XRayEffect;
import com.minelife.guns.item.ItemDynamite;
import com.minelife.util.PacketPlaySound;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
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
                "AA",
                "AA",
                'A', Ingredient.fromStacks(new ItemStack(Items.IRON_INGOT)));
    }

    public static void setHandcuffed(EntityPlayer player, boolean value, boolean playSound) {
        List<String> handcuffed = ModPolice.getConfig().getStringList("Handcuffed") != null ? ModPolice.getConfig().getStringList("Handcuffed") : Lists.newArrayList();
        if (value) {
            if (!isHandcuffed(player)) {
                handcuffed.add(player.getUniqueID().toString() + "," + UUID.randomUUID().toString());
                player.addPotionEffect(new PotionEffect(MobEffects.SPEED, Integer.MAX_VALUE, -10, false, false));
                player.addPotionEffect(new PotionEffect(MobEffects.JUMP_BOOST, Integer.MAX_VALUE, -10, false, false));

                if (playSound)
                    Minelife.getNetwork().sendToAllAround(new PacketPlaySound("minelife:handcuff", 1, 1), new NetworkRegistry.TargetPoint(player.dimension, player.posX, player.posY, player.posZ, 10));
            }
        } else {
            String toRemove = null;
            for (String s : handcuffed) {
                if (s.contains(player.getUniqueID().toString())) {
                    toRemove = s;
                    break;
                }
            }
            handcuffed.remove(toRemove);
            player.removePotionEffect(MobEffects.SPEED);
            player.removePotionEffect(MobEffects.JUMP_BOOST);
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
        return ModPolice.getConfig().getStringList("Handcuffed") != null
                && ModPolice.getConfig().getStringList("Handcuffed").stream().filter(playerString -> playerString.contains(player.getUniqueID().toString())).findFirst().orElse(null) != null;
    }

    @SubscribeEvent
    public void onDismount(EntityDismountEvent event) {
        EntityPlayer player = (EntityPlayer) event.entity;

        if (isHandcuffed(player)) event.setCanceled(true);
    }

    @SubscribeEvent
    public void onInteract(PlayerInteractEvent.EntityInteract event) {
        if (!(event.getTarget() instanceof EntityPlayer)) return;

        EntityPlayer handcuffing = event.getEntityPlayer();
        EntityPlayer beingHandcuffed = (EntityPlayer) event.getTarget();

        if (handcuffing.getHeldItem(event.getHand()).getItem() != this) return;

        if(handcuffing.getDistance(beingHandcuffed) > 1) return;

        if (isHandcuffed(beingHandcuffed)) {
            handcuffing.sendMessage(new TextComponentString(TextFormatting.RED + "That player is already handcuffed."));
            return;
        }

        setHandcuffed(beingHandcuffed, true, true);

        ItemStack stack = handcuffing.getHeldItem(event.getHand());

        stack.shrink(1);

        if (stack.isEmpty()) handcuffing.inventory.deleteStack(stack);

        ItemStack key = new ItemStack(ModPolice.itemHandcuffKey);
        ItemHandcuffKey.setUUID(key, getKeyUUID(beingHandcuffed), beingHandcuffed.getUniqueID());
        handcuffing.setHeldItem(event.getHand(), key);

        handcuffing.inventoryContainer.detectAndSendChanges();
    }

    @SubscribeEvent
    public void onJump(LivingEvent.LivingJumpEvent event) {
        if (!(event.getEntityLiving() instanceof EntityPlayer)) return;

        EntityPlayer player = (EntityPlayer) event.getEntityLiving();
        if (isHandcuffed(player)) {
            player.addVelocity(0, 10, 0);
        }
    }

    @SubscribeEvent
    public void onPlayerTick(EntityJoinWorldEvent event) {
        if(!(event.getEntity() instanceof EntityPlayer)) return;

        EntityPlayer player = (EntityPlayer) event.getEntity();

        if (isHandcuffed(player)) {
            player.removePotionEffect(MobEffects.SPEED);
            player.removePotionEffect(MobEffects.JUMP_BOOST);
            player.addPotionEffect(new PotionEffect(MobEffects.SPEED, Integer.MAX_VALUE, -10, false, false));
            player.addPotionEffect(new PotionEffect(MobEffects.JUMP_BOOST, Integer.MAX_VALUE, -10, false, false));
        }
    }

}
