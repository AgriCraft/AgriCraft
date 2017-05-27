package com.infinityraider.agricraft.reference;

/**
 * This class is fine.
 *
 */
public interface Reference {

    String MOD_NAME = /*^${mod.name}^*/ "AgriCraft";
    String MOD_ID = /*^${mod.id}^*/ "agricraft";
    String AUTHOR = /*^${mod.author}^*/ "Captain Nemo";

    String VER_MAJOR = /*^${mod.version_major}^*/ "0";
    String VER_MINOR = /*^${mod.version_minor}^*/ "0";
    String VER_PATCH = /*^${mod.version_patch}^*/ "0";
    String MOD_VERSION = /*^${mod.version}^*/ "0.0.0";
    String VERSION = /*^${mod.version_minecraft}-${mod.version}^*/ "0.0-0.0.0";

    String VERSION_INFLIB = /*^${mod.version_inflib}^*/ "0.0-0.0.0";
    String VERSION_FORGE = /*^${mod.version_forge}^*/ "0.0-0.0.0";

    String CLIENT_PROXY_CLASS = "com.infinityraider.agricraft.proxy.ClientProxy";
    String SERVER_PROXY_CLASS = "com.infinityraider.agricraft.proxy.ServerProxy";
    String GUI_FACTORY_CLASS = "com.infinityraider.agricraft.gui.GuiFactory";

    String UPDATE_URL = /*^${mod.update_url}^*/ "https://www.youtube.com/watch?v=dQw4w9WgXcQ";

}
