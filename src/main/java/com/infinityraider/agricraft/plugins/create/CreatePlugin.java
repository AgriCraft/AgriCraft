package com.infinityraider.agricraft.plugins.create;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.plugin.*;
import com.infinityraider.agricraft.reference.Names;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@SuppressWarnings("unused")
@AgriPlugin(modId = Names.Mods.CREATE)
public class CreatePlugin implements IAgriPlugin {
    @Override
    public boolean isEnabled() {
        return AgriCraft.instance.getConfig().enableCreateCompat();
    }

    @Override
    public String getId() {
        return Names.Mods.CREATE;
    }

    @Override
    public String getDescription() {
        return "Create compatibility";
    }

    @Override
    public void onCommonSetupEvent(FMLCommonSetupEvent event) {
        CreateCompat.injectAgriHarvesterMovementBehaviour();
    }
}
