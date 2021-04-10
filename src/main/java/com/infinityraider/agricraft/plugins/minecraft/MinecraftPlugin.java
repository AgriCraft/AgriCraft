package com.infinityraider.agricraft.plugins.minecraft;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.adapter.IAgriAdapterizer;
import com.infinityraider.agricraft.api.v1.fertilizer.IAgriFertilizer;
import com.infinityraider.agricraft.api.v1.plugin.AgriPlugin;
import com.infinityraider.agricraft.api.v1.plugin.IAgriPlugin;
import com.infinityraider.agricraft.api.v1.seed.AgriSeed;
import com.infinityraider.agricraft.impl.v1.plant.JsonPlantCallback;
import net.minecraft.block.ComposterBlock;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import javax.annotation.Nonnull;

@AgriPlugin
@SuppressWarnings("unused")
public class MinecraftPlugin implements IAgriPlugin {
    public final JsonPlantCallback thorns;
    public final JsonPlantCallback brightness;

    public MinecraftPlugin() {
        this.thorns = JsonPlantCallBackThorns.getInstance();
        this.brightness = JsonPlantCallBackBrightness.getInstance();
    }

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
    public void onCommonSetupEvent(FMLCommonSetupEvent event) {
        float compostValue = AgriCraft.instance.getConfig().seedCompostValue();
        if(compostValue > 0) {
            ComposterBlock.CHANCES.put(AgriCraft.instance.getModItemRegistry().seed, compostValue);
        }
    }

    @Override
    public void registerSeeds(@Nonnull IAgriAdapterizer<AgriSeed> adapterizer) {
        adapterizer.registerAdapter(new SeedWrapper());
    }

    @Override
    public void registerFertilizers(@Nonnull IAgriAdapterizer<IAgriFertilizer> adapterizer) {
        adapterizer.registerAdapter(BonemealWrapper.INSTANCE);
    }

}
