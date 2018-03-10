package com.minelife;

import com.minelife.capes.ItemCape;
import com.minelife.clothing.ItemDesigner;
import com.minelife.drug.item.*;
import com.minelife.economy.ItemBagOCash;
import com.minelife.economy.ItemMoney;
import com.minelife.economy.ItemWallet;
import com.minelife.gun.item.ItemGunmetal;
import com.minelife.gun.item.ItemZincIngot;
import com.minelife.gun.item.ItemZincPlate;
import com.minelife.gun.item.ammos.*;
import com.minelife.gun.item.attachments.ItemSight;
import com.minelife.gun.item.guns.*;
import com.minelife.gun.item.parts.*;
import com.minelife.locks.ItemLockPick;
import com.minelife.police.ItemHandcuffs;
import com.minelife.police.ItemTicket;
import com.minelife.tutorial.ItemTutorialBook;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.Item;

import java.util.logging.Level;

public class MLItems {

    public static ItemAmmonia ammonia;
    public static ItemAbstractDrug calcium_hydroxide;
    public static ItemAbstractDrug calcium_oxide;
    public static ItemAbstractDrug cannabis_buds;
    public static ItemCannabisSeeds cannabis_seeds;
    public static ItemAbstractDrug cannabis_shredded;
    public static ItemCocaLeaf coca_leaf;
    public static ItemAbstractDrug coca_leaf_shredded;
    public static ItemAbstractDrug coca_paste;
    public static ItemGrinder grinder;
    public static ItemAbstractDrug lime;
    public static ItemLimeSeeds lime_seeds;
    public static ItemAbstractDrug potassium_hydroxide;
    public static ItemAbstractDrug potassium_manganate;
    public static ItemPotassiumPermanganate potassium_permanganate;
    public static ItemAbstractDrug pyrolusite;
    public static ItemAbstractDrug salt;
    public static ItemAbstractDrug sulfur;
    public static ItemSulfuricAcid sulfuric_acid;
    public static ItemCocaSeeds coca_seeds;
    public static ItemAbstractDrug potassium_hydroxide_pyrolusite_mixture;
    public static GunAK47 ak47;
    public static GunAWP awp;
    public static GunBarrett barrett;
    public static GunDesertEagle desert_eagle;
    public static GunM4A4 m4a4;
    public static GunMagnum magnum;
    public static ItemZincPlate zinc_plate;
    public static ItemGunmetal gunmetal;
    public static ItemZincIngot zinc_ingot;
    public static Ammo556 ammo_556;
    public static Ammo556Explosive ammo_556_explosive;
    public static Ammo556Incendiary ammo_556_incendiary;
    public static AmmoPistol ammo_pistol;
    public static AmmoPistolIncendiary ammo_pistol_incendiary;
    public static ItemPistolBarrel pistol_barrel;
    public static ItemPistolFrame pistol_frame;
    public static ItemRifleBarrel rifle_barrel;
    public static ItemRifleFrame rifle_frame;
    public static ItemRifleStock rifle_stock;
    public static ItemSniperBarrel sniper_barrel;
    public static ItemSniperFrame sniper_frame;
    public static ItemSniperScope sniper_scope;
    public static ItemTrigger trigger;
    public static ItemGrip grip;
    public static ItemAbstractDrug waxy_cocaine;
    public static ItemAbstractDrug heated_cocaine;
    public static ItemAbstractDrug pressed_cocaine;
    public static ItemAbstractDrug purple_cocaine;
    public static ItemProcessedCocaine processed_cocaine;
    public static ItemJoint joint;
    public static ItemDrugTest drug_test;
    public static ItemTicket ticket;
    public static ItemHandcuffs handcuffs;
    public static ItemSight holographicSight;
    public static ItemSight twoXSight;
    public static ItemSight acogSight;
    public static ItemSight reddotSight;
    public static ItemDesigner designer;
    public static ItemCape cape;
    public static ItemMoney dollar_1;
    public static ItemMoney dollar_5;
    public static ItemMoney dollar_10;
    public static ItemMoney dollar_20;
    public static ItemMoney dollar_50;
    public static ItemMoney dollar_100;
    public static ItemMoney dollar_250;
    public static ItemMoney dollar_500;
    public static ItemMoney dollar_1000;
    public static ItemWallet wallet;
    public static ItemBagOCash bagOCash;
    public static ItemTutorialBook tutorialBook;
    public static ItemLockPick lockPick;

