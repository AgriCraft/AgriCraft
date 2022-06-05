package com.infinityraider.agricraft.plugins.industrialforegoing;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.plugin.AgriPlugin;
import com.infinityraider.agricraft.api.v1.plugin.IAgriPlugin;
import com.infinityraider.agricraft.reference.Names;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@AgriPlugin(modId = Names.Mods.INDUSTRIAL_FOREGOING)
public class IndustrialForegoingPlugin implements IAgriPlugin {
    @SuppressWarnings("unused")
    public IndustrialForegoingPlugin() {
        FMLJavaModLoadingContext.get().getModEventBus().register(IndustrialForegoingCompat.class);
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
