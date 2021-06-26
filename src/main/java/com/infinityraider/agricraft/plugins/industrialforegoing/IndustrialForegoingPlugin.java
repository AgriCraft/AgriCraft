package com.infinityraider.agricraft.plugins.industrialforegoing;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.plugin.*;
import com.infinityraider.agricraft.reference.Names;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@SuppressWarnings("unused")
@AgriPlugin(modId = Names.Mods.INDUSTRIAL_FOREGOING)
public class IndustrialForegoingPlugin implements IAgriPlugin {
    @Override
    public void onCommonSetupEvent(FMLCommonSetupEvent event) {
        IndustrialForegoingCompat.execute();
    }

    @Override
    public boolean isEnabled() {
        return AgriCraft.instance.getConfig().enableIndustrialForegoingCompat();
    }

    @Override
    public String getId() {
        return Names.Mods.INDUSTRIAL_FOREGOING;
    }

    @Override
    public String getDescription() {
        return "Industrial Foregoing compatibility";
    }
}
