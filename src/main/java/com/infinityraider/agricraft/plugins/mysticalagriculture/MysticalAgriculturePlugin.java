package com.infinityraider.agricraft.plugins.mysticalagriculture;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.plugin.AgriPlugin;
import com.infinityraider.agricraft.api.v1.plugin.IAgriPlugin;
import com.infinityraider.agricraft.reference.Names;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@SuppressWarnings("unused")
@AgriPlugin(modId = Names.Mods.MYSTICAL_AGRICULTURE)
public class MysticalAgriculturePlugin implements IAgriPlugin {
    @Override
    public boolean isEnabled() {
        return AgriCraft.instance.getConfig().enableMysticalAgricultureCompat();
    }

    @Override
    public String getId() {
        return Names.Mods.MYSTICAL_AGRICULTURE;
    }

    @Override
    public String getDescription() {
        return "Mystical Agriculture compatibility";
    }

    @Override
    public void onClientSetupEvent(FMLClientSetupEvent event) {
        MysticalAgricultureCompatClient.init();
    }
}
