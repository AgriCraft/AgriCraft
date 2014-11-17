package com.InfinityRaider.AgriCraft.reference;

public final class Reference {
    public static final String MOD_NAME = "AgriCraft";
    public static final String MOD_ID = "AgriCraft";
    public static final String VERSION = "1.7.10-0.0.7";
    public static final String AUTHOR = "InfinityRaider";
    public static boolean debug;
    public static final String CLIENT_PROXY_CLASS = "com.InfinityRaider.AgriCraft.proxy.ClientProxy";
    public static final String SERVER_PROXY_CLASS = "com.InfinityRaider.AgriCraft.proxy.ServerProxy";

    public static void setDebug(boolean value) {
        debug = value;
    }
}
