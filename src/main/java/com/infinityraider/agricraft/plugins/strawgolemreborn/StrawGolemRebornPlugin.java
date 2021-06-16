package com.infinityraider.agricraft.plugins.strawgolemreborn;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.plugin.*;
import com.infinityraider.agricraft.reference.Names;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@SuppressWarnings("unused")
@AgriPlugin(modId = Names.Mods.STRAW_GOLEM)
public class StrawGolemRebornPlugin implements IAgriPlugin {
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
        return "Straw Golem Reborn compatibility [WIP]";
    }

    @Override
    public void onCommonSetupEvent(FMLCommonSetupEvent event) {
        // TODO
    }
}
