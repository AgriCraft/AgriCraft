package com.infinityraider.agricraft.plugins.botania;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.plugin.*;
import com.infinityraider.agricraft.reference.Names;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@SuppressWarnings("unused")
@AgriPlugin(modId = Names.Mods.BOTANIA)
public class BotaniaPlugin implements IAgriPlugin {
    @Override
    public boolean isEnabled() {
        return AgriCraft.instance.getConfig().enableBotaniaCompat();
    }

    @Override
    public String getId() {
        return Names.Mods.BOTANIA;
    }

    @Override
    public String getDescription() {
        return "Botania compatibility";
    }

    @Override
    public void onCommonSetupEvent(FMLCommonSetupEvent event) {
        BotaniaCompat.registerHarvestables();
    }
}
