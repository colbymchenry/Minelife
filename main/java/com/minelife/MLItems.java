package com.minelife;

import com.minelife.drug.item.*;
import com.minelife.gun.item.ItemGunmetal;
import com.minelife.gun.item.ItemZincIngot;
import com.minelife.gun.item.ItemZincPlate;
import com.minelife.gun.item.ammos.*;
import com.minelife.gun.item.guns.*;
import com.minelife.gun.item.parts.*;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.Item;

import java.util.logging.Level;

public class MLItems {

    public static ItemAmmonia ammonia;
    public static ItemCalciumHydroxide calcium_hydroxide;
    public static ItemCalciumOxide calcium_oxide;
    public static ItemCannabisBuds cannabis_buds;
    public static ItemCannabisSeeds cannabis_seeds;
    public static ItemCannabisShredded cannabis_shredded;
    public static ItemCocaineSulfate cocaine_sulfate;
    public static ItemCocaLeaf coca_leaf;
    public static ItemCocaLeafShredded coca_leaf_shredded;
    public static ItemCocaPaste coca_paste;
    public static ItemGrinder grinder;
    public static ItemLime lime;
    public static ItemLimeSeeds lime_seeds;
    public static ItemPotassiumHydroxide potassium_hydroxide;
    public static ItemPotassiumManganate potassium_manganate;
    public static ItemPotassiumPermanganate potassium_permanganate;
    public static ItemPyrolusite pyrolusite;
    public static ItemSalt salt;
    public static ItemSulfur sulfur;
    public static ItemSulfuricAcid sulfuric_acid;
    public static ItemCocaSeeds coca_seeds;
    public static ItemPotassiumHydroxidePyrolusiteMixture potassium_hydroxide_pyrolusite_mixture;
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

    protected static void init()
    {
        register_item(ammonia = new ItemAmmonia());
        register_item(calcium_hydroxide = new ItemCalciumHydroxide());
        register_item(calcium_oxide = new ItemCalciumOxide());
        register_item(cannabis_buds = new ItemCannabisBuds());
        register_item(cannabis_seeds = new ItemCannabisSeeds());
        register_item(cannabis_shredded = new ItemCannabisShredded());
        register_item(cocaine_sulfate = new ItemCocaineSulfate());
        register_item(coca_leaf = new ItemCocaLeaf());
        register_item(coca_leaf_shredded = new ItemCocaLeafShredded());
        register_item(coca_paste = new ItemCocaPaste());
        register_item(coca_seeds = new ItemCocaSeeds());
        register_item(grinder = new ItemGrinder());
        register_item(lime = new ItemLime());
        register_item(lime_seeds = new ItemLimeSeeds());
        register_item(potassium_hydroxide = new ItemPotassiumHydroxide());
        register_item(potassium_manganate = new ItemPotassiumManganate());
        register_item(potassium_permanganate = new ItemPotassiumPermanganate());
        register_item(pyrolusite = new ItemPyrolusite());
        register_item(salt = new ItemSalt());
        register_item(sulfur = new ItemSulfur());
        register_item(sulfuric_acid = new ItemSulfuricAcid());
        register_item(potassium_hydroxide_pyrolusite_mixture = new ItemPotassiumHydroxidePyrolusiteMixture());
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
    }

    private static void register_item(Item item)
    {
        try {
            GameRegistry.registerItem(item, item.getUnlocalizedName());
            System.out.println(item.getUnlocalizedName() + " registered!");
        } catch (Exception e) {
            Minelife.getLogger().log(Level.SEVERE, "Failed to register item! " + item.getClass().getSimpleName() + "\nError Message: " + e.getMessage());
        }
    }

}
