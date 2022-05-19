package com.infinityraider.agricraft.reference;

public interface Reference {

    String MOD_NAME = /*^${mod.name}^*/ "AgriCraft";
    String MOD_ID = /*^${mod.id}^*/ "agricraft";
    String AUTHOR = /*^${mod.author}^*/ "InfinityRaider";

    String VER_MAJOR = /*^${mod.version_major}^*/ "3";
    String VER_MINOR = /*^${mod.version_minor}^*/ "1";
    String VER_PATCH = /*^${mod.version_patch}^*/ "0";
    String MOD_VERSION = /*^${mod.version}^*/ VER_MAJOR + "." + VER_MINOR + "." + VER_PATCH;;
    String VERSION = /*^${mod.version_minecraft}-${mod.version}^*/ "1.18.2-" + MOD_VERSION;

    String VERSION_INFLIB = /*^${mod.version_inflib}^*/ "1.18.2-2.1.0";
    String VERSION_FORGE = /*^${mod.version_forge}^*/ "0.0-0.0.0";

}
