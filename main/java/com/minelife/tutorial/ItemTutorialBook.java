package com.minelife.tutorial;

import com.google.common.collect.Sets;
import com.minelife.Minelife;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
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
        setUnlocalizedName("tutorialBook");
        setTextureName(Minelife.MOD_ID + ":tutorialBook");
        setCreativeTab(CreativeTabs.tabMisc);
    }

    @SideOnly(Side.SERVER)
    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        Set<Section> sectionSet = Sets.newTreeSet();
        for (File sectionFile : ModTutorial.getSections(new File(Minelife.getConfigDirectory(), "tutorials"))) {
            Section section = new Section(sectionFile.getName().split("\\.")[0]);
            for (File pageFile : ModTutorial.getPages(sectionFile)) {
                Page page = new Page();
                try {
                    Scanner scanner = new Scanner(pageFile);
                    while(scanner.hasNextLine()) page.lines.add(scanner.nextLine());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }

            sectionSet.add(section);
        }

        Minelife.NETWORK.sendTo(new PacketSendTutorials(sectionSet), (EntityPlayerMP) player);
        return super.onItemRightClick(stack, world, player);
    }
}
