package com.infinityraider.agricraft.reference;

/**
 * The version should be tracked in the base mod class.
 * Just have to figure out where (and when) to move it...
 *
 * @deprecated
 */
@Deprecated
public interface Reference {

	String MOD_NAME = "${mod.name}";
	String MOD_ID = "${mod.id}";
	String AUTHOR = "${mod.author}";

	String VER_MAJOR = "${mod.version_major}";
	String VER_MINOR = "${mod.version_minor}";
	String VER_REVIS = "${mod.version_patch}";
	String MOD_VERSION = "${mod.version}";
	String VERSION = "${mod.version_minecraft}-${mod.version}";

	String CLIENT_PROXY_CLASS = "${mod.group}.${mod.id}.proxy.ClientProxy";
	String SERVER_PROXY_CLASS = "${mod.group}.${mod.id}.proxy.ServerProxy";
	String GUI_FACTORY_CLASS = "${mod.group}.${mod.id}.gui.GuiFactory";

}
