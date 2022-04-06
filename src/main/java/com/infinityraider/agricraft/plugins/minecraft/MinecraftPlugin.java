package com.infinityraider.agricraft.plugins.minecraft;

import com.google.common.collect.ImmutableList;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.adapter.IAgriAdapterizer;
import com.infinityraider.agricraft.api.v1.genetics.IAgriGeneRegistry;
import com.infinityraider.agricraft.api.v1.genetics.IAgriGenome;
import com.infinityraider.agricraft.api.v1.plant.IJsonPlantCallback;
import com.infinityraider.agricraft.api.v1.plugin.*;
import com.infinityraider.agricraft.content.AgriItemRegistry;
import com.infinityraider.agricraft.reference.Names;
import com.mojang.math.Vector3f;
import net.minecraft.world.entity.animal.*;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Objects;

@SuppressWarnings("unused")
@AgriPlugin(modId = Names.Mods.MINECRAFT, alwaysLoad = true)
public class MinecraftPlugin implements IAgriPlugin {
    public final IJsonPlantCallback.Factory brightness;
    public final IJsonPlantCallback.Factory burn;
    public final IJsonPlantCallback.Factory bushy;
    public final IJsonPlantCallback.Factory experience;
    public final IJsonPlantCallback.Factory fungus;
    public final IJsonPlantCallback.Factory poisoning;
    public final IJsonPlantCallback.Factory redstone;
    public final IJsonPlantCallback.Factory thorns;
    public final IJsonPlantCallback.Factory withering;

    public MinecraftPlugin() {
        this.brightness = JsonPlantCallBackBrightness.getFactory();
        this.burn = JsonPlantCallBackBurn.getFactory();
        this.bushy = JsonPlantCallBackBushy.getFactory();
        this.experience = JsonPlantCallBackExperience.getFactory();
        this.fungus = JsonPlantCallBackFungus.getFactory();
        this.poisoning = JsonPlantCallBackPoisoning.getFactory();
        this.redstone = JsonPlantCallBackRedstone.getFactory();
        this.thorns = JsonPlantCallBackThorns.getFactory();
        this.withering = JsonPlantCallBackWithering.getFactory();
    }

    protected void registerCallBackFactories() {
        Arrays.stream(this.getClass().getDeclaredFields())
                .map(f -> {
                    try {
                        return f.get(this);
                    } catch (Exception e) {
                        return null;
                    }
                })
                .filter(obj -> obj instanceof IJsonPlantCallback.Factory)
                .map(obj -> ((IJsonPlantCallback.Factory) obj).register())
                .filter(Objects::nonNull)
                .map(IJsonPlantCallback.Factory::getId)
                .forEach(id -> AgriCraft.instance.getLogger().info("Registered Json Callback: " + id));
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
    public String getDescription() {
        return "Vanilla Minecraft Integration";
    }

    @Override
    public void onCommonSetupEvent(FMLCommonSetupEvent event) {
        // Register callbacks
        this.registerCallBackFactories();
        // Define compost value
        float compostValue = AgriCraft.instance.getConfig().seedCompostValue();
        if(compostValue > 0) {
            ComposterBlock.COMPOSTABLES.put(AgriItemRegistry.SEED, compostValue);
        }
        // Inject seed into chicken feed
        ChickenBreedItemInjector.inject();
    }

    @Override
    public void registerGenomes(@Nonnull IAgriAdapterizer<IAgriGenome> adapterizer) {
        adapterizer.registerAdapter(new SeedWrapper());
        adapterizer.registerAdapter(new GenomeWrapper());
    }

    @Override
    public void registerGenes(@Nonnull IAgriGeneRegistry geneRegistry) {
        if(AgriCraft.instance.getConfig().enableAnimalAttractingCrops()) {
            geneRegistry.add(new GeneAnimalAttractant("cow",
                    Cow.class, ImmutableList.of("vanilla:wheat_plant"),
                    new Vector3f(1, 1, 1), new Vector3f(0, 0, 0)));
            geneRegistry.add(new GeneAnimalAttractant("pig",
                    Pig.class, ImmutableList.of("vanilla:beet_root_plant", "vanilla:carrot_plant", "vanilla:potato_plant"),
                    new Vector3f(1, 0, 223.0F / 255.0F), new Vector3f(1, 133.0F / 255.0F, 240.0F / 255.0F)));
            geneRegistry.add(new GeneAnimalAttractant("sheep",
                    Sheep.class, ImmutableList.of("vanilla:wheat_plant"),
                    new Vector3f(1, 1, 1), new Vector3f(123.0F / 255.0F, 83.0F / 255.0F, 25.0F / 255.0F)));
            geneRegistry.add(new GeneAnimalAttractant("chicken",
                    Chicken.class, ImmutableList.of("vanilla:melon_plant", "vanilla:pumpkin_plant"),
                    new Vector3f(255.0F / 255.0F, 170.0F / 255.0F, 96.0F / 255.0F), new Vector3f(253.0F / 255.0F, 201.0F / 255.0F, 160.0F / 255.0F)));
            geneRegistry.add(new GeneAnimalAttractant("rabbit",
                    Rabbit.class, ImmutableList.of("vanilla:carrot_plant", "vanilla:dandelion_plant"),
                    new Vector3f(83.0F / 255.0F, 46.0F / 255.0F, 0), new Vector3f(123.0F / 255.0F, 83.0F / 255.0F, 25.0F / 255.0F)));
            geneRegistry.add(new GeneAnimalAttractant("panda",
                    Panda.class, ImmutableList.of("vanilla:bamboo_plant"),
                    new Vector3f(0, 0, 0), new Vector3f(1, 1, 1)));
            geneRegistry.add(new GeneAnimalAttractant("turtle",
                    Turtle.class, ImmutableList.of("vanilla:sea_grass_plant", "vanilla:kelp_plant"),
                    new Vector3f(12.0F / 255.0F, 80.0F / 255.0F, 0), new Vector3f(144.0F / 255.0F, 96.0F / 255.0F, 54.0F / 255.0F)));
        }
    }
}
