package com.infinityraider.agricraft.plugins.minecraft;

import com.google.common.collect.ImmutableList;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.adapter.IAgriAdapterizer;
import com.infinityraider.agricraft.api.v1.fertilizer.IAgriFertilizer;
import com.infinityraider.agricraft.api.v1.genetics.IAgriGeneRegistry;
import com.infinityraider.agricraft.api.v1.genetics.IAgriGenome;
import com.infinityraider.agricraft.api.v1.plugin.AgriPlugin;
import com.infinityraider.agricraft.api.v1.plugin.IAgriPlugin;
import com.infinityraider.agricraft.impl.v1.plant.JsonPlantCallback;
import net.minecraft.block.ComposterBlock;
import net.minecraft.entity.passive.*;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import javax.annotation.Nonnull;

@AgriPlugin
@SuppressWarnings("unused")
public class MinecraftPlugin implements IAgriPlugin {
    public final JsonPlantCallback experience;
    public final JsonPlantCallback brightness;
    public final JsonPlantCallback redstone;
    public final JsonPlantCallback thorns;
    public final JsonPlantCallback withering;
    public final JsonPlantCallback poisoning;
    public final JsonPlantCallback bushy;

    public MinecraftPlugin() {
        this.experience = JsonPlantCallBackExperience.getInstance();
        this.brightness = JsonPlantCallBackBrightness.getInstance();
        this.redstone = JsonPlantCallBackRedstone.getInstance();
        this.thorns = JsonPlantCallBackThorns.getInstance();
        this.withering = JsonPlantCallBackWithering.getInstance();
        this.poisoning = JsonPlantCallBackPoisoning.getInstance();
        this.bushy = JsonPlantCallBackBushy.getInstance();
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
    public void registerGenomes(@Nonnull IAgriAdapterizer<IAgriGenome> adapterizer) {
        adapterizer.registerAdapter(new SeedWrapper());
        adapterizer.registerAdapter(new GenomeWrapper());
    }

    @Override
    public void registerFertilizers(@Nonnull IAgriAdapterizer<IAgriFertilizer> adapterizer) {
        adapterizer.registerAdapter(BonemealWrapper.INSTANCE);
    }

    @Override
    public void registerGenes(@Nonnull IAgriGeneRegistry geneRegistry) {
        if(AgriCraft.instance.getConfig().enableAnimalAttractingCrops()) {
            geneRegistry.add(new GeneAnimalAttractant("cow",
                    CowEntity.class, ImmutableList.of("vanilla:wheat_plant"),
                    new Vector3f(1, 1, 1), new Vector3f(0, 0, 0)));
            geneRegistry.add(new GeneAnimalAttractant("pig",
                    PigEntity.class, ImmutableList.of("vanilla:beet_root_plant", "vanilla:carrot_plant", "vanilla:potato_plant"),
                    new Vector3f(1, 0, 223.0F / 255.0F), new Vector3f(1, 133.0F / 255.0F, 240.0F / 255.0F)));
            geneRegistry.add(new GeneAnimalAttractant("sheep",
                    SheepEntity.class, ImmutableList.of("vanilla:wheat_plant"),
                    new Vector3f(1, 1, 1), new Vector3f(123.0F / 255.0F, 83.0F / 255.0F, 25.0F / 255.0F)));
            geneRegistry.add(new GeneAnimalAttractant("chicken",
                    ChickenEntity.class, ImmutableList.of("vanilla:melon_plant", "vanilla:pumpkin_plant"),
                    new Vector3f(255.0F / 255.0F, 170.0F / 255.0F, 96.0F / 255.0F), new Vector3f(253.0F / 255.0F, 201.0F / 255.0F, 160.0F / 255.0F)));
            geneRegistry.add(new GeneAnimalAttractant("rabbit",
                    RabbitEntity.class, ImmutableList.of("vanilla:carrot_plant", "vanilla:dandelion_plant"),
                    new Vector3f(83.0F / 255.0F, 46.0F / 255.0F, 0), new Vector3f(123.0F / 255.0F, 83.0F / 255.0F, 25.0F / 255.0F)));
            geneRegistry.add(new GeneAnimalAttractant("panda",
                    PandaEntity.class, ImmutableList.of("vanilla:bamboo_plant"),
                    new Vector3f(0, 0, 0), new Vector3f(1, 1, 1)));
            geneRegistry.add(new GeneAnimalAttractant("turtle",
                    TurtleEntity.class, ImmutableList.of("vanilla:sea_grass_plant", "vanilla:kelp_plant"),
                    new Vector3f(12.0F / 255.0F, 80.0F / 255.0F, 0), new Vector3f(144.0F / 255.0F, 96.0F / 255.0F, 54.0F / 255.0F)));
        }
    }
}
