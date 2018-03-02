package com.minelife.tutorial;

import com.google.common.collect.Lists;
import com.minelife.MLMod;
import com.minelife.MLProxy;
import com.minelife.util.NumberConversions;

import java.io.File;
import java.util.List;
import java.util.Objects;

public class ModTutorial extends MLMod {

    @Override
    public Class<? extends MLProxy> getClientProxyClass() {
        return com.minelife.tutorial.ClientProxy.class;
    }

    public static List<File> getPages(File currentSection) {
        List<File> files = Lists.newArrayList();
        for (File file : Objects.requireNonNull(currentSection.listFiles())) {
            if(file.getName().endsWith(".page") && file.isFile()) {
                if(NumberConversions.isInt(file.getName().replaceAll(".page", "")))
                    files.add(file);
            }
        }
        return files;
    }

    public static List<File> getSections(File currentSection) {
        List<File> folders = Lists.newArrayList();
        for (File file : Objects.requireNonNull(currentSection.listFiles())) {
            if(file.getName().endsWith(".section") && file.isDirectory()) {
                folders.add(file);
            }
        }
        return folders;
    }

}
