package com.infinityraider.agricraft.plugins.botania;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import com.infinityraider.agricraft.api.v1.plugin.*;
import com.infinityraider.agricraft.reference.Names;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import javax.annotation.Nonnull;

import static vazkii.botania.api.BotaniaForgeCapabilities.HORN_HARVEST;

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
        // Register mana callback
        JsonPlantCallBackManaConsumer.getFactory().register();
    }

    @Override
    public <T> LazyOptional<T> getCropCapability(@Nonnull Capability<T> cap, IAgriCrop crop) {
        if(cap == HORN_HARVEST) {
            return LazyOptional.of(() -> AgriHornHarvestable.INSTANCE).cast();
        } else {
            return IAgriPlugin.super.getCropCapability(cap, crop);
        }
    }
}
