package com.minelife.guns.item;

import com.google.common.collect.Lists;
import com.minelife.Minelife;
import com.minelife.essentials.ModEssentials;
import com.minelife.permission.ModPermission;
import com.minelife.util.PlayerHelper;
import com.minelife.util.StringHelper;
import com.minelife.util.fireworks.Color;
import com.minelife.util.fireworks.FireworkBuilder;
import com.minelife.util.irds.IRDSObject;
import com.minelife.util.irds.IRDSTable;
import com.minelife.util.irds.IRDSValue;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import org.apache.commons.lang3.text.WordUtils;

import java.util.List;

public class ItemGunSkinUnlocker extends Item {

    public ItemGunSkinUnlocker() {
        setRegistryName(Minelife.MOD_ID, "gunSkinUnlocker");
        setUnlocalizedName(Minelife.MOD_ID + ":gunSkinUnlocker");
        setMaxStackSize(2);
        setCreativeTab(CreativeTabs.MISC);
    }

    public void registerModel() {
        ModelResourceLocation itemModelResourceLocation = new ModelResourceLocation(Minelife.MOD_ID + ":gunskinunlocker", "inventory");
        ModelLoader.setCustomModelResourceLocation(this, 0, itemModelResourceLocation);
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        return TextFormatting.DARK_RED.toString() + TextFormatting.BOLD.toString() + "Gun Skin Unlock!";
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        if(worldIn.isRemote) {
            return super.onItemRightClick(worldIn, playerIn, handIn);
        }

        SkinUnlockerTable unlockerTable = new SkinUnlockerTable();
        GunSkinRDS gunUnlocked = ((GunSkinRDS)unlockerTable.getResult().get(0));

        if(gunUnlocked.getProbability() < 40) {
            ItemStack fireworkStack = FireworkBuilder.builder().addExplosion(true, true, FireworkBuilder.Type.LARGE_BALL,
                    new int[]{Color.RED.asRGB(), Color.YELLOW.asRGB()}, new int[]{Color.ORANGE.asRGB(), Color.WHITE.asRGB()}).getStack(1);

            EntityFireworkRocket ent = new EntityFireworkRocket(playerIn.getEntityWorld(), playerIn.posX, playerIn.posY + 2, playerIn.posZ, fireworkStack);
            playerIn.getEntityWorld().spawnEntity(ent);

            PlayerHelper.sendMessageToAll(StringHelper.ParseFormatting("&9&l" + playerIn.getName() + " &6&lunlocked the &c&l" + WordUtils.capitalizeFully(gunUnlocked.gunSkin.name().replace("_", " ")) + " &6&lskin!", '&'));
            ModPermission.addPlayerPermission(playerIn.getUniqueID(), "gun.skin." + gunUnlocked.gunSkin.name().toLowerCase());
        } else {
            playerIn.sendMessage(new TextComponentString(StringHelper.ParseFormatting("&6&lYou got nothing good...", '&')));
        }

        if(playerIn.getHeldItem(handIn).getCount() == 1) {
            playerIn.setHeldItem(handIn, ItemStack.EMPTY);
        } else {
            ItemStack stack = playerIn.getHeldItem(handIn);
            stack.setCount(playerIn.getHeldItem(handIn).getCount() - 1);
            playerIn.setHeldItem(handIn, stack);
        }

        playerIn.inventoryContainer.detectAndSendChanges();

        return super.onItemRightClick(worldIn, playerIn, handIn);
    }

    static class SkinUnlockerTable extends IRDSTable {

        static List<GunSkinRDS> skinUnlocks = Lists.newArrayList();

        static {
//            skinUnlocks.add(new GunSkinRDS(40, EnumGun.M4A4));
//            skinUnlocks.add(new GunSkinRDS(40, EnumGun.DESERT_EAGLE));
//            skinUnlocks.add(new GunSkinRDS(40, EnumGun.AK47));
//            skinUnlocks.add(new GunSkinRDS(40, EnumGun.BARRETT));
//            skinUnlocks.add(new GunSkinRDS(40, EnumGun.MAGNUM));
//            skinUnlocks.add(new GunSkinRDS(40, EnumGun.AWP));
            skinUnlocks.add(new GunSkinRDS(10, EnumGun.AK47_BLOODBATH));
            skinUnlocks.add(new GunSkinRDS(2, EnumGun.AK47_BLUE_STEEL));
            skinUnlocks.add(new GunSkinRDS(1, EnumGun.AK47_GOLD));
            skinUnlocks.add(new GunSkinRDS(1, EnumGun.AK47_SPEC_OPS));
            skinUnlocks.add(new GunSkinRDS(20, EnumGun.M4A4_BUMBLEBEE));
            skinUnlocks.add(new GunSkinRDS(20, EnumGun.M4A4_PINEAPPLE));
            skinUnlocks.add(new GunSkinRDS(10, EnumGun.M4A4_BLACK_MESA));
            skinUnlocks.add(new GunSkinRDS(2, EnumGun.AWP_GHOST));
            skinUnlocks.add(new GunSkinRDS(1, EnumGun.AWP_SPEC_OPS));
            skinUnlocks.add(new GunSkinRDS(10, EnumGun.BARRETT_HOT_ROD));
            skinUnlocks.add(new GunSkinRDS(15, EnumGun.BARRETT_PINEAPPLE));
            skinUnlocks.add(new GunSkinRDS(1.5, EnumGun.DESERT_EAGLE_SHOCK));
            skinUnlocks.add(new GunSkinRDS(5, EnumGun.DESERT_EAGLE_BLACK_MESA));
            skinUnlocks.add(new GunSkinRDS(10, EnumGun.DESERT_EAGLE_PINEAPPLE));
            skinUnlocks.add(new GunSkinRDS(6, EnumGun.MAGNUM_BLACK_MESA));
            skinUnlocks.add(new GunSkinRDS(5, EnumGun.MAGNUM_KINGPIN));
            skinUnlocks.add(new GunSkinRDS(1, EnumGun.MAGNUM_SPEC_OPS));
            skinUnlocks.add(new GunSkinRDS(20, EnumGun.MAGNUM_RUST_COAT));
        }

        @Override
        public int getCount() {
            return 1;
        }

        @Override
        public List<IRDSObject> getContents() {
            return ((List<IRDSObject>)(List<?>) skinUnlocks);
        }

        @Override
        public List<IRDSObject> getResult() {
            return getResultDefault();
        }

        @Override
        public double getProbability() {
            return 0;
        }

        @Override
        public boolean isUnique() {
            return false;
        }

        @Override
        public boolean dropsAlways() {
            return false;
        }

        @Override
        public boolean canDrop() {
            return false;
        }

        @Override
        public void preResultEvaluation() {

        }

        @Override
        public void onHit() {

        }

        @Override
        public void postResultEvaluation() {

        }
    }

    static class GunSkinRDS implements IRDSObject, IRDSValue<EnumGun> {

        private double probability;
        private EnumGun gunSkin;

        public GunSkinRDS(double probability, EnumGun gunSkin) {
            this.probability = probability;
            this.gunSkin = gunSkin;
        }

        @Override
        public double getProbability() {
            return probability;
        }

        @Override
        public boolean isUnique() {
            return true;
        }

        @Override
        public boolean dropsAlways() {
            return false;
        }

        @Override
        public boolean canDrop() {
            return true;
        }

        @Override
        public void preResultEvaluation() {

        }

        @Override
        public void onHit() {

        }

        @Override
        public void postResultEvaluation() {

        }

        @Override
        public EnumGun getValue() {
            return gunSkin;
        }
    }
}
