package com.minelife.tutorial;

import com.google.common.collect.Sets;
import com.minelife.Minelife;
import com.minelife.guns.item.ItemGunPart;
import com.minelife.util.NumberConversions;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Set;

public class ItemTutorialBook extends Item {

    public ItemTutorialBook() {
        setUnlocalizedName(Minelife.MOD_ID + ".tutorialBook");
        setRegistryName("tutorialBook");
        setCreativeTab(CreativeTabs.MISC);
    }

    @SideOnly(Side.SERVER)
    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
        Set<Section> sectionSet = Sets.newTreeSet();
        for (File sectionFile : ModTutorial.getSections(new File(Minelife.getDirectory(), "tutorials"))) {
            Section section = new Section(sectionFile.getName().split("\\.")[0]);
            for (File pageFile : ModTutorial.getPages(sectionFile)) {
                Page page = new Page(NumberConversions.toInt(pageFile.getName().split("\\.")[0]));
                try {
                    Scanner scanner = new Scanner(pageFile);
                    while(scanner.hasNextLine()) page.lines.add(scanner.nextLine());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                section.pages.add(page);
            }

            sectionSet.add(section);
        }

        Minelife.getNetwork().sendTo(new PacketSendTutorials(sectionSet), (EntityPlayerMP) playerIn);
        return super.onItemRightClick(worldIn, playerIn, handIn);
    }

    @SideOnly(Side.CLIENT)
    public void registerModel() {
        ModelResourceLocation itemModelResourceLocation = new ModelResourceLocation(Minelife.MOD_ID + ":tutorial_book", "inventory");
        ModelLoader.setCustomModelResourceLocation(this, 0, itemModelResourceLocation);
    }

}
