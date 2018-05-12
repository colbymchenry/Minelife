package com.minelife.police;

import com.minelife.Minelife;
import com.minelife.core.event.EntityDismountEvent;
import com.minelife.police.cop.EntityCop;
import com.minelife.util.StringHelper;
import com.minelife.util.client.PacketDropEntity;
import com.minelife.util.client.PacketRidingEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ArrestedEffect extends Potion {

    public static Potion INSTANCE = new ArrestedEffect(false, 0);

    private static final ResourceLocation icon = new ResourceLocation(Minelife.MOD_ID, "textures/gui/potion_xray.png");

    private ArrestedEffect(boolean isBadEffectIn, int liquidColorIn) {
        super(isBadEffectIn, liquidColorIn);
        setIconIndex(0, 0).setPotionName("potion.arrest");
        setRegistryName(Minelife.MOD_ID, "potion.arrest");
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
                e.getEntityLiving().removePotionEffect(MobEffects.JUMP_BOOST);
                e.getEntityLiving().removePotionEffect(MobEffects.SLOWNESS);
                ((EntityPlayer) e.getEntityLiving()).getFoodStats().setFoodLevel(10);
                e.getEntityLiving().removePotionEffect(this);
            } else {
                e.getEntityLiving().addPotionEffect(new PotionEffect(MobEffects.JUMP_BOOST, Integer.MAX_VALUE, -20, false, false));
                e.getEntityLiving().addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, Integer.MAX_VALUE, 20, false, false));
                ((EntityPlayer) e.getEntityLiving()).getFoodStats().setFoodLevel(2);
            }
        }
    }

    @SubscribeEvent
    public void onDismount(EntityDismountEvent event) {
        EntityPlayer player = (EntityPlayer) event.entity;

        // this is here in attempt to fix the chairs
        if (player.getRidingEntity().getClass().getSimpleName().contains("Sittable")) {
            Minelife.getNetwork().sendToAll(new PacketDropEntity(player.getEntityId()));
            return;
        }

        if (ItemHandcuff.isHandcuffed(player) || player.getRidingEntity() instanceof EntityCop) event.setCanceled(true);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onInteract(PlayerInteractEvent.EntityInteract event) {
        if (!(event.getTarget() instanceof EntityPlayer)) {
            return;
        }

        EntityPlayer handcuffing = event.getEntityPlayer();
        EntityPlayer beingHandcuffed = (EntityPlayer) event.getTarget();

        if (ItemHandcuff.isHandcuffed(beingHandcuffed) && handcuffing.getPassengers().isEmpty()) {
            if (handcuffing.isSneaking()) {
                beingHandcuffed.startRiding(handcuffing);
                handcuffing.sendMessage(new TextComponentString(StringHelper.ParseFormatting("&6&lType &c&l/drop &6&lto drop the player.", '&')));
                Minelife.getNetwork().sendToAll(new PacketRidingEntity(beingHandcuffed.getEntityId(), handcuffing.getEntityId()));
            } else {
                if (!ModPolice.isCop(handcuffing.getUniqueID()) && !ModPolice.isUnconscious(beingHandcuffed))
                    handcuffing.sendMessage(new TextComponentString(TextFormatting.RED + "Only the police can view a handcuffed player's inventory."));
                else
                    handcuffing.openGui(Minelife.getInstance(), GuiHandler.GUI_PLAYER_INVENTORY, handcuffing.getEntityWorld(), beingHandcuffed.getEntityId(), 0, 0);
            }
            return;
        }

        if (ModPolice.isUnconscious(beingHandcuffed)) {
            handcuffing.openGui(Minelife.getInstance(), GuiHandler.GUI_PLAYER_INVENTORY, handcuffing.getEntityWorld(), beingHandcuffed.getEntityId(), 0, 0);
            return;
        }

        if (handcuffing.getHeldItem(event.getHand()).getItem() != ModPolice.itemHandcuff) {
            return;
        }

        if (handcuffing.getDistance(beingHandcuffed) > 1) {
            return;
        }

        if (ItemHandcuff.isHandcuffed(beingHandcuffed)) {
            handcuffing.sendMessage(new TextComponentString(TextFormatting.RED + "That player is already handcuffed."));
            return;
        }

        if (ModPolice.isCop(handcuffing.getUniqueID())) {
            ItemHandcuff.setHandcuffed(beingHandcuffed, true, true);
        } else {
            if (beingHandcuffed.getHealth() < 5) {
                ItemHandcuff.setHandcuffed(beingHandcuffed, true, true);
            } else {
                handcuffing.sendMessage(new TextComponentString(TextFormatting.RED + "That player's health is too high to handcuff. Beat them down first."));
                return;
            }
        }

        ItemStack stack = handcuffing.getHeldItem(event.getHand());

        stack.shrink(1);

        if (stack.isEmpty()) handcuffing.inventory.deleteStack(stack);

        ItemStack key = new ItemStack(ModPolice.itemHandcuffKey);
        ItemHandcuffKey.setUUID(key, ItemHandcuff.getKeyUUID(beingHandcuffed), beingHandcuffed.getUniqueID());
        handcuffing.setHeldItem(event.getHand(), key);

        handcuffing.inventoryContainer.detectAndSendChanges();
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onInteract(PlayerInteractEvent event) {
        if (ItemHandcuff.isHandcuffed(event.getEntityPlayer())) event.setCanceled(true);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onEat(PlayerInteractEvent.RightClickItem event) {
        if (ItemHandcuff.isHandcuffed(event.getEntityPlayer())) event.setCanceled(true);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onFall(LivingFallEvent event) {
        if (event.getEntityLiving() instanceof EntityPlayer)
            if (ItemHandcuff.isHandcuffed((EntityPlayer) event.getEntityLiving())) event.setCanceled(true);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onHeal(LivingHealEvent event) {
        if (event.getEntityLiving() instanceof EntityPlayer)
            if (ItemHandcuff.isHandcuffed((EntityPlayer) event.getEntityLiving())) event.setCanceled(true);
    }

}
