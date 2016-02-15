package com.InfinityRaider.AgriCraft.reference;

/**
 * The version should be tracked in the base mod class.
 * 
 * @deprecated
 */
@Deprecated
public final class Reference {
    public static final String MOD_NAME = "${mod.name}";
    public static final String MOD_ID = "${mod.id}";

    public static final String VER_MAJOR = "${mod.version_major}";
    public static final String VER_MINOR = "${mod.version_minor}";
    public static final String VER_REVIS = "${mod.version_patch}";
    public static final String MOD_VERSION = "${mod.version}";
    public static final String VERSION = "${mod.version_minecraft}-${mod.version}";

    public static final String AUTHOR = "${mod.author}";
    public static final String CLIENT_PROXY_CLASS = "${mod.group}.${mod.author}.${mod.id}.proxy.ClientProxy";
    public static final String SERVER_PROXY_CLASS = "${mod.group}.${mod.author}.${mod.id}.proxy.ServerProxy";
    public static final String GUI_FACTORY_CLASS = "${mod.group}.${mod.author}.${mod.id}.gui.GuiFactory";
}
