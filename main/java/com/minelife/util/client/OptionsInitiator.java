package com.minelife.util.client;

import com.google.common.collect.Lists;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

public class OptionsInitiator {

    public static void init() throws IOException {
        List<String> lines = Lists.newArrayList();

        File optionsFile = new File(System.getProperty("user.dir"), "options.txt");
        File completeFile = new File(System.getProperty("user.dir"), "completeFile");
        if (!optionsFile.exists()) {
            optionsFile.createNewFile();
        } else {
            Scanner scanner = new Scanner(optionsFile);
            while (scanner.hasNextLine()) {
                lines.add(scanner.nextLine());
            }
        }

        if (completeFile.exists()) return;

        completeFile.createNewFile();

        Iterator<String> iterator = lines.iterator();
        while(iterator.hasNext()) {
            if(iterator.next().contains("key_")) iterator.remove();
        }

        lines.add("resourcePacks:[\"f81517ca516363c381528480bb4c5189-New Default Betapack V2.zip\"]");
        lines.add("autoJump:false");
        lines.add("tutorialStep:none");
        lines.add("key_key.attack:-100");
        lines.add("key_key.use:-99");
        lines.add("key_key.forward:17");
        lines.add("key_key.left:30");
        lines.add("key_key.back:31");
        lines.add("key_key.right:32");
        lines.add("key_key.jump:57");
        lines.add("key_key.sneak:42");
        lines.add("key_key.sprint:29");
        lines.add("key_key.drop:16");
        lines.add("key_key.inventory:18");
        lines.add("key_key.chat:20");
        lines.add("key_key.playerlist:15");
        lines.add("key_key.pickItem:-98");
        lines.add("key_key.command:53");
        lines.add("key_key.screenshot:60");
        lines.add("key_key.togglePerspective:63");
        lines.add("key_key.smoothCamera:0");
        lines.add("key_key.fullscreen:87");
        lines.add("key_key.spectatorOutlines:0");
        lines.add("key_key.swapHands:33");
        lines.add("key_key.saveToolbarActivator:46");
        lines.add("key_key.loadToolbarActivator:45");
        lines.add("key_key.advancements:38");
        lines.add("key_key.hotbar .1:2");
        lines.add("key_key.hotbar .2:3");
        lines.add("key_key.hotbar .3:4");
        lines.add("key_key.hotbar .4:5");
        lines.add("key_key.hotbar .5:6");
        lines.add("key_key.hotbar .6:7");
        lines.add("key_key.hotbar .7:8");
        lines.add("key_key.hotbar .8:9");
        lines.add("key_key.hotbar .9:10");
        lines.add("key_of.key.zoom:46");
        lines.add("key_key.betterfoliage.gui:66");
        lines.add("key_key.fart.desc:34");
        lines.add("key_key.minelife.notifications:49");
        lines.add("key_key.minelife.minebay.gui:50");
        lines.add("key_vc.key.moveForward:17");
        lines.add("key_vc.key.moveBack:31");
        lines.add("key_vc.key.moveLeft:30");
        lines.add("key_vc.key.moveRight:32");
        lines.add("key_vc.key.moveUp:57");
        lines.add("key_vc.key.moveDown:45");
        lines.add("key_vc.key.openInventory:34");
        lines.add("key_key.jei.toggleOverlay:24:CONTROL");
        lines.add("key_key.jei.focusSearch:33:CONTROL");
        lines.add("key_key.jei.toggleCheatMode:0");
        lines.add("key_key.jei.showRecipe:36");
        lines.add("key_key.jei.showUses:22");
        lines.add("key_key.jei.recipeBack:14");
        lines.add("key_key.jei.previousPage:201");
        lines.add("key_key.jei.nextPage:209");
        lines.add("key_key.car_forward:17");
        lines.add("key_key.car_back:31");
        lines.add("key_key.car_left:30");
        lines.add("key_key.car_right:32");
        lines.add("key_key.car_gui:23");
        lines.add("key_key.car_start:46");
        lines.add("key_key.car_horn:35");
        lines.add("key_key.center_car:57");
        lines.add("key_key.immersiveengineering.magnetEquip:31");
        lines.add("key_key.immersiveengineering.chemthrowerSwitch:0");
        lines.add("key_Open Backpack:48");
        lines.add("key_Mekanism Item Mode Switch:50");
        lines.add("key_Mekanism Armor Mode Switch:34");
        lines.add("key_Mekanism Feet Mode Switch:35");
        lines.add("key_Mekanism Voice:22");
        lines.add("key_key.minelife.guns.reload:19");
        lines.add("key_key.minelife.guns.modify:44");
        lines.add("key_Speak:47");
        lines.add("key_Gliby's Options Menu:52");
        lines.add("key_WorldEdit Reference:38");

        PrintWriter writer = new PrintWriter(optionsFile, "UTF-8");
        lines.forEach(line -> writer.println(line));
        writer.close();
    }

}
