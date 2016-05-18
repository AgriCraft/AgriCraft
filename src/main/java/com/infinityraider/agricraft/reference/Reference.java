package com.infinityraider.agricraft.reference;

/**
 * This class is fine.
 *
 */
public interface Reference {

	String MOD_NAME = "${mod.name}";
	String MOD_ID = "agricraft";
	String AUTHOR = "${mod.author}";

	String VER_MAJOR = "${mod.version_major}";
	String VER_MINOR = "${mod.version_minor}";
	String VER_REVIS = "${mod.version_patch}";
	String MOD_VERSION = "${mod.version}";
	String VERSION = "${mod.version_minecraft}-${mod.version}";

	String CLIENT_PROXY_CLASS = "com.infinityraider.agricraft.proxy.ClientProxy";
	String SERVER_PROXY_CLASS = "com.infinityraider.agricraft.proxy.ServerProxy";
	String GUI_FACTORY_CLASS = "com.infinityraider.agricraft.gui.GuiFactory";
	
	String UPDATE_URL = "${mod.update_url}";

}
