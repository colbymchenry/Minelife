package com.minelife.realestate;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public abstract class Packet implements IMessage {

    protected Packet() { }

    public abstract Side sideOfHandling();

    public abstract void handle(MessageContext ctx);

    public static class Handler implements IMessageHandler<Packet, IMessage> {

        @Override
        public IMessage onMessage(Packet packet, MessageContext ctx) {

            packet.handle(ctx);

            return null;

        }

    }

    static void registerPackets() {

        try {
            getClasses(Packet.class.getPackage().getName()).forEach(aClass -> {
                if (aClass.getSuperclass().equals(Packet.class) && !aClass.equals(Packet.class)) {
                    try {
                        Packet packet = (Packet) aClass.newInstance();
                        ModRealEstate.registerPacket(Packet.Handler.class, packet.getClass(), packet.sideOfHandling());
                        System.out.println("Registered " + packet.getClass().getSimpleName() + "!");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Scans all classes accessible from the context class loader which belong to the given package and subpackages.
     *
     * @param packageName The base package
     * @return The classes
     */
    private static ArrayList<Class> getClasses(String packageName) throws ClassNotFoundException, IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        assert classLoader != null;
        String path = packageName.replace('.', File.separatorChar);
        Enumeration<URL> resources = classLoader.getResources(path);
        List<File> dirs = new ArrayList<>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            dirs.add(new File(resource.getFile()));
        }
        ArrayList<Class> classes = new ArrayList<>();
        for (File directory : dirs) {
            classes.addAll(findClasses(directory, packageName));
        }
        return classes;
    }

    /**
     * Recursive method used to find all classes in a given directory and subdirs.
     *
     * @param directory   The base directory
     * @param packageName The package name for classes found inside the base directory
     * @return The classes
     */
    private static List<Class> findClasses(File directory, String packageName) throws ClassNotFoundException {
        List<Class> classes = new ArrayList<>();
        if (!directory.exists()) return classes;
        File[] files = directory.listFiles();
        if (files == null) return classes;
        for (File file : files) {
            if (file.isDirectory() && !file.getName().contains(".")) {
                classes.addAll(findClasses(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                try {
                    Class aClass = Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6));
                    if (aClass != null) classes.add(aClass);
                } catch (Error | Exception ignored) {
                }
            }
        }
        return classes;
    }

}
