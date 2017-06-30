package com.minelife.gun.client.guns;

import com.minelife.Minelife;
import com.minelife.gun.item.guns.ItemGun;
import com.minelife.gun.packet.PacketMouseClick;
import com.minelife.util.PlayerHelper;
import com.minelife.util.client.Animation;
import com.minelife.util.client.render.ModelBipedCustom;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;
import org.lwjgl.input.Mouse;

@SideOnly(Side.CLIENT)
public abstract class ItemGunClient {

    public static GunClientAK47 ak47 = new GunClientAK47(ItemGun.ak47);
    public static GunClientAWP awp = new GunClientAWP(ItemGun.awp);
    public static GunClientBarrett barrett = new GunClientBarrett(ItemGun.barrett);
    public static GunClientDesertEagle desertEagle = new GunClientDesertEagle(ItemGun.desertEagle);
    public static GunClientM4A4 m4A4 = new GunClientM4A4(ItemGun.m4A4);
    public static GunClientMagnum magnum = new GunClientMagnum(ItemGun.magnum);

    private final ResourceLocation texture, objModel;
    private final IModelCustom model;
    private final ItemGun gun;
    private Animation animation;
    private boolean mouseDown = false;

    private long nextFire;

    public ItemGunClient(ItemGun gun) {
        this.gun = gun;
        texture = new ResourceLocation(Minelife.MOD_ID, "textures/guns/" + gun.getName() + ".png");
        objModel = new ResourceLocation(Minelife.MOD_ID, "models/guns/" + gun.getName() + ".obj");
        model = AdvancedModelLoader.loadModel(objModel);
    }

    public abstract boolean handleRenderType(ItemStack item, IItemRenderer.ItemRenderType type);

    public abstract boolean shouldUseRenderHelper(IItemRenderer.ItemRenderType type, ItemStack item, IItemRenderer.ItemRendererHelper helper);

    public abstract void renderItem(IItemRenderer.ItemRenderType type, ItemStack item, Object... data);

    public abstract void setArmRotations(ModelBipedCustom model, float f1);

    public abstract void shootBullet();

    public void onUpdate(ItemStack stack, World world, EntityPlayer holder, int slot, boolean inHand) {
        if (!inHand) return;

        if (ItemGun.getCurrentClipHoldings(stack) < 1) return;

        if(Minecraft.getMinecraft().currentScreen != null) return;

        if (Mouse.isButtonDown(0)) {
            if (!gun.isFullAuto()) {
                if (System.currentTimeMillis() > nextFire) {
                    if (!mouseDown) {
                        nextFire = System.currentTimeMillis() + gun.getFireRate();
                        Minelife.NETWORK.sendToServer(new PacketMouseClick(false));
                        shootBullet();
                        Minecraft.getMinecraft().thePlayer.playSound(Minelife.MOD_ID + ":guns." + gun.getName() + ".shot", 5F, 1.0F);
                        PlayerHelper.getTarget(holder, 11);
                    }
                }
            } else {
                if (System.currentTimeMillis() > nextFire) {
                    nextFire = System.currentTimeMillis() + gun.getFireRate();
                    Minelife.NETWORK.sendToServer(new PacketMouseClick(false));
                    shootBullet();
                    Minecraft.getMinecraft().thePlayer.playSound(Minelife.MOD_ID + ":guns." + gun.getName() + ".shot", 5F, 1.0F);
                    PlayerHelper.getTarget(holder, 11);
                }
            }

            mouseDown = true;
        } else {
            mouseDown = false;
        }
    }

    public IModelCustom getModel() {
        return model;
    }

    public ResourceLocation getTexture() {
        return texture;
    }

    public ResourceLocation getObjModel() {
        return objModel;
    }

    public Animation getAnimation() {
        if (animation == null) setAnimation(new Animation(0, 0, 0));
        return animation;
    }

    public void setAnimation(Animation animation) {
        this.animation = animation;
    }

    public ItemGun getGun() {
        return gun;
    }
}