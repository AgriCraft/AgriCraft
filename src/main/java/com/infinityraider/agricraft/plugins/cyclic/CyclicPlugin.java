package com.infinityraider.agricraft.plugins.cyclic;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.plugin.*;
import com.infinityraider.agricraft.reference.Names;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@SuppressWarnings("unused")
@AgriPlugin(modId = Names.Mods.CYCLIC)
public class CyclicPlugin implements IAgriPlugin {
    @Override
    public boolean isEnabled() {
        return AgriCraft.instance.getConfig().enableCyclicCompat();
    }

    @Override
    public String getId() {
        return Names.Mods.CYCLIC;
    }

    @Override
    public String getDescription() {
        return "Cyclic compatibility";
    }

    @Override
    public void onCommonSetupEvent(FMLCommonSetupEvent event) {
        CyclicCompat.registerHarvesterOverride();
    }
}
