package com.minelife.police;

import com.minelife.Minelife;
import com.minelife.util.server.NameFetcher;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Objects;
import java.util.UUID;

public class ItemHandcuffKey extends Item {

    public ItemHandcuffKey() {
        setRegistryName(Minelife.MOD_ID, "handcuff_key");
        setUnlocalizedName(Minelife.MOD_ID + ":handcuff_key");
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        return stack.hasTagCompound() && stack.getTagCompound().hasKey("PlayerID") ? "Key's for " + NameFetcher.asyncFetchClient(UUID.fromString(stack.getTagCompound().getString("PlayerID"))) : "Unbound Keys";
    }

    public void registerModel() {
        ModelResourceLocation itemModelResourceLocation = new ModelResourceLocation(Minelife.MOD_ID + ":handcuff_key", "inventory");
        ModelLoader.setCustomModelResourceLocation(this, 0, itemModelResourceLocation);
    }

    public static void setUUID(ItemStack stack, UUID id, UUID player) {
        NBTTagCompound tagCompound = stack.hasTagCompound() ? stack.getTagCompound() : new NBTTagCompound();
        tagCompound.setString("HandcuffID", id.toString());
        tagCompound.setString("PlayerID", player.toString());
        tagCompound.setLong("Created", System.currentTimeMillis() + 1000L);
        stack.setTagCompound(tagCompound);
    }

    public static UUID getUUID(ItemStack stack) {
        if(!stack.hasTagCompound()) return null;
        if(!stack.getTagCompound().hasKey("HandcuffID")) return null;
        return UUID.fromString(stack.getTagCompound().getString("HandcuffID"));
    }

    @SubscribeEvent
    public void onInteract(PlayerInteractEvent.EntityInteract event) {
        if(!(event.getEntity() instanceof EntityPlayer)) return;
        if(!(event.getTarget() instanceof EntityPlayer)) return;

        EntityPlayer uncuffing = event.getEntityPlayer();
        EntityPlayer beingUncuffed = (EntityPlayer) event.getTarget();

        if(uncuffing.getHeldItem(event.getHand()).getItem() != this) return;

        if(!ItemHandcuff.isHandcuffed(beingUncuffed)) {
            uncuffing.sendMessage(new TextComponentString(TextFormatting.RED + "That player is not handcuffed."));
            return;
        }

        if(!Objects.equals(ItemHandcuff.getKeyUUID(beingUncuffed), getUUID(uncuffing.getHeldItem(event.getHand())))) {
            uncuffing.sendMessage(new TextComponentString(TextFormatting.RED + "That is not the key for the cuffs on this player."));
            return;
        }

        if(event.getEntityPlayer().getHeldItem(event.getHand()).getTagCompound().getLong("Created") > System.currentTimeMillis()) return;

        ItemHandcuff.setHandcuffed(beingUncuffed, false, false);

        uncuffing.setHeldItem(event.getHand(), new ItemStack(ModPolice.itemHandcuff));
        uncuffing.inventoryContainer.detectAndSendChanges();
    }

}
