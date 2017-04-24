package com.minelife.gun;

import com.minelife.Minelife;
import com.minelife.util.PlayerHelper;
import com.minelife.gun.client.RenderGun;
import com.minelife.gun.packet.PacketMouseClick;
import com.minelife.util.client.render.ModelBipedCustom;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
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

    @SideOnly(Side.CLIENT)
    public long nextFire;

    public ItemGun(FMLPreInitializationEvent event) {
        setUnlocalizedName(getClass().getSimpleName());
        setCreativeTab(ModGun.tabGuns);

        if (event.getSide() == Side.CLIENT) {
            MinecraftForgeClient.registerItemRenderer(this, new RenderGun(this));
            texture = new ResourceLocation(Minelife.MOD_ID, "textures/guns/" + getClass().getSimpleName() + ".png");
            objModelLocation = new ResourceLocation(Minelife.MOD_ID, "models/guns/" + getClass().getSimpleName() + ".obj");
            model = AdvancedModelLoader.loadModel(objModelLocation);
        }
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity holder, int arg, boolean inHand) {
        // return if we are on the server
        if (!world.isRemote) return;

        // return if we are not holding the gun
        if (!inHand) return;

        // call the fire() event if ready according to fire rate
        if (Mouse.isButtonDown(0) && System.currentTimeMillis() > nextFire) {
            nextFire = System.currentTimeMillis() + getFireRate();

            ItemStack ammo = getAmmo(stack);

            if (ammo == null) return;
            if (getSoundForShot(getAmmo(stack)) == null) return;

            if(ammo.stackSize == 0) return;

            NBTTagCompound tagCompound = stack.hasTagCompound() ? stack.getTagCompound() : stack.writeToNBT(new NBTTagCompound());
            tagCompound.setString("ammo", tagCompound.getString("ammo").split(",")[0] + "," + (ammo.stackSize - 1));
            stack.stackTagCompound = tagCompound;

            Minelife.NETWORK.sendToServer(new PacketMouseClick(false));

            this.fire(stack);
        }
    }

    public void shootBullet(EntityPlayer player, ItemStack gunStack) {
//        double distanceTraveled = (initialBulletVelocity * Math.cos(player.rotationPitch) / gravity);
//        distanceTraveled *= (initialBulletVelocity * Math.sin(player.rotationPitch)) + Math.sqrt((Math.pow(initialBulletVelocity * Math.sin(player.rotationPitch), 2)) + (2 * gravity * player.posY));
//        double timeTraveled = distanceTraveled / (initialBulletVelocity * Math.cos(player.rotationPitch));

        NBTTagCompound tagCompound = gunStack.hasTagCompound() ? gunStack.getTagCompound() : gunStack.writeToNBT(new NBTTagCompound());

        boolean fireRateCheck = System.currentTimeMillis() > (!tagCompound.hasKey("nextFire") ? 0 : tagCompound.getLong("nextFire"));

        if (!fireRateCheck) return;

        tagCompound.setLong("nextFire", System.currentTimeMillis() + getFireRate());
        gunStack.stackTagCompound = tagCompound;

        ItemStack ammo = getAmmo(gunStack);

        if (ammo == null) return;

        if (getSoundForShot(getAmmo(gunStack)) == null) return;

        if(ammo.stackSize == 0) return;

        tagCompound.setString("ammo", tagCompound.getString("ammo").split(",")[0] + "," + (ammo.stackSize - 1));
        gunStack.stackTagCompound = tagCompound;

        player.worldObj.playSoundToNearExcept(player, Minelife.MOD_ID + ":" + getSoundForShot(getAmmo(gunStack)), 0.5F, 1.0F);

        EntityLivingBase target = PlayerHelper.getTargetEntity(player, 50);

        if (target != null) {
            MinecraftForge.EVENT_BUS.post(new EntityShotEvent(player, target, player.getHeldItem()));
        }
    }

    // prevent the gun from swinging
    @Override
    public boolean onEntitySwing(EntityLivingBase entityLiving, ItemStack stack) {
        return true;
    }

    // prevent the gun from swinging
    @Override
    public EnumAction getItemUseAction(ItemStack par1ItemStack) {
        return EnumAction.none;
    }

    // this is called on the client to do animations
    @SideOnly(Side.CLIENT)
    public abstract void fire(ItemStack gunStack);

    // the sound for the certain bullet shot
    public abstract String getSoundForShot(ItemStack ammo);

    // the maximum amount of bullet this gun can hold
    public abstract int getClipSize();

    // the fire rate of the gun
    public abstract long getFireRate();

    // a list of ammo that this gun can accept
    public abstract List<ItemAmmo> validAmmo();

    @SideOnly(Side.CLIENT)
    public abstract boolean handleRenderType(ItemStack item, IItemRenderer.ItemRenderType type);

    @SideOnly(Side.CLIENT)
    public abstract boolean shouldUseRenderHelper(IItemRenderer.ItemRenderType type, ItemStack item, IItemRenderer.ItemRendererHelper helper);

    @SideOnly(Side.CLIENT)
    public abstract void renderItem(IItemRenderer.ItemRenderType type, ItemStack item, Object... data);

    public abstract void setArmRotations(ModelBipedCustom model, float f1);

    /*
    ---------------------------- STATIC METHODS -------------------------------------
     */

    public static ItemStack getAmmo(ItemStack itemStack) {
        if (!(itemStack.getItem() instanceof ItemGun)) return null;

        if (!itemStack.hasTagCompound()) return null;

        NBTTagCompound tagCompound = itemStack.getTagCompound();

        if (!tagCompound.hasKey("ammo")) return null;

        String[] data = tagCompound.getString("ammo").split(",");

        return new ItemStack(Item.getItemById(Integer.parseInt(data[0])), Integer.parseInt(data[1]));
    }

    public static void reload(EntityPlayer player, ItemStack stackGun) {
        if (!(stackGun.getItem() instanceof ItemGun)) return;

        ItemGun itemGun = (ItemGun) stackGun.getItem();

        ItemStack stackAmmo = getAmmoFromInventory(player, stackGun);

        if (stackAmmo == null) return;

        ItemAmmo itemAmmo = (ItemAmmo) stackAmmo.getItem();

        NBTTagCompound tagCompound = stackGun.hasTagCompound() ? stackGun.getTagCompound() : stackGun.writeToNBT(new NBTTagCompound());

        ItemStack currentAmmo = getAmmo(stackGun);

        int ammoAmount = 0;

        while (ammoAmount < itemGun.getClipSize() && stackAmmo != null) {
            // add current ammo
            if (currentAmmo != null) ammoAmount += currentAmmo.stackSize;

            int difference = itemGun.getClipSize() - ammoAmount;

            if (stackAmmo.stackSize > difference) {
                stackAmmo.stackSize -= difference;
            } else {
                difference = stackAmmo.stackSize;

                // remove the ammo stack from the players inventory
                for (int i = 0; i < player.inventory.mainInventory.length; i++) {
                    if (ItemStack.areItemStacksEqual(player.inventory.mainInventory[i], stackAmmo)) {
                        player.inventory.mainInventory[i] = null;
                        break;
                    }
                }
            }

            ammoAmount += difference;

            stackAmmo = getAmmoFromInventory(player, stackGun);
        }

        tagCompound.setString("ammo", Item.getIdFromItem(itemAmmo) + "," + ammoAmount);

        stackGun.stackTagCompound = tagCompound;
    }

    public static ItemStack getAmmoFromInventory(EntityPlayer player, ItemStack gun) {
        if (!(gun.getItem() instanceof ItemGun)) return null;

        ItemGun itemGun = (ItemGun) gun.getItem();

        for (ItemStack itemStack : player.inventory.mainInventory) {
            if (itemStack != null) {
                if (itemStack.getItem() instanceof ItemAmmo) {
                    if (itemGun.validAmmo().contains(itemStack.getItem())) {
                        return itemStack;
                    }
                }
            }
        }

        return null;
    }

}
