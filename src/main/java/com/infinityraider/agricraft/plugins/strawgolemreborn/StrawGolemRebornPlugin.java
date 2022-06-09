package com.infinityraider.agricraft.plugins.strawgolemreborn;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.plugin.AgriPlugin;
import com.infinityraider.agricraft.api.v1.plugin.IAgriPlugin;
import com.infinityraider.agricraft.reference.Names;

@SuppressWarnings("unused")
@AgriPlugin(modId = Names.Mods.STRAW_GOLEM)
public class StrawGolemRebornPlugin implements IAgriPlugin {
    @SuppressWarnings("unused")
    public StrawGolemRebornPlugin() {
        // Register agricraft crops as a crop to harvest
        try {
            StrawGolemRebornCompat.init();
        } catch(Exception e) {
            AgriCraft.instance.getLogger().error("Failed to initialize Straw Golem Reborn Compat, notify the mod author");
            AgriCraft.instance.getLogger().printStackTrace(e);
        }
    }

    @Override
    public boolean isEnabled() {
        return AgriCraft.instance.getConfig().enableStrawGolemRebornCompat();
    }

    @Override
    public String getId() {
        return Names.Mods.STRAW_GOLEM;
    }

    @Override
    public String getDescription() {
        return "Straw Golem Reborn compatibility";
    }
}
