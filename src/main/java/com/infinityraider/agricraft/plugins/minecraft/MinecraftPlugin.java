package com.infinityraider.agricraft.plugins.minecraft;

import com.infinityraider.agricraft.api.v1.adapter.IAgriAdapterizer;
import com.infinityraider.agricraft.api.v1.fertilizer.IAgriFertilizer;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.agricraft.api.v1.plugin.AgriPlugin;
import com.infinityraider.agricraft.api.v1.plugin.IAgriPlugin;
import com.infinityraider.agricraft.api.v1.seed.AgriSeed;

import javax.annotation.Nonnull;

@AgriPlugin
@SuppressWarnings("unused")
public class MinecraftPlugin implements IAgriPlugin {
    public MinecraftPlugin() {}

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getId() {
        return "vanilla";
    }

    @Override
    public String getName() {
        return "Vanilla Minecraft Integration";
    }

    @Override
    public void registerSeeds(IAgriAdapterizer<AgriSeed> adapterizer) {
        adapterizer.registerAdapter(new SeedWrapper());
    }

    @Override
    public void registerSeedSubstitutes(@Nonnull IAgriAdapterizer<IAgriPlant> adapterizer) {
        adapterizer.registerAdapter(new SeedSubstituteWrapper());
    }

    @Override
    public void registerFertilizers(IAgriAdapterizer<IAgriFertilizer> adapterizer) {
        adapterizer.registerAdapter(BonemealWrapper.INSTANCE);
    }

}
