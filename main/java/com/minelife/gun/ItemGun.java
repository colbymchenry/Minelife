package com.minelife.gun;

import com.google.common.collect.Lists;
import com.minelife.Minelife;
import com.minelife.PlayerHelper;
import com.minelife.gun.client.RenderGun;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.input.Mouse;

import java.util.List;

public abstract class ItemGun extends Item {

    public ResourceLocation texture;
    public ResourceLocation objModelLocation;
    public IModelCustom model;
    public String name;
    public long fireRate;

    @SideOnly(Side.CLIENT)
    public long nextFire;

    public ItemGun(String name, long fireRate, FMLPreInitializationEvent event) {
        this.name = name;
        this.fireRate = fireRate;
        setUnlocalizedName(name);
        setCreativeTab(ModGun.tabGuns);

        if (event.getSide() == Side.CLIENT) {
            MinecraftForgeClient.registerItemRenderer(this, new RenderGun(this));
            texture = new ResourceLocation(Minelife.MOD_ID, "textures/guns/" + name + ".png");
            objModelLocation = new ResourceLocation(Minelife.MOD_ID, "models/guns/" + name + ".obj");
            model = AdvancedModelLoader.loadModel(objModelLocation);
        }
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity holder, int arg, boolean inHand) {
        // return if we are on the server
        if (!world.isRemote) return;
        if (!inHand) return;

        if (Mouse.isButtonDown(0) && System.currentTimeMillis() > nextFire) {
            nextFire = System.currentTimeMillis() + fireRate;
            Minecraft.getMinecraft().thePlayer.playSound(Minelife.MOD_ID + ":" + getSoundName(getPossibleAmmo()[0]), 0.5F, 1.0F);
            this.fire();
        }
    }

    @SideOnly(Side.SERVER)
    public void shootBullet(EntityPlayerMP player, ItemStack itemStack) {
//        double distanceTraveled = (initialBulletVelocity * Math.cos(player.rotationPitch) / gravity);
//        distanceTraveled *= (initialBulletVelocity * Math.sin(player.rotationPitch)) + Math.sqrt((Math.pow(initialBulletVelocity * Math.sin(player.rotationPitch), 2)) + (2 * gravity * player.posY));
//        double timeTraveled = distanceTraveled / (initialBulletVelocity * Math.cos(player.rotationPitch));

        NBTTagCompound tagCompound = itemStack.writeToNBT(new NBTTagCompound());

        boolean fireRateCheck = System.currentTimeMillis() > (!tagCompound.hasKey("nextFire") ? 0 : tagCompound.getLong("nextFire"));

        if (!fireRateCheck) return;

        tagCompound.setLong("nextFire", System.currentTimeMillis() + fireRate);
        itemStack.readFromNBT(tagCompound);

        player.worldObj.playSoundToNearExcept(player, Minelife.MOD_ID + ":" + getSoundName(getPossibleAmmo()[0]), 0.5F, 1.0F);

        // TODO: Check if block is in the way
        // TODO: Check if method gets nearest entity or furthest first
        EntityLivingBase target = PlayerHelper.getTargetEntity(player, 50);

        if (target != null)
            MinecraftForge.EVENT_BUS.post(new EntityShotEvent(player, target, player.getHeldItem()));
    }

    public static ItemStack getAmmo(ItemStack itemStack) {
        if(!(itemStack.getItem() instanceof ItemGun)) return null;

        if(!itemStack.hasTagCompound()) return null;

        NBTTagCompound tagCompound = itemStack.getTagCompound();

        if(!tagCompound.hasKey("ammo")) return null;

        String[] data = tagCompound.getString("ammo").split(",");

        return new ItemStack(Item.getItemById(Integer.parseInt(data[0])), Integer.parseInt(data[1]));
    }

    @SideOnly(Side.SERVER)
    public static void reload(EntityPlayerMP player, ItemStack stackGun) {
        if(!(stackGun.getItem() instanceof ItemGun)) return;

        ItemGun itemGun = (ItemGun) stackGun.getItem();

        ItemStack stackAmmo = getAmmoFromInventory(player, stackGun);

        if(stackAmmo == null) return;

        ItemAmmo itemAmmo = (ItemAmmo) stackAmmo.getItem();

        NBTTagCompound tagCompound = stackGun.hasTagCompound() ? stackGun.getTagCompound() : stackGun.writeToNBT(new NBTTagCompound());

        ItemStack currentAmmo = getAmmo(stackGun);

        int ammoAmount = 0;

        while(ammoAmount < itemGun.getClipSize() && stackAmmo != null) {
            // add current ammo
            if (currentAmmo != null) ammoAmount += currentAmmo.stackSize;

            int difference = itemGun.getClipSize() - ammoAmount;

            if(stackAmmo.stackSize > difference) {
                stackAmmo.stackSize -= difference;
            } else {
                difference = stackAmmo.stackSize;
                // TODO: Remove from stack from inventory
            }

            ammoAmount += difference;

            stackAmmo = getAmmoFromInventory(player, stackGun);
        }

        tagCompound.setString("ammo", Item.getIdFromItem(itemAmmo) + "," + ammoAmount);

        stackGun.readFromNBT(tagCompound);
    }

    @SideOnly(Side.SERVER)
    public static ItemStack getAmmoFromInventory(EntityPlayerMP player, ItemStack gun) {
        if(!(gun.getItem() instanceof ItemGun)) return null;

        ItemGun itemGun = (ItemGun) gun.getItem();

        for (ItemStack itemStack : player.inventory.mainInventory) {
            if(itemStack != null) {
                if(itemStack.getItem() instanceof ItemAmmo) {
                    if(itemGun.validAmmo().contains(itemStack.getItem())) {
                        return itemStack;
                    }
                }
            }
        }

        return null;
    }

    @Override
    public boolean onEntitySwing(EntityLivingBase entityLiving, ItemStack stack) {
        return true;
    }

    @Override
    public EnumAction getItemUseAction(ItemStack par1ItemStack) {
        return EnumAction.none;
    }

    public abstract boolean handleRenderType(ItemStack item, IItemRenderer.ItemRenderType type);

    public abstract boolean shouldUseRenderHelper(IItemRenderer.ItemRenderType type, ItemStack item, IItemRenderer.ItemRendererHelper helper);

    public abstract void renderItem(IItemRenderer.ItemRenderType type, ItemStack item, Object... data);

    public abstract void fire();

    public abstract void reload(ItemStack ammo);

    public abstract String getSoundForShot(ItemStack ammo);

    public abstract int getClipSize();

    public abstract List<ItemAmmo> validAmmo();

}
