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

public class MinelifeItems {

    public ItemAmmonia ammonia;
    public ItemCalciumHydroxide calcium_hydroxide;
    public ItemCalciumOxide calcium_oxide;
    public ItemCannabisBuds cannabis_buds;
    public ItemCannabisSeeds cannabis_seeds;
    public ItemCannabisShredded cannabis_shredded;
    public ItemCocaineSulfate cocaine_sulfate;
    public ItemCocaLeaf coca_leaf;
    public ItemCocaLeafShredded coca_leaf_shredded;
    public ItemCocaPaste coca_paste;
    public ItemGrinder grinder;
    public ItemLime lime;
    public ItemLimeSeeds lime_seeds;
    public ItemPotassiumHydroxide potassium_hydroxide;
    public ItemPotassiumManganate potassium_manganate;
    public ItemPotassiumPermanganate potassium_permanganate;
    public ItemPyrolusite pyrolusite;
    public ItemSalt salt;
    public ItemSulfur sulfur;
    public ItemSulfuricAcid sulfuric_acid;
    public ItemCocaSeeds coca_seeds;
    public ItemPotassiumHydroxidePyrolusiteMixture potassium_hydroxide_pyrolusite_mixture;
    public GunAK47 ak47;
    public GunAWP awp;
    public GunBarrett barrett;
    public GunDesertEagle desert_eagle;
    public GunM4A4 m4a4;
    public GunMagnum magnum;
    public ItemZincPlate zinc_plate;
    public ItemGunmetal gunmetal;
    public ItemZincIngot zinc_ingot;
    public Ammo556 ammo_556;
    public Ammo556Explosive ammo_556_explosive;
    public Ammo556Incendiary ammo_556_incendiary;
    public AmmoPistol ammo_pistol;
    public AmmoPistolIncendiary ammo_pistol_incendiary;
    public ItemPistolBarrel pistol_barrel;
    public ItemPistolFrame pistol_frame;
    public ItemRifleBarrel rifle_barrel;
    public ItemRifleFrame rifle_frame;
    public ItemRifleStock rifle_stock;
    public ItemSniperBarrel sniper_barrel;
    public ItemSniperFrame sniper_frame;
    public ItemSniperScope sniper_scope;
    public ItemTrigger trigger;
    public ItemGrip grip;

    protected void init()
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

    private void register_item(Item item)
    {
        try {
            GameRegistry.registerItem(item, item.getUnlocalizedName());
            System.out.println(item.getUnlocalizedName() + " registered!");
        } catch (Exception e) {
            Minelife.getLogger().log(Level.SEVERE, "Failed to register item! " + item.getClass().getSimpleName() + "\nError Message: " + e.getMessage());
        }
    }

}