    protected static void init()
    {
        register_item(ammonia = new ItemAmmonia());
        register_item(calcium_hydroxide = new ItemAbstractDrug("calcium_hydroxide"));
        register_item(calcium_oxide = new ItemAbstractDrug("calcium_oxide"));
        register_item(cannabis_buds = new ItemAbstractDrug("cannabis_buds"));
        register_item(cannabis_seeds = new ItemCannabisSeeds());
        register_item(cannabis_shredded = new ItemAbstractDrug("cannabis_shredded"));
        register_item(coca_leaf = new ItemCocaLeaf());
        register_item(coca_leaf_shredded = new ItemAbstractDrug("coca_leaf_shredded"));
        register_item(coca_paste = new ItemAbstractDrug("coca_paste"));
        register_item(coca_seeds = new ItemCocaSeeds());
        register_item(grinder = new ItemGrinder());
        register_item(lime = new ItemAbstractDrug("lime"));
        register_item(lime_seeds = new ItemLimeSeeds());
        register_item(potassium_hydroxide = new ItemAbstractDrug("potassium_hydroxide"));
        register_item(potassium_manganate = new ItemAbstractDrug("potassium_manganate"));
        register_item(potassium_permanganate = new ItemPotassiumPermanganate());
        register_item(pyrolusite = new ItemAbstractDrug("pyrolusite"));
        register_item(salt = new ItemAbstractDrug("salt"));
        register_item(sulfur = new ItemAbstractDrug("sulfur"));
        register_item(sulfuric_acid = new ItemSulfuricAcid());
        register_item(potassium_hydroxide_pyrolusite_mixture = new ItemAbstractDrug("potassium_hydroxide_pyrolusite_mixture"));
        register_item(ak47 = new GunAK47());
        register_item(awp = new GunAWP());
        register_item(barrett = new GunBarrett());
        register_item(desert_eagle = new GunDesertEagle());
        register_item(m4a4 = new GunM4A4());
        register_item(magnum = new GunMagnum());
        register_item(zinc_plate = new ItemZincPlate());
        register_item(zinc_ingot = new ItemZincIngot());
        register_item(gunmetal = new ItemGunmetal());
        register_item(ammo_556 = new Ammo556());
        register_item(ammo_556_explosive = new Ammo556Explosive());
        register_item(ammo_556_incendiary = new Ammo556Incendiary());
        register_item(ammo_pistol = new AmmoPistol());
        register_item(ammo_pistol_incendiary = new AmmoPistolIncendiary());
        register_item(grip = new ItemGrip());
        register_item(pistol_barrel = new ItemPistolBarrel());
        register_item(pistol_frame = new ItemPistolFrame());
        register_item(rifle_barrel = new ItemRifleBarrel());
        register_item(rifle_stock = new ItemRifleStock());
        register_item(rifle_frame = new ItemRifleFrame());
        register_item(sniper_barrel = new ItemSniperBarrel());
        register_item(sniper_scope = new ItemSniperScope());
        register_item(sniper_frame = new ItemSniperFrame());
        register_item(trigger = new ItemTrigger());
        register_item(waxy_cocaine = new ItemAbstractDrug("waxy_cocaine"));
        register_item(heated_cocaine = new ItemAbstractDrug("heated_cocaine"));
        register_item(pressed_cocaine = new ItemAbstractDrug("pressed_cocaine"));
        register_item(processed_cocaine = new ItemProcessedCocaine());
        register_item(purple_cocaine = new ItemAbstractDrug("purple_cocaine"));
        register_item(joint = new ItemJoint());
        register_item(drug_test = new ItemDrugTest());
        register_item(ticket = new ItemTicket());
        register_item(handcuffs = new ItemHandcuffs());
        register_item(holographicSight = new ItemSight("holographic_sight"));
        register_item(twoXSight = new ItemSight("2x_sight"));
        register_item(acogSight = new ItemSight("acog_sight"));
        register_item(reddotSight = new ItemSight("reddot_sight"));
        register_item(designer = new ItemDesigner());
        register_item(cape = new ItemCape());
        register_item(dollar_1 = new ItemMoney(1));
        register_item(dollar_5 = new ItemMoney(5));
        register_item(dollar_10 = new ItemMoney(10));
        register_item(dollar_20 = new ItemMoney(20));
        register_item(dollar_50 = new ItemMoney(50));
        register_item(dollar_100 = new ItemMoney(100));
        register_item(dollar_250 = new ItemMoney(250));
        register_item(dollar_500 = new ItemMoney(500));
        register_item(dollar_1000 = new ItemMoney(1000));
        register_item(wallet = new ItemWallet());
        register_item(bagOCash = new ItemBagOCash());
        register_item(tutorialBook = new ItemTutorialBook());
        register_item(lockPick = new ItemLockPick());
    }

    private static void register_item(Item item)
    {
        try {
            GameRegistry.registerItem(item, item.getUnlocalizedName());
        } catch (Exception e) {
            Minelife.getLogger().log(Level.SEVERE, "Failed to register item! " + item.getClass().getSimpleName() + "\nError Message: " + e.getMessage());
        }
    }

}
