package com.minelife.gun.server;

import com.minelife.CommonProxy;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.common.MinecraftForge;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class ServerProxy extends CommonProxy {

//    public static int viewDistance = 0;

    public ServerProxy() {
//        try (Stream<String> stream = Files.lines(Paths.get(new File(System.getProperty("user.dir"), "server.properties").getAbsolutePath()))) {
//
//            String viewDistanceLine = stream.filter(line -> line.startsWith("view-distance")).findFirst().orElse(null);
//
//            if(viewDistanceLine != null) {
//                viewDistance = Integer.parseInt(viewDistanceLine.split("=")[1]);
//            }
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }

}
