package com.infinityraider.agricraft.plugins.jei;

import com.infinityraider.agricraft.api.v1.plugin.AgriPlugin;
import com.infinityraider.agricraft.api.v1.plugin.IAgriPlugin;
import com.infinityraider.agricraft.reference.Names;

@AgriPlugin
@SuppressWarnings("unused")
public class JeiPlugin implements IAgriPlugin {
    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getId() {
        return Names.Mods.JEI;
    }

    @Override
    public String getName() {
        return "AgriCraft JEI Integration";
    }
}
