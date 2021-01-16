package com.infinityraider.agricraft.plugins.agricraft;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.plugin.AgriPlugin;
import com.infinityraider.agricraft.api.v1.plugin.IAgriPlugin;

@AgriPlugin
@SuppressWarnings("unused")
public class AgriCraftPlugin implements IAgriPlugin {
    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getId() {
        return AgriCraft.instance.getModId();
    }

    @Override
    public String getName() {
        return "AgriCraft Content";
    }
}
