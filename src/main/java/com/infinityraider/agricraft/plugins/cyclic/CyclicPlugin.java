package com.infinityraider.agricraft.plugins.cyclic;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.plugin.AgriPlugin;
import com.infinityraider.agricraft.api.v1.plugin.IAgriPlugin;
import com.infinityraider.agricraft.reference.Names;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@AgriPlugin
@SuppressWarnings("unused")
public class CyclicPlugin implements IAgriPlugin {
    @Override
    public boolean isEnabled() {
        return ModList.get().isLoaded(this.getId());
    }

    @Override
    public String getId() {
        return Names.Mods.CYCLIC;
    }

    @Override
    public String getName() {
        return this.getId();
    }

    @Override
    public void onCommonSetupEvent(FMLCommonSetupEvent event) {
        if(this.isEnabled() && AgriCraft.instance.getConfig().enableCreateCompat()) {
            CyclicCompat.registerHarvesterOverride();
        }
    }
}
