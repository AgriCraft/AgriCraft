package com.infinityraider.agricraft.plugins.botanypots;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.plugin.*;
import com.infinityraider.agricraft.reference.Names;
import com.infinityraider.infinitylib.crafting.IInfRecipeSerializer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import javax.annotation.Nullable;

@SuppressWarnings("unused")
@AgriPlugin(modId = Names.Mods.BOTANY_POTS)
public class BotanyPotsPlugin implements IAgriPlugin {

    @Override
    public boolean isEnabled() {
        return AgriCraft.instance.getConfig().enableBotanyPotsCompat();
    }

    @Override
    public String getId() {
        return Names.Mods.BOTANY_POTS;
    }

    @Override
    public String getDescription() {
        return "Botany Pots compatibility";
    }

    @Override
    public void onCommonSetupEvent(FMLCommonSetupEvent event) {
        BotanyCropsCompat.registerEventHandler();
        BotanyCropsCompat.registerCapability();
    }

    @Nullable
    public static IInfRecipeSerializer getAgriCropInfoSerializer() {
        if (ModList.get().isLoaded(Names.Mods.BOTANY_POTS) && AgriCraft.instance.getConfig().enableBotanyPotsCompat()) {
            return BotanyCropsCompat.getAgriCropInfoSerializer();
        } else {
            return null;
        }
    }
}
